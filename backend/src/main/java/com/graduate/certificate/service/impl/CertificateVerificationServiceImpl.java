package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduate.certificate.entity.CertificateApplication;
import com.graduate.certificate.entity.CertificateVerification;
import com.graduate.certificate.entity.StudentInfo;
import com.graduate.certificate.mapper.CertificateApplicationMapper;
import com.graduate.certificate.mapper.CertificateVerificationMapper;
import com.graduate.certificate.mapper.StudentInfoMapper;
import com.graduate.certificate.service.CertificateVerificationService;
import com.graduate.certificate.util.PrimaryKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 证书验证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateVerificationServiceImpl implements CertificateVerificationService {

    private final CertificateApplicationMapper applicationMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final CertificateVerificationMapper verificationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> verifyCertificate(String certificateNo, String verifierName,
                                                  String verifierPhone, String verifierCompany,
                                                  String ipAddress, String userAgent) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 查询证书编号
        LambdaQueryWrapper<CertificateApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CertificateApplication::getCertificateNo, certificateNo)
               .eq(CertificateApplication::getStatus, 2); // 已通过
        CertificateApplication application = applicationMapper.selectOne(wrapper);

        CertificateVerification verification = new CertificateVerification();
        verification.setPkVerification(PrimaryKeyGenerator.generateCertificateVerificationKey());
        verification.setCertificateNo(certificateNo);
        verification.setVerifierName(verifierName);
        verification.setVerifierPhone(verifierPhone);
        verification.setVerifierCompany(verifierCompany);
        verification.setIpAddress(ipAddress);
        verification.setUserAgent(userAgent);
        verification.setCreateTime(LocalDateTime.now());

        if (application == null) {
            verification.setVerificationResult(0);
            verificationMapper.insert(verification);

            result.put("valid", false);
            result.put("message", "证书不存在或已失效");
            return result;
        }

        verification.setPkCa(application.getPkCa());
        verification.setVerificationResult(1);
        verificationMapper.insert(verification);

        // 查询学生信息
        StudentInfo student = studentInfoMapper.selectById(application.getPkStudent());

        result.put("valid", true);
        result.put("message", "证书真实有效");
        result.put("certificateNo", application.getCertificateNo());
        result.put("certificateType", application.getCertificateType());
        result.put("studentName", student != null ? student.getName() : "");
        result.put("studentNo", student != null ? student.getStudentNo() : "");
        result.put("college", student != null ? student.getCollege() : "");
        result.put("major", student != null ? student.getMajor() : "");
        result.put("educationLevel", student != null ? student.getEducationLevel() : "");
        result.put("issueDate", application.getIssueDate() != null ?
                application.getIssueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
        result.put("completeTime", application.getCompleteTime() != null ?
                application.getCompleteTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");

        log.info("证书验证成功: certificateNo={}, verifier={}", certificateNo, verifierName);
        return result;
    }
}
