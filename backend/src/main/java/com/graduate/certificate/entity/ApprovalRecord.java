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
 * 审批记录实体类
 */
@Data
@TableName("approval_record")
public class ApprovalRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 审批记录主键（AR+年份+顺序号）
     */
    @TableId(value = "pk_ar", type = IdType.INPUT)
    private String pkAr;

    /**
     * 申请ID
     */
    private String pkCa;

    /**
     * 申请编号（冗余字段）
     */
    private String applicationNo;

    /**
     * 审批人ID
     */
    private String pkTeacher;

    /**
     * 审批人用户ID
     */
    private String pkUser;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 审批级别：1-初审 2-复审 3-终审
     */
    private Integer approvalLevel;

    /**
     * 审批结果：1-通过 2-拒绝 3-退回
     */
    private Integer approvalResult;

    /**
     * 审批意见
     */
    private String approvalOpinion;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 审批耗时（分钟）
     */
    private Integer timeCost;

    /**
     * 附件URL
     */
    private String attachmentUrl;

    /**
     * 审批IP地址
     */
    private String ipAddress;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
