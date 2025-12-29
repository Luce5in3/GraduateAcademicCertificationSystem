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
 * 证明申请实体类
 */
@Data
@TableName("certificate_application")
public class CertificateApplication implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申请主键（CA+年份+顺序号）
     */
    @TableId(value = "pk_ca", type = IdType.INPUT)
    private String pkCa;

    /**
     * 申请编号（对外展示编号）
     */
    private String applicationNo;

    /**
     * 学生ID
     */
    private String pkStudent;

    /**
     * 学生用户ID
     */
    private String pkUser;

    /**
     * 证明模板ID
     */
    private String pkCt;

    /**
     * 证明类型
     */
    private String certificateType;

    /**
     * 申请理由
     */
    private String applicationReason;

    /**
     * 申请数据（JSON格式，表单填写内容）
     */
    private String applicationData;

    /**
     * 申请状态：0-待审批 1-审批中 2-已通过 3-已拒绝 4-已撤销
     */
    private Integer status;

    /**
     * 当前审批级别
     */
    private Integer currentApprovalLevel;

    /**
     * 当前审批人ID
     */
    private String pkTeacher;

    /**
     * 生成的证明文件URL
     */
    private String certificateFileUrl;

    /**
     * 证明编号
     */
    private String certificateNo;

    /**
     * 开具日期
     */
    private LocalDateTime issueDate;

    /**
     * 申请份数
     */
    private Integer copies;

    /**
     * 是否加急：0-否 1-是
     */
    private Integer urgent;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

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
