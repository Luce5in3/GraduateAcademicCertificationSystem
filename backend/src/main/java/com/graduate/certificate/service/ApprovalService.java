package com.graduate.certificate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.dto.approval.ApprovalRequest;
import com.graduate.certificate.dto.approval.BatchApprovalRequest;
import com.graduate.certificate.entity.ApprovalRecord;

import java.util.List;

/**
 * 审批服务接口
 */
public interface ApprovalService {
    
    /**
     * 处理审批
     */
    void processApproval(ApprovalRequest request);

    /**
     * 批量审批
     */
    void batchApproval(BatchApprovalRequest request);

    /**
     * 获取当前教师的审批历史记录
     */
    IPage<ApprovalRecord> getApprovalHistory(int current, int size);

    /**
     * 获取某条申请的审批记录
     */
    List<ApprovalRecord> getRecordsByApplication(String pkCa);
}
