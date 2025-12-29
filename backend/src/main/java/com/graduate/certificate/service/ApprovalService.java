package com.graduate.certificate.service;

import com.graduate.certificate.dto.approval.ApprovalRequest;

/**
 * 审批服务接口
 */
public interface ApprovalService {
    
    /**
     * 处理审批
     */
    void processApproval(ApprovalRequest request);
}
