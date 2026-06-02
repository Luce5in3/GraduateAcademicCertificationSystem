package com.graduate.certificate.service;

import com.graduate.certificate.entity.CertificateVerification;

import java.util.Map;

/**
 * 证书验证服务接口
 */
public interface CertificateVerificationService {

    /**
     * 验证证书真伪
     * @param certificateNo 证书编号
     * @param verifierName 验证人姓名
     * @param verifierPhone 验证人手机号
     * @param verifierCompany 验证单位
     * @param ipAddress IP地址
     * @param userAgent UA
     * @return 验证结果
     */
    Map<String, Object> verifyCertificate(String certificateNo, String verifierName,
                                           String verifierPhone, String verifierCompany,
                                           String ipAddress, String userAgent);
}
