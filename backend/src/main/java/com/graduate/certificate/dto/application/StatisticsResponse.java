package com.graduate.certificate.dto.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计数据响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    
    /**
     * 待审批数量
     */
    private Long pendingCount;
    
    /**
     * 已通过数量
     */
    private Long approvedCount;
    
    /**
     * 申请总数
     */
    private Long totalCount;
    
    /**
     * 今日已审批数量（教师用）
     */
    private Long todayApprovedCount;
    
    /**
     * 总审批数（教师用）
     */
    private Long totalApprovalCount;
}
