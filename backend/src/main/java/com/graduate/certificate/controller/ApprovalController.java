package com.graduate.certificate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.dto.approval.ApprovalRequest;
import com.graduate.certificate.dto.approval.BatchApprovalRequest;
import com.graduate.certificate.entity.ApprovalRecord;
import com.graduate.certificate.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 批量审批
     */
    @PostMapping("/batch")
    public Result<Void> batchApproval(@Validated @RequestBody BatchApprovalRequest request) {
        approvalService.batchApproval(request);
        return Result.success("批量审批完成");
    }

    /**
     * 获取当前教师的审批历史记录
     */
    @GetMapping("/history")
    public Result<IPage<ApprovalRecord>> getApprovalHistory(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(approvalService.getApprovalHistory(current, size));
    }

    /**
     * 获取某条申请的审批记录
     */
    @GetMapping("/records/{pkCa}")
    public Result<List<ApprovalRecord>> getRecordsByApplication(@PathVariable String pkCa) {
        return Result.success(approvalService.getRecordsByApplication(pkCa));
    }
}
