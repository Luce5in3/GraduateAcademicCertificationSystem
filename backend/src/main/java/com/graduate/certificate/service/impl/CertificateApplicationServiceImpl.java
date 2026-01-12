package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduate.certificate.common.constant.ApplicationStatusConstant;
import com.graduate.certificate.common.constant.UserTypeConstant;
import com.graduate.certificate.common.context.UserContextHolder;
import com.graduate.certificate.common.exception.BusinessException;
import com.graduate.certificate.common.result.ResultCode;
import com.graduate.certificate.dto.application.ApplicationRequest;
import com.graduate.certificate.dto.application.ApplicationResponse;
import com.graduate.certificate.entity.CertificateApplication;
import com.graduate.certificate.entity.CertificateTemplate;
import com.graduate.certificate.entity.StudentInfo;
import com.graduate.certificate.entity.TeacherInfo;
import com.graduate.certificate.mapper.CertificateApplicationMapper;
import com.graduate.certificate.mapper.CertificateTemplateMapper;
import com.graduate.certificate.mapper.StudentInfoMapper;
import com.graduate.certificate.mapper.TeacherInfoMapper;
import com.graduate.certificate.service.CertificateApplicationService;
import com.graduate.certificate.util.PrimaryKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 证明申请服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateApplicationServiceImpl implements CertificateApplicationService {

    private final CertificateApplicationMapper applicationMapper;
    private final CertificateTemplateMapper templateMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final TeacherInfoMapper teacherInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplicationResponse submitApplication(ApplicationRequest request) {
        // 获取当前学生信息
        String studentId = UserContextHolder.getStudentId();
        String userId = UserContextHolder.getUserId();
        Integer userType = UserContextHolder.getUserType();

        if (userType == null || userType != UserTypeConstant.STUDENT) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED.getCode(), "只有学生才能提交申请");
        }

        if (!StringUtils.hasText(studentId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "学生信息不存在");
        }

        // 查询模板
        CertificateTemplate template = templateMapper.selectById(request.getPkCt());
        if (template == null) {
            throw new BusinessException(ResultCode.TEMPLATE_NOT_FOUND);
        }

        if (template.getIsActive() == 0) {
            throw new BusinessException(ResultCode.TEMPLATE_DISABLED);
        }

        // 查询学生信息，获取导师（第一级审批人）
        StudentInfo studentInfo = studentInfoMapper.selectById(studentId);
        if (studentInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "学生信息不存在");
        }
        
        // 使用学生的导师作为第一级审批人（导师默认为导员）
        String firstApprover = studentInfo.getPkTeacher();
        if (!StringUtils.hasText(firstApprover)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "未配置导师，无法提交申请");
        }

        // 创建申请
        CertificateApplication application = new CertificateApplication();
        application.setPkCa(PrimaryKeyGenerator.generateCertificateApplicationKey());
        application.setApplicationNo(generateApplicationNo());
        application.setPkStudent(studentId);
        application.setPkUser(userId);
        application.setPkCt(request.getPkCt());
        application.setCertificateType(template.getTemplateType());
        application.setApplicationReason(request.getApplicationReason());
        application.setApplicationData(request.getApplicationData());
        application.setStatus(ApplicationStatusConstant.PENDING);
        application.setCurrentApprovalLevel(1); // 第一级：导师
        application.setPkTeacher(firstApprover); // 设置第一级审批人
        application.setCopies(request.getCopies() != null ? request.getCopies() : 1);
        application.setUrgent(request.getUrgent() != null ? request.getUrgent() : 0);

        applicationMapper.insert(application);

        log.info("学生提交申请成功: studentId={}, applicationNo={}", studentId, application.getApplicationNo());
        return convertToResponse(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelApplication(String pkCa) {
        String studentId = UserContextHolder.getStudentId();

        CertificateApplication application = applicationMapper.selectById(pkCa);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        if (!application.getPkStudent().equals(studentId)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED.getCode(), "只能撤销自己的申请");
        }

        if (!ApplicationStatusConstant.PENDING.equals(application.getStatus()) 
            && !ApplicationStatusConstant.IN_PROGRESS.equals(application.getStatus())) {
            throw new BusinessException(ResultCode.APPLICATION_CANNOT_CANCEL);
        }

        application.setStatus(ApplicationStatusConstant.CANCELLED);
        applicationMapper.updateById(application);

        log.info("撤销申请成功: pkCa={}", pkCa);
    }

    @Override
    public ApplicationResponse getApplicationById(String pkCa) {
        CertificateApplication application = applicationMapper.selectById(pkCa);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        return convertToResponse(application);
    }

    @Override
    public IPage<ApplicationResponse> getMyApplications(int current, int size, Integer status) {
        String studentId = UserContextHolder.getStudentId();

        Page<CertificateApplication> page = new Page<>(current, size);
        LambdaQueryWrapper<CertificateApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CertificateApplication::getPkStudent, studentId);
        
        if (status != null) {
            wrapper.eq(CertificateApplication::getStatus, status);
        }

        wrapper.orderByDesc(CertificateApplication::getCreateTime);

        IPage<CertificateApplication> applicationPage = applicationMapper.selectPage(page, wrapper);
        return applicationPage.convert(this::convertToResponse);
    }

    @Override
    public IPage<ApplicationResponse> getPendingApprovals(int current, int size) {
        String teacherId = UserContextHolder.getTeacherId();

        // 查询当前教师信息
        TeacherInfo teacher = teacherInfoMapper.selectById(teacherId);
        if (teacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }

        Page<CertificateApplication> page = new Page<>(current, size);
        LambdaQueryWrapper<CertificateApplication> wrapper = new LambdaQueryWrapper<>();
        
        // 查询待审批和审批中的申请
        wrapper.in(CertificateApplication::getStatus, 
                   ApplicationStatusConstant.PENDING, 
                   ApplicationStatusConstant.IN_PROGRESS);
        
        // 第一级审批：只能看到指定给自己的
        // 第二级及以上：能看到同学院、同系别且级别足够的所有申请
        wrapper.and(w -> w
            // 情况1：第一级审批，指定给我的
            .eq(CertificateApplication::getPkTeacher, teacherId)
            .or()
            // 情况2：第二级及以上，我有足够的级别，且申请者是我学院/系的
            .and(w2 -> {
                w2.isNull(CertificateApplication::getPkTeacher) // pkTeacher为空表示第二级及以上
                  .ge(CertificateApplication::getCurrentApprovalLevel, 2);
                
                // 子查询：申请的学生和我在同一个学院/系
                List<CertificateApplication> allApps = applicationMapper.selectList(
                    new LambdaQueryWrapper<CertificateApplication>()
                        .in(CertificateApplication::getStatus, 
                            ApplicationStatusConstant.PENDING, 
                            ApplicationStatusConstant.IN_PROGRESS)
                        .isNull(CertificateApplication::getPkTeacher)
                );
                
                // 过滤出符合条件的申请
                List<String> validPkCaList = allApps.stream()
                    .filter(app -> {
                        StudentInfo student = studentInfoMapper.selectById(app.getPkStudent());
                        if (student == null) return false;
                        
                        // 验证学院
                        boolean sameCollege = StringUtils.hasText(student.getPkCollege()) 
                            && student.getPkCollege().equals(teacher.getPkCollege());
                        if (!sameCollege) return false;
                        
                        // 验证系别（如果学生有系别）
                        if (StringUtils.hasText(student.getPkDepartment())) {
                            boolean sameDepartment = student.getPkDepartment().equals(teacher.getPkDepartment());
                            if (!sameDepartment) return false;
                        }
                        
                        // 验证审批级别
                        return teacher.getApprovalLevel() != null 
                            && teacher.getApprovalLevel() >= app.getCurrentApprovalLevel()
                            && teacher.getCanApprove() != null 
                            && teacher.getCanApprove() == 1;
                    })
                    .map(CertificateApplication::getPkCa)
                    .toList();
                
                if (!validPkCaList.isEmpty()) {
                    w2.in(CertificateApplication::getPkCa, validPkCaList);
                } else {
                    // 没有符合条件的，添加一个不可能的条件
                    w2.eq(CertificateApplication::getPkCa, "IMPOSSIBLE");
                }
            })
        );

        wrapper.orderByDesc(CertificateApplication::getUrgent)
               .orderByAsc(CertificateApplication::getCreateTime);

        IPage<CertificateApplication> applicationPage = applicationMapper.selectPage(page, wrapper);
        return applicationPage.convert(this::convertToResponse);
    }

    /**
     * 生成申请编号
     */
    private String generateApplicationNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = (int) (Math.random() * 10000);
        return "APP" + dateStr + String.format("%04d", random);
    }

    /**
     * 转换为响应DTO
     */
    private ApplicationResponse convertToResponse(CertificateApplication application) {
        ApplicationResponse response = new ApplicationResponse();
        BeanUtils.copyProperties(application, response);
        response.setStatusDesc(ApplicationStatusConstant.getStatusDesc(application.getStatus()));
        return response;
    }
    
    @Override
    public ApplicationResponse getCertificateDetail(String pkCa) {
        // 查询申请信息
        CertificateApplication application = applicationMapper.selectById(pkCa);
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }
        
        // 查询学生信息
        StudentInfo student = studentInfoMapper.selectById(application.getPkStudent());
        if (student == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "学生信息不存在");
        }
        
        // 查询模板信息
        CertificateTemplate template = templateMapper.selectById(application.getPkCt());
        if (template == null) {
            throw new BusinessException(ResultCode.TEMPLATE_NOT_FOUND);
        }
        
        // 组装响应数据
        ApplicationResponse response = convertToResponse(application);
        
        // 填充学生信息
        response.setStudentName(student.getName());
        response.setStudentNo(student.getStudentNo());
        response.setCollege(student.getCollege());
        response.setMajor(student.getMajor());
        response.setClassName(student.getClassName());
        response.setGrade(student.getGrade());
        response.setEducationLevel(student.getEducationLevel());
        response.setStudyType(student.getStudyType());
        response.setEnrollmentDate(student.getEnrollmentDate() != null ? student.getEnrollmentDate().toString() : null);
        response.setGraduationDate(student.getGraduationDate() != null ? student.getGraduationDate().toString() : null);
        
        // 填充模板信息
        response.setTemplateName(template.getTemplateName());
        response.setTemplateCode(template.getTemplateCode());
        response.setTemplateType(template.getTemplateType());
        response.setTemplateContent(template.getTemplateContent());
        
        return response;
    }
}
