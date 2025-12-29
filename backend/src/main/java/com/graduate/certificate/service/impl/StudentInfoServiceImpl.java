package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduate.certificate.common.constant.UserTypeConstant;
import com.graduate.certificate.common.context.UserContextHolder;
import com.graduate.certificate.common.exception.BusinessException;
import com.graduate.certificate.common.result.ResultCode;
import com.graduate.certificate.dto.student.StudentInfoRequest;
import com.graduate.certificate.dto.student.StudentInfoResponse;
import com.graduate.certificate.entity.StudentInfo;
import com.graduate.certificate.mapper.StudentInfoMapper;
import com.graduate.certificate.service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 学生信息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentInfoServiceImpl implements StudentInfoService {

    private final StudentInfoMapper studentInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentInfoResponse updateStudent(StudentInfoRequest request) {
        // 从上下文获取当前用户信息
        String studentId = UserContextHolder.getStudentId();
        Integer userType = UserContextHolder.getUserType();
        
        // 验证用户类型
        if (!(UserTypeConstant.STUDENT == userType)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED.getCode(), "只有学生才能更新学生信息");
        }
        
        if (!StringUtils.hasText(studentId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "学生信息不存在");
        }

        // 查询学生信息
        StudentInfo existingStudent = studentInfoMapper.selectById(studentId);
        if (existingStudent == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }

        // 更新学生信息（只更新允许修改的字段）
        if (request.getGender() != null) {
            existingStudent.setGender(request.getGender());
        }
        if (StringUtils.hasText(request.getIdCard())) {
            existingStudent.setIdCard(request.getIdCard());
        }
        if (StringUtils.hasText(request.getCollege())) {
            existingStudent.setCollege(request.getCollege());
        }
        if (StringUtils.hasText(request.getMajor())) {
            existingStudent.setMajor(request.getMajor());
        }
        if (StringUtils.hasText(request.getClassName())) {
            existingStudent.setClassName(request.getClassName());
        }
        if (StringUtils.hasText(request.getGrade())) {
            existingStudent.setGrade(request.getGrade());
        }
        if (request.getEnrollmentDate() != null) {
            existingStudent.setEnrollmentDate(request.getEnrollmentDate());
        }
        if (request.getGraduationDate() != null) {
            existingStudent.setGraduationDate(request.getGraduationDate());
        }
        if (StringUtils.hasText(request.getEducationLevel())) {
            existingStudent.setEducationLevel(request.getEducationLevel());
        }
        if (StringUtils.hasText(request.getStudyType())) {
            existingStudent.setStudyType(request.getStudyType());
        }
        if (StringUtils.hasText(request.getPkTeacher())) {
            existingStudent.setPkTeacher(request.getPkTeacher());
        }
        
        studentInfoMapper.updateById(existingStudent);
        
        log.info("更新学生信息成功: {}", existingStudent.getPkStudent());
        return convertToResponse(existingStudent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(String pkStudent) {
        if (!StringUtils.hasText(pkStudent)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "学生主键不能为空");
        }

        StudentInfo studentInfo = studentInfoMapper.selectById(pkStudent);
        if (studentInfo == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }

        studentInfoMapper.deleteById(pkStudent);
        log.info("删除学生信息成功: {}", pkStudent);
    }

    @Override
    public StudentInfoResponse getStudentById(String pkStudent) {
        if (!StringUtils.hasText(pkStudent)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "学生主键不能为空");
        }

        StudentInfo studentInfo = studentInfoMapper.selectById(pkStudent);
        if (studentInfo == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }

        return convertToResponse(studentInfo);
    }

    @Override
    public StudentInfoResponse getStudentByUserId(String pkUser) {
        if (!StringUtils.hasText(pkUser)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户ID不能为空");
        }

        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getPkUser, pkUser);
        StudentInfo studentInfo = studentInfoMapper.selectOne(wrapper);
        
        if (studentInfo == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }

        return convertToResponse(studentInfo);
    }

    @Override
    public StudentInfoResponse getStudentByStudentNo(String studentNo) {
        if (!StringUtils.hasText(studentNo)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "学号不能为空");
        }

        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getStudentNo, studentNo);
        StudentInfo studentInfo = studentInfoMapper.selectOne(wrapper);
        
        if (studentInfo == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }

        return convertToResponse(studentInfo);
    }

    @Override
    public IPage<StudentInfoResponse> pageStudents(int current, int size, String college, String major, Integer graduationStatus) {
        Page<StudentInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(college)) {
            wrapper.eq(StudentInfo::getCollege, college);
        }
        if (StringUtils.hasText(major)) {
            wrapper.eq(StudentInfo::getMajor, major);
        }
        if (graduationStatus != null) {
            wrapper.eq(StudentInfo::getGraduationStatus, graduationStatus);
        }
        
        wrapper.orderByDesc(StudentInfo::getCreateTime);
        
        IPage<StudentInfo> studentPage = studentInfoMapper.selectPage(page, wrapper);
        
        // 转换为响应DTO
        return studentPage.convert(this::convertToResponse);
    }

    /**
     * 实体转换为响应DTO
     */
    private StudentInfoResponse convertToResponse(StudentInfo studentInfo) {
        StudentInfoResponse response = new StudentInfoResponse();
        BeanUtils.copyProperties(studentInfo, response);
        return response;
    }
}
