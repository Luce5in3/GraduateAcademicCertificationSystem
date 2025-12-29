package com.graduate.certificate.dto.approval;

import lombok.Data;

/**
 * 审批请求DTO
 */
@Data
public class ApprovalRequest {
    
    /**
     * 申请主键
     */
    private String pkCa;
    
    /**
     * 审批结果：1-通过 2-拒绝 3-退回
     */
    private Integer approvalResult;
    
    /**
     * 审批意见
     */
    private String approvalOpinion;
    
    /**
     * 附件URL
     */
    private String attachmentUrl;
}
