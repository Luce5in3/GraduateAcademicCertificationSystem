package com.graduate.certificate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生信息实体类
 */
@Data
@TableName("student_info")
public class StudentInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（S+年份+顺序号，如：S2021000001）
     */
    @TableId(value = "pk_student", type = IdType.INPUT)
    private String pkStudent;

    /**
     * 关联用户表ID（sys_user.pk_user）
     */
    private String pkUser;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别：0-女 1-男
     */
    private Integer gender;

    /**
     * 身份证号
     */
    private String idCard;

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
     * 毕业状态：0-在读 1-已毕业 2-结业
     */
    private Integer graduationStatus;

    /**
     * 导师ID（teacher_info.pk_teacher）
     */
    private String pkTeacher;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
