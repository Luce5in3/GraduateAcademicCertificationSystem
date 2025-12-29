package com.graduate.certificate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.dto.teacher.TeacherInfoRequest;
import com.graduate.certificate.dto.teacher.TeacherInfoResponse;

/**
 * 教师信息服务接口
 */
public interface TeacherInfoService {

    /**
     * 更新教师信息
     */
    TeacherInfoResponse updateTeacher(TeacherInfoRequest request);

    /**
     * 删除教师信息
     */
    void deleteTeacher(String pkTeacher);

    /**
     * 根据主键查询教师信息
     */
    TeacherInfoResponse getTeacherById(String pkTeacher);

    /**
     * 根据用户ID查询教师信息
     */
    TeacherInfoResponse getTeacherByUserId(String pkUser);

    /**
     * 根据工号查询教师信息
     */
    TeacherInfoResponse getTeacherByTeacherNo(String teacherNo);

    /**
     * 分页查询教师信息
     */
    IPage<TeacherInfoResponse> pageTeachers(int current, int size, String college, String department, Integer approvalLevel);
}
