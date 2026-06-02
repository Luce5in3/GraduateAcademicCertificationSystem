package com.graduate.certificate.controller;

import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.service.CertificateVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 公开证书验证控制器（无需登录）
 */
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final CertificateVerificationService verificationService;

    /**
     * 证书真伪验证（公开接口）
     */
    @PostMapping("/verify")
    public Result<Map<String, Object>> verifyCertificate(
            @RequestParam String certificateNo,
            @RequestParam(required = false) String verifierName,
            @RequestParam(required = false) String verifierPhone,
            @RequestParam(required = false) String verifierCompany,
            HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        Map<String, Object> result = verificationService.verifyCertificate(
                certificateNo, verifierName, verifierPhone, verifierCompany, ipAddress, userAgent);
        return Result.success(result);
    }

    /**
     * 通过GET方式验证（便于扫码跳转）
     */
    @GetMapping("/verify")
    public Result<Map<String, Object>> verifyCertificateGet(
            @RequestParam String certificateNo,
            HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        Map<String, Object> result = verificationService.verifyCertificate(
                certificateNo, null, null, null, ipAddress, userAgent);
        return Result.success(result);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
