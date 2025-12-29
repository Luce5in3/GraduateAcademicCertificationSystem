package com.graduate.certificate.dto.student;

import lombok.Data;

import java.time.LocalDate;

/**
 * 学生信息请求DTO
 * 注意：学生主键、用户ID、学号、姓名由系统从上下文获取，不允许前端传入
 */
@Data
public class StudentInfoRequest {

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 性别：0-女 1-男
     */
    private Integer gender;

    /**
     * 学院
     */
    private String college;

    /**
     * 专业
     */
    private String major;

    /**
     * 班级
     */
    private String className;

    /**
     * 年级
     */
    private String grade;

    /**
     * 入学日期
     */
    private LocalDate enrollmentDate;

    /**
     * 毕业日期
     */
    private LocalDate graduationDate;

    /**
     * 学历层次：本科/硕士/博士
     */
    private String educationLevel;

    /**
     * 学习形式：全日制/非全日制
     */
    private String studyType;

    /**
     * 导师ID
     */
    private String pkTeacher;
}
