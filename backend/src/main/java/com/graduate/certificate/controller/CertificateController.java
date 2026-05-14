package com.graduate.certificate.controller;

import com.graduate.certificate.service.CertificateGenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 证书生成与下载控制器
 */
@Slf4j
@RestController
@RequestMapping("/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateGenerateService certificateGenerateService;

    /**
     * 下载证书PDF
     */
    @GetMapping("/download/{pkCa}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String pkCa) {
        log.info("下载证书PDF: pkCa={}", pkCa);

        ByteArrayOutputStream pdfStream = certificateGenerateService.generateCertificatePdf(pkCa);

        String fileName = URLEncoder.encode("学术证明_" + pkCa + ".pdf", StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfStream.size())
                .body(pdfStream.toByteArray());
    }

    /**
     * 预览证书PDF（在浏览器中打开）
     */
    @GetMapping("/preview/{pkCa}")
    public ResponseEntity<byte[]> previewCertificate(@PathVariable String pkCa) {
        log.info("预览证书PDF: pkCa={}", pkCa);

        ByteArrayOutputStream pdfStream = certificateGenerateService.generateCertificatePdf(pkCa);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"certificate.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfStream.size())
                .body(pdfStream.toByteArray());
    }
}
