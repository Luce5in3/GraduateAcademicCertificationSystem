package com.graduate.certificate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 证书在线验证记录实体类
 */
@Data
@TableName("certificate_verification")
public class CertificateVerification implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "pk_verification", type = IdType.INPUT)
    private String pkVerification;

    /**
     * 被验证的证书编号
     */
    private String certificateNo;

    /**
     * 关联申请ID
     */
    private String pkCa;

    /**
     * 验证人姓名
     */
    private String verifierName;

    /**
     * 验证人手机号
     */
    private String verifierPhone;

    /**
     * 验证单位名称
     */
    private String verifierCompany;

    /**
     * 验证结果：1-真实有效 0-无效
     */
    private Integer verificationResult;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime createTime;
}
