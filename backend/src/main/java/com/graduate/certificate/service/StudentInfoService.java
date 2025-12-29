package com.graduate.certificate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.dto.student.StudentInfoRequest;
import com.graduate.certificate.dto.student.StudentInfoResponse;

/**
 * 学生信息服务接口
 */
public interface StudentInfoService {

    /**
     * 更新学生信息
     */
    StudentInfoResponse updateStudent(StudentInfoRequest request);

    /**
     * 删除学生信息
     */
    void deleteStudent(String pkStudent);

    /**
     * 根据主键查询学生信息
     */
    StudentInfoResponse getStudentById(String pkStudent);

    /**
     * 根据用户ID查询学生信息
     */
    StudentInfoResponse getStudentByUserId(String pkUser);

    /**
     * 根据学号查询学生信息
     */
    StudentInfoResponse getStudentByStudentNo(String studentNo);

    /**
     * 分页查询学生信息
     */
    IPage<StudentInfoResponse> pageStudents(int current, int size, String college, String major, Integer graduationStatus);
}
