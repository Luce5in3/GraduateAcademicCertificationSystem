package com.graduate.certificate.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师信息实体类
 */
@Data
@TableName("teacher_info")
public class TeacherInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（T+年份+顺序号，如：T2023000001）
     */
    @TableId(value = "pk_teacher", type = IdType.INPUT)
    private String pkTeacher;

    /**
     * 关联用户表ID（sys_user.pk_user）
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
     * 职称
     */
    private String title;

    /**
     * 职位
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
     * 部门主键(所属系别)
     */
    private String pkDepartment;

    /**
     * 学院主键
     */
    private String pkCollege;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
