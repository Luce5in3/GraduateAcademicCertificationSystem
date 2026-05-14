package com.graduate.certificate.service;

import java.io.ByteArrayOutputStream;

/**
 * 证书PDF生成服务接口
 */
public interface CertificateGenerateService {

    /**
     * 生成证书PDF
     *
     * @param pkCa 申请主键
     * @return PDF文件字节数组
     */
    ByteArrayOutputStream generateCertificatePdf(String pkCa);
}
