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
 * 证明模板实体类
 */
@Data
@TableName("certificate_template")
public class CertificateTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板主键（CT+年份+顺序号）
     */
    @TableId(value = "pk_ct", type = IdType.INPUT)
    private String pkCt;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板类型：学历证明/在读证明/成绩证明等
     */
    private String templateType;

    /**
     * 模板内容（支持变量替换）
     */
    private String templateContent;

    /**
     * 必填字段（JSON格式）
     */
    private String requiredFields;

    /**
     * 审批流程配置（JSON格式）
     */
    private String approvalFlow;

    /**
     * 是否启用：0-禁用 1-启用
     */
    private Integer isActive;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建人ID
     */
    private String createBy;

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

    /**
     * 备注
     */
    private String remark;
}
