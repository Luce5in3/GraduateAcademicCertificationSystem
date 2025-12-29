package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduate.certificate.common.constant.UserTypeConstant;
import com.graduate.certificate.common.context.UserContextHolder;
import com.graduate.certificate.common.exception.BusinessException;
import com.graduate.certificate.common.result.ResultCode;
import com.graduate.certificate.dto.teacher.TeacherInfoRequest;
import com.graduate.certificate.dto.teacher.TeacherInfoResponse;
import com.graduate.certificate.entity.TeacherInfo;
import com.graduate.certificate.mapper.TeacherInfoMapper;
import com.graduate.certificate.service.TeacherInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 教师信息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherInfoServiceImpl implements TeacherInfoService {

    private final TeacherInfoMapper teacherInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeacherInfoResponse updateTeacher(TeacherInfoRequest request) {
        // 从上下文获取当前用户信息
        String teacherId = UserContextHolder.getTeacherId();
        Integer userType = UserContextHolder.getUserType();
        
        // 验证用户类型
        if (!(UserTypeConstant.TEACHER == userType)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED.getCode(), "只有教师才能更新教师信息");
        }
        
        if (!StringUtils.hasText(teacherId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "教师信息不存在");
        }

        // 查询教师信息
        TeacherInfo existingTeacher = teacherInfoMapper.selectById(teacherId);
        if (existingTeacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }

        // 更新教师信息（只更新允许修改的字段）
        if (request.getGender() != null) {
            existingTeacher.setGender(request.getGender());
        }
        if (StringUtils.hasText(request.getIdCard())) {
            existingTeacher.setIdCard(request.getIdCard());
        }
        if (StringUtils.hasText(request.getCollege())) {
            existingTeacher.setCollege(request.getCollege());
        }
        if (StringUtils.hasText(request.getDepartment())) {
            existingTeacher.setDepartment(request.getDepartment());
        }
        if (StringUtils.hasText(request.getTitle())) {
            existingTeacher.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getPosition())) {
            existingTeacher.setPosition(request.getPosition());
        }
        
        teacherInfoMapper.updateById(existingTeacher);
        
        log.info("更新教师信息成功: {}", existingTeacher.getPkTeacher());
        return convertToResponse(existingTeacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeacher(String pkTeacher) {
        if (!StringUtils.hasText(pkTeacher)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "教师主键不能为空");
        }

        TeacherInfo teacherInfo = teacherInfoMapper.selectById(pkTeacher);
        if (teacherInfo == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }

        teacherInfoMapper.deleteById(pkTeacher);
        log.info("删除教师信息成功: {}", pkTeacher);
    }

    @Override
    public TeacherInfoResponse getTeacherById(String pkTeacher) {
        if (!StringUtils.hasText(pkTeacher)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "教师主键不能为空");
        }

        TeacherInfo teacherInfo = teacherInfoMapper.selectById(pkTeacher);
        if (teacherInfo == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }

        return convertToResponse(teacherInfo);
    }

    @Override
    public TeacherInfoResponse getTeacherByUserId(String pkUser) {
        if (!StringUtils.hasText(pkUser)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户ID不能为空");
        }

        LambdaQueryWrapper<TeacherInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherInfo::getPkUser, pkUser);
        TeacherInfo teacherInfo = teacherInfoMapper.selectOne(wrapper);
        
        if (teacherInfo == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }

        return convertToResponse(teacherInfo);
    }

    @Override
    public TeacherInfoResponse getTeacherByTeacherNo(String teacherNo) {
        if (!StringUtils.hasText(teacherNo)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "工号不能为空");
        }

        LambdaQueryWrapper<TeacherInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherInfo::getTeacherNo, teacherNo);
        TeacherInfo teacherInfo = teacherInfoMapper.selectOne(wrapper);
        
        if (teacherInfo == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }

        return convertToResponse(teacherInfo);
    }

    @Override
    public IPage<TeacherInfoResponse> pageTeachers(int current, int size, String college, String department, Integer approvalLevel) {
        Page<TeacherInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<TeacherInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(college)) {
            wrapper.eq(TeacherInfo::getCollege, college);
        }
        if (StringUtils.hasText(department)) {
            wrapper.eq(TeacherInfo::getDepartment, department);
        }
        if (approvalLevel != null) {
            wrapper.eq(TeacherInfo::getApprovalLevel, approvalLevel);
        }
        
        wrapper.orderByDesc(TeacherInfo::getCreateTime);
        
        IPage<TeacherInfo> teacherPage = teacherInfoMapper.selectPage(page, wrapper);
        
        // 转换为响应DTO
        return teacherPage.convert(this::convertToResponse);
    }

    /**
     * 实体转换为响应DTO
     */
    private TeacherInfoResponse convertToResponse(TeacherInfo teacherInfo) {
        TeacherInfoResponse response = new TeacherInfoResponse();
        BeanUtils.copyProperties(teacherInfo, response);
        return response;
    }
}
