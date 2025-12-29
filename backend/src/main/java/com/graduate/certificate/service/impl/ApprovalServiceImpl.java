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
     * 2. 第二级及以上：同学院、同系别、审批级别足够的老师都可以审批
     */
    private void validateApprovalPermission(CertificateApplication application, TeacherInfo teacher) {
        int currentLevel = application.getCurrentApprovalLevel();
        
        // 第一级审批：必须是指定的审批人
        if (currentLevel == 1) {
            if (!teacher.getPkTeacher().equals(application.getPkTeacher())) {
                throw new BusinessException(ResultCode.APPROVAL_NO_PERMISSION.getCode(), 
                    "第一级审批必须由指定的导师完成");
            }
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

        // 验证审批级别
        if (teacher.getApprovalLevel() == null || teacher.getApprovalLevel() < currentLevel) {
            throw new BusinessException(ResultCode.APPROVAL_NO_PERMISSION.getCode(), 
                "审批级别不足，无法审批");
        }

        // 验证是否有审批权限
        if (teacher.getCanApprove() == null || teacher.getCanApprove() == 0) {
            throw new BusinessException(ResultCode.APPROVAL_NO_PERMISSION.getCode(), 
                "您没有审批权限");
        }
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
     * 1. 获取模板的审批流程配置
     * 2. 判断是否还有下一级审批
     * 3. 如果有下一级，设置状态为审批中，清空pkTeacher（让符合条件的老师都能看到）
     * 4. 如果没有下一级，设置状态为已通过
     */
    private void handleApproved(CertificateApplication application, TeacherInfo currentTeacher) {
        // 查询模板获取审批流程
        CertificateTemplate template = templateMapper.selectById(application.getPkCt());
        if (template == null) {
            throw new BusinessException(ResultCode.TEMPLATE_NOT_FOUND);
        }

        int maxLevel = getMaxApprovalLevel(template);
        int currentLevel = application.getCurrentApprovalLevel();

        if (currentLevel >= maxLevel) {
            // 已经是最后一级，审批通过
            application.setStatus(ApplicationStatusConstant.APPROVED);
            application.setCompleteTime(LocalDateTime.now());
            application.setPkTeacher(null);
        } else {
            // 进入下一级审批
            application.setStatus(ApplicationStatusConstant.IN_PROGRESS);
            application.setCurrentApprovalLevel(currentLevel + 1);
            // 第二级及以上清空pkTeacher，让符合条件的老师都能审批
            application.setPkTeacher(null);
        }
        
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
     */
    private void handleReturned(CertificateApplication application) {
        if (application.getCurrentApprovalLevel() > 1) {
            application.setCurrentApprovalLevel(application.getCurrentApprovalLevel() - 1);
        }
        application.setStatus(ApplicationStatusConstant.PENDING);
        // 退回到第一级时，恢复导师
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
     * 从模板中获取最大审批级别
     * approvalFlow格式: {"levels":[{"level":1,"name":"导师审批"},{"level":2,"name":"系主任审批"}]}
     */
    private int getMaxApprovalLevel(CertificateTemplate template) {
        if (!StringUtils.hasText(template.getApprovalFlow())) {
            return 1; // 默认只有一级
        }

        try {
            JSONObject flowJson = JSON.parseObject(template.getApprovalFlow());
            JSONArray levels = flowJson.getJSONArray("levels");
            if (levels == null || levels.isEmpty()) {
                return 1;
            }
            return levels.size();
        } catch (Exception e) {
            log.error("解析审批流程配置失败: {}", template.getApprovalFlow(), e);
            return 1;
        }
    }
}
