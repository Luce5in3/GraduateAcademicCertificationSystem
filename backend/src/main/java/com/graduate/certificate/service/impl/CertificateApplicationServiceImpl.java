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
import com.graduate.certificate.dto.application.StatisticsResponse;
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
import java.util.Map;

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
    private final ApprovalRecordMapper approvalRecordMapper;

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
        
        // 解析审批等级
        int targetLevel = 1;
        try {
            if (StringUtils.hasText(template.getApprovalFlow())) {
                targetLevel = Integer.parseInt(template.getApprovalFlow().trim());
            }
        } catch (NumberFormatException e) {
            log.error("解析审批流程配置等级失败: {}, 默认使用1级", template.getApprovalFlow());
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
        application.setCurrentApprovalLevel(targetLevel); // 直接设置为目标审批等级
        
        // 只有1级审批才指定特定导师，2级及以上不指定（让符合等级的老师都能审批）
        if (targetLevel == 1) {
            application.setPkTeacher(studentInfo.getPkTeacher());
        } else {
            application.setPkTeacher(null);
        }
        
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
        
        // 先查询第二级及以上的申请，过滤出符合条件的
        List<CertificateApplication> secondLevelApps = applicationMapper.selectList(
            new LambdaQueryWrapper<CertificateApplication>()
                .in(CertificateApplication::getStatus, 
                    ApplicationStatusConstant.PENDING, 
                    ApplicationStatusConstant.IN_PROGRESS)
                .ge(CertificateApplication::getCurrentApprovalLevel, 2)
        );
        
        // 过滤出符合条件的申请ID
        List<String> secondLevelValidIds = secondLevelApps.stream()
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
                
                // 关键修改：审批级别必须等于当前级别，不能是大于等于
                return teacher.getApprovalLevel() != null 
                    && teacher.getApprovalLevel() == app.getCurrentApprovalLevel()  // 从 >= 改为 ==
                    && teacher.getCanApprove() != null 
                    && teacher.getCanApprove() == 1;
            })
            .map(CertificateApplication::getPkCa)
            .toList();
        
        // 构建查询条件：（第一级 OR 第二级）
        wrapper.and(w -> {
            // 情况1：第一级审批
            w.and(w1 -> w1
                .eq(CertificateApplication::getCurrentApprovalLevel, 1)
                .eq(CertificateApplication::getPkTeacher, teacherId)
            );
            
            // 情况2：第二级及以上（如果有符合条件的）
            if (!secondLevelValidIds.isEmpty()) {
                w.or(w2 -> w2.in(CertificateApplication::getPkCa, secondLevelValidIds));
            }
        });

        wrapper.orderByDesc(CertificateApplication::getUrgent)
               .orderByAsc(CertificateApplication::getCreateTime);

        IPage<CertificateApplication> applicationPage = applicationMapper.selectPage(page, wrapper);
        
        // 转换为响应DTO并填充学生信息
        return applicationPage.convert(app -> {
            ApplicationResponse response = convertToResponse(app);
            
            // 填充学生信息
            StudentInfo student = studentInfoMapper.selectById(app.getPkStudent());
            if (student != null) {
                response.setStudentName(student.getName());
                response.setStudentNo(student.getStudentNo());
                response.setCollege(student.getCollege());
                response.setMajor(student.getMajor());
            }
            
            return response;
        });
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
    
    @Override
    public StatisticsResponse getStudentStatistics() {
        String studentId = UserContextHolder.getStudentId();
        
        // 查询待审批数量（状态为待审批或审批中）
        Long pendingCount = applicationMapper.selectCount(
            new LambdaQueryWrapper<CertificateApplication>()
                .eq(CertificateApplication::getPkStudent, studentId)
                .in(CertificateApplication::getStatus, 
                    ApplicationStatusConstant.PENDING, 
                    ApplicationStatusConstant.IN_PROGRESS)
        );
        
        // 查询已通过数量
        Long approvedCount = applicationMapper.selectCount(
            new LambdaQueryWrapper<CertificateApplication>()
                .eq(CertificateApplication::getPkStudent, studentId)
                .eq(CertificateApplication::getStatus, ApplicationStatusConstant.APPROVED)
        );
        
        // 查询申请总数
        Long totalCount = applicationMapper.selectCount(
            new LambdaQueryWrapper<CertificateApplication>()
                .eq(CertificateApplication::getPkStudent, studentId)
        );
        
        return StatisticsResponse.builder()
                .pendingCount(pendingCount)
                .approvedCount(approvedCount)
                .totalCount(totalCount)
                .build();
    }
    
    @Override
    public StatisticsResponse getTeacherStatistics() {
        String teacherId = UserContextHolder.getTeacherId();
        String userId = UserContextHolder.getUserId();
        
        // 查询当前教师信息
        TeacherInfo teacher = teacherInfoMapper.selectById(teacherId);
        if (teacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }
        
        // 查询待审批数量（需要当前教师审批的申请）
        // 第一级审批：指定给自己的
        Long pendingCount = applicationMapper.selectCount(
            new LambdaQueryWrapper<CertificateApplication>()
                .in(CertificateApplication::getStatus, 
                    ApplicationStatusConstant.PENDING, 
                    ApplicationStatusConstant.IN_PROGRESS)
                .eq(CertificateApplication::getCurrentApprovalLevel, 1)
                .eq(CertificateApplication::getPkTeacher, teacherId)
        );
        
        // 第二级及以上：需要遍历判断（这里简化处理，只统计可以看到的）
        List<CertificateApplication> allPendingApps = applicationMapper.selectList(
            new LambdaQueryWrapper<CertificateApplication>()
                .in(CertificateApplication::getStatus, 
                    ApplicationStatusConstant.PENDING, 
                    ApplicationStatusConstant.IN_PROGRESS)
                .ge(CertificateApplication::getCurrentApprovalLevel, 2)
                .ge(CertificateApplication::getCurrentApprovalLevel, 2)
        );
        
        // 过滤出符合条件的申请
        long secondLevelCount = allPendingApps.stream()
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
                
                // 关键修改：审批级别必须等于当前级别
                return teacher.getApprovalLevel() != null 
                    && teacher.getApprovalLevel() == app.getCurrentApprovalLevel()  // 从 >= 改为 ==
                    && teacher.getCanApprove() != null 
                    && teacher.getCanApprove() == 1;
            })
            .count();
        
        pendingCount += secondLevelCount;
        
        // 查询今日已审批数量
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        Long todayApprovedCount = approvalRecordMapper.selectCount(
            new LambdaQueryWrapper<ApprovalRecord>()
                .eq(ApprovalRecord::getPkUser, userId)
                .ge(ApprovalRecord::getApprovalTime, todayStart)
        );
        
        // 查询总审批数
        Long totalApprovalCount = approvalRecordMapper.selectCount(
            new LambdaQueryWrapper<ApprovalRecord>()
                .eq(ApprovalRecord::getPkUser, userId)
        );
        
        return StatisticsResponse.builder()
                .pendingCount(pendingCount)
                .todayApprovedCount(todayApprovedCount)
                .totalApprovalCount(totalApprovalCount)
                .build();
    }
    
    @Override
    public StatisticsResponse getAdminStatistics() {
        // 全站待审批
        Long pendingCount = applicationMapper.selectCount(
                new LambdaQueryWrapper<CertificateApplication>()
                        .in(CertificateApplication::getStatus,
                                ApplicationStatusConstant.PENDING,
                                ApplicationStatusConstant.IN_PROGRESS)
        );
    
        // 全站已通过
        Long approvedCount = applicationMapper.selectCount(
                new LambdaQueryWrapper<CertificateApplication>()
                        .eq(CertificateApplication::getStatus, ApplicationStatusConstant.APPROVED)
        );
    
        // 全站总申请
        Long totalCount = applicationMapper.selectCount(null);
    
        return StatisticsResponse.builder()
                .pendingCount(pendingCount)
                .approvedCount(approvedCount)
                .totalCount(totalCount)
                .build();
    }
    
    @Override
    public List<Map<String, Object>> getAvailableTemplates() {
        // 查询所有启用的模板，按排序字段排序
        List<CertificateTemplate> templates = templateMapper.selectList(
            new LambdaQueryWrapper<CertificateTemplate>()
                .eq(CertificateTemplate::getIsActive, 1)
                .orderByAsc(CertificateTemplate::getSortOrder)
        );
        
        // 转换为前端需要的格式
        return templates.stream()
                .map(template -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("value", template.getPkCt());
                    map.put("label", template.getTemplateName());
                    map.put("type", template.getTemplateType());
                    return map;
                })
                .toList();
    }

    @Override
    public IPage<ApplicationResponse> listAllApplications(int current, int size, Integer status) {
        Page<CertificateApplication> page = new Page<>(current, size);
        LambdaQueryWrapper<CertificateApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(CertificateApplication::getStatus, status);
        }
        wrapper.orderByDesc(CertificateApplication::getCreateTime);
        
        IPage<CertificateApplication> applicationPage = applicationMapper.selectPage(page, wrapper);
        return applicationPage.convert(app -> {
            ApplicationResponse response = convertToResponse(app);
            // 填充学生信息
            StudentInfo student = studentInfoMapper.selectById(app.getPkStudent());
            if (student != null) {
                response.setStudentName(student.getName());
                response.setStudentNo(student.getStudentNo());
                response.setCollege(student.getCollege());
                response.setMajor(student.getMajor());
            }
            return response;
        });
    }
}
