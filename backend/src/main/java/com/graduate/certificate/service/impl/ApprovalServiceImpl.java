package com.graduate.certificate.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduate.certificate.common.constant.ApplicationStatusConstant;
import com.graduate.certificate.common.constant.ApprovalResultConstant;
import com.graduate.certificate.common.constant.UserTypeConstant;
import com.graduate.certificate.common.context.UserContextHolder;
import com.graduate.certificate.common.exception.BusinessException;
import com.graduate.certificate.common.result.ResultCode;
import com.graduate.certificate.dto.approval.ApprovalRequest;
import com.graduate.certificate.entity.ApprovalRecord;
import com.graduate.certificate.entity.CertificateApplication;
import com.graduate.certificate.entity.CertificateTemplate;
import com.graduate.certificate.entity.StudentInfo;
import com.graduate.certificate.entity.TeacherInfo;
import com.graduate.certificate.mapper.ApprovalRecordMapper;
import com.graduate.certificate.mapper.CertificateApplicationMapper;
import com.graduate.certificate.mapper.CertificateTemplateMapper;
import com.graduate.certificate.mapper.StudentInfoMapper;
import com.graduate.certificate.mapper.TeacherInfoMapper;
import com.graduate.certificate.service.ApprovalService;
import com.graduate.certificate.util.PrimaryKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批服务实现类
 * 
 * 审批逻辑：
 * 1. 简单证明（approval_level=1）：只需导师审批
 * 2. 复杂证明（approval_level>=2）：
 *    - 第一级：导师审批
 *    - 第二级及以上：同学院、同系别、审批级别>=当前级别的老师都可以审批
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final CertificateApplicationMapper applicationMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final TeacherInfoMapper teacherInfoMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final CertificateTemplateMapper templateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processApproval(ApprovalRequest request) {
        // 获取当前教师信息
        String teacherId = UserContextHolder.getTeacherId();
        String userId = UserContextHolder.getUserId();
        Integer userType = UserContextHolder.getUserType();

        if (userType == null || userType != UserTypeConstant.TEACHER) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED.getCode(), "只有教师才能审批");
        }

        // 查询申请
        CertificateApplication application = applicationMapper.selectById(request.getPkCa());
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        // 查询教师信息
        TeacherInfo teacher = teacherInfoMapper.selectById(teacherId);
        if (teacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }

        // 验证审批权限
        validateApprovalPermission(application, teacher);

        // 创建审批记录
        ApprovalRecord record = createApprovalRecord(application, teacher, userId, request);
        approvalRecordMapper.insert(record);

        // 根据审批结果更新申请状态
        if (ApprovalResultConstant.APPROVED.equals(request.getApprovalResult())) {
            handleApproved(application, teacher);
        } else if (ApprovalResultConstant.REJECTED.equals(request.getApprovalResult())) {
            handleRejected(application, request.getApprovalOpinion());
        } else if (ApprovalResultConstant.RETURNED.equals(request.getApprovalResult())) {
            handleReturned(application);
        }

        log.info("审批处理成功: pkCa={}, result={}, teacherId={}", request.getPkCa(), request.getApprovalResult(), teacherId);
    }

    /**
     * 验证审批权限
     * 规则：
     * 1. 第一级审批：必须是指定的导师（pkTeacher）
     * 2. 第二级及以上：必须是当前级别的审批人（approval_level 等于 currentLevel）
     */
    private void validateApprovalPermission(CertificateApplication application, TeacherInfo teacher) {
        int currentLevel = application.getCurrentApprovalLevel();
        
        log.info("验证审批权限: pkCa={}, currentLevel={}, teacherId={}, teacherApprovalLevel={}, teacherName={}",
            application.getPkCa(), currentLevel, teacher.getPkTeacher(), 
            teacher.getApprovalLevel(), teacher.getName());
        
        // 第一级审批：必须是指定的审批人
        if (currentLevel == 1) {
            if (!teacher.getPkTeacher().equals(application.getPkTeacher())) {
                throw new BusinessException(ResultCode.APPROVAL_NO_PERMISSION.getCode(), 
                    "第一级审批必须由指定的导师完成");
            }
            log.info("第一级审批权限验证通过");
            return;
        }

        // 第二级及以上：验证学院、系别、审批级别
        StudentInfo student = studentInfoMapper.selectById(application.getPkStudent());
        if (student == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }

        // 验证学院是否一致
        if (!StringUtils.hasText(student.getPkCollege()) || 
            !student.getPkCollege().equals(teacher.getPkCollege())) {
            throw new BusinessException(ResultCode.APPROVAL_NO_PERMISSION.getCode(), 
                "只能审批本学院的申请");
        }

        // 验证系别是否一致（如果学生有系别）
        if (StringUtils.hasText(student.getPkDepartment()) && 
            !student.getPkDepartment().equals(teacher.getPkDepartment())) {
            throw new BusinessException(ResultCode.APPROVAL_NO_PERMISSION.getCode(), 
                "只能审批本系的申请");
        }

        // 关键修改：审批级别必须等于当前级别，不能是大于
        if (teacher.getApprovalLevel() == null || teacher.getApprovalLevel() != currentLevel) {
            throw new BusinessException(ResultCode.APPROVAL_NO_PERMISSION.getCode(), 
                String.format("您的审批级别为%d，不能审批第%d级的申请", 
                    teacher.getApprovalLevel(), currentLevel));
        }

        // 验证是否有审批权限
        if (teacher.getCanApprove() == null || teacher.getCanApprove() == 0) {
            throw new BusinessException(ResultCode.APPROVAL_NO_PERMISSION.getCode(), 
                "您没有审批权限");
        }
        
        log.info("第{}\u7ea7审批权限验证通过", currentLevel);
    }

    /**
     * 创建审批记录
     */
    private ApprovalRecord createApprovalRecord(CertificateApplication application, 
                                                TeacherInfo teacher, 
                                                String userId, 
                                                ApprovalRequest request) {
        ApprovalRecord record = new ApprovalRecord();
        record.setPkAr(PrimaryKeyGenerator.generateApprovalRecordKey());
        record.setPkCa(application.getPkCa());
        record.setApplicationNo(application.getApplicationNo());
        record.setPkTeacher(teacher.getPkTeacher());
        record.setPkUser(userId);
        record.setApproverName(teacher.getName());
        record.setApprovalLevel(application.getCurrentApprovalLevel());
        record.setApprovalResult(request.getApprovalResult());
        record.setApprovalOpinion(request.getApprovalOpinion());
        record.setApprovalTime(LocalDateTime.now());
        record.setAttachmentUrl(request.getAttachmentUrl());
        
        // 计算审批耗时
        long minutes = Duration.between(application.getCreateTime(), LocalDateTime.now()).toMinutes();
        record.setTimeCost((int) minutes);

        return record;
    }

    /**
     * 处理审批通过
     * 逻辑：
     * 1. 变更为单级直接审批逻辑
     * 2. 一旦当前级别教师审批通过，申请即标记为最终已通过
     */
    private void handleApproved(CertificateApplication application, TeacherInfo currentTeacher) {
        log.info("审批通过处理: pkCa={}, 当前级别={}, 审批教师={}",
            application.getPkCa(), application.getCurrentApprovalLevel(), currentTeacher.getName());

        // 单级直接审批：审批即通过
        application.setStatus(ApplicationStatusConstant.APPROVED);
        application.setCompleteTime(LocalDateTime.now());
        application.setPkTeacher(null);
        
        applicationMapper.updateById(application);
    }

    /**
     * 处理审批拒绝
     */
    private void handleRejected(CertificateApplication application, String reason) {
        application.setStatus(ApplicationStatusConstant.REJECTED);
        application.setRejectReason(reason);
        application.setCompleteTime(LocalDateTime.now());
        application.setPkTeacher(null);
        applicationMapper.updateById(application);
    }

    /**
     * 处理审批退回
     * 逻辑：变更为退回到当前级别的初始状态，不再逐级退回
     */
    private void handleReturned(CertificateApplication application) {
        log.info("审批退回处理: pkCa={}, 当前级别={}", application.getPkCa(), application.getCurrentApprovalLevel());
        
        application.setStatus(ApplicationStatusConstant.PENDING);
        // 如果是1级审批，保留/恢复导师指定；2级及以上保持pkTeacher为空
        if (application.getCurrentApprovalLevel() == 1) {
            StudentInfo student = studentInfoMapper.selectById(application.getPkStudent());
            if (student != null) {
                application.setPkTeacher(student.getPkTeacher());
            }
        } else {
            application.setPkTeacher(null);
        }
        applicationMapper.updateById(application);
    }

    /**
     * 从模板中获取审批级别
     * approvalFlow已变更为直接定义审批等级（如 "1", "2", "3"）
     */
    private int getMaxApprovalLevel(CertificateTemplate template) {
        if (!StringUtils.hasText(template.getApprovalFlow())) {
            return 1; // 默认一级
        }

        try {
            // 尝试直接解析为数字
            return Integer.parseInt(template.getApprovalFlow().trim());
        } catch (NumberFormatException e) {
            log.error("解析审批流程配置等级失败: {}, 默认使用1级", template.getApprovalFlow());
            return 1;
        }
    }
}
