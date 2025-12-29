package com.graduate.certificate.dto.teacher;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教师信息响应DTO
 */
@Data
public class TeacherInfoResponse {

    /**
     * 教师主键
     */
    private String pkTeacher;

    /**
     * 关联用户ID
     */
    private String pkUser;

    /**
     * 工号
     */
    private String teacherNo;

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
     * 所属学院
     */
    private String college;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 职称：教授/副教授/讲师等
     */
    private String title;

    /**
     * 职位：院长/系主任/辅导员等
     */
    private String position;

    /**
     * 审批级别：1-初审 2-复审 3-终审
     */
    private Integer approvalLevel;

    /**
     * 是否有审批权限：0-否 1-是
     */
    private Integer canApprove;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
