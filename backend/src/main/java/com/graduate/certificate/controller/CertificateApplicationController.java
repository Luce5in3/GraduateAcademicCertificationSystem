package com.graduate.certificate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.dto.application.ApplicationRequest;
import com.graduate.certificate.dto.application.ApplicationResponse;
import com.graduate.certificate.service.CertificateApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 证明申请控制器
 */
@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class CertificateApplicationController {

    private final CertificateApplicationService applicationService;

    /**
     * 提交申请
     */
    @PostMapping
    public Result<ApplicationResponse> submitApplication(@Validated @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.submitApplication(request);
        return Result.success("申请提交成功", response);
    }

    /**
     * 撤销申请
     */
    @PutMapping("/{pkCa}/cancel")
    public Result<Void> cancelApplication(@PathVariable String pkCa) {
        applicationService.cancelApplication(pkCa);
        return Result.success("申请已撤销");
    }

    /**
     * 查询申请详情
     */
    @GetMapping("/{pkCa}")
    public Result<ApplicationResponse> getApplicationById(@PathVariable String pkCa) {
        ApplicationResponse response = applicationService.getApplicationById(pkCa);
        return Result.success(response);
    }

    /**
     * 查询我的申请列表
     */
    @GetMapping("/my")
    public Result<IPage<ApplicationResponse>> getMyApplications(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        IPage<ApplicationResponse> page = applicationService.getMyApplications(current, size, status);
        return Result.success(page);
    }

    /**
     * 查询待审批列表
     */
    @GetMapping("/pending")
    public Result<IPage<ApplicationResponse>> getPendingApprovals(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        IPage<ApplicationResponse> page = applicationService.getPendingApprovals(current, size);
        return Result.success(page);
    }
    
    /**
     * 获取证明详情（包含学生信息和模板信息）
     */
    @GetMapping("/{pkCa}/detail")
    public Result<ApplicationResponse> getCertificateDetail(@PathVariable String pkCa) {
        ApplicationResponse response = applicationService.getCertificateDetail(pkCa);
        return Result.success(response);
    }
}
