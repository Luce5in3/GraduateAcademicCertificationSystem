package com.graduate.certificate.controller;

import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.dto.approval.ApprovalRequest;
import com.graduate.certificate.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批控制器
 */
@RestController
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * 处理审批
     */
    @PostMapping
    public Result<Void> processApproval(@Validated @RequestBody ApprovalRequest request) {
        approvalService.processApproval(request);
        return Result.success("审批操作成功");
    }
}
