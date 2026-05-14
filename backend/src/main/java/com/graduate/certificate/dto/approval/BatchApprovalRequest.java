package com.graduate.certificate.dto.approval;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 批量审批请求DTO
 */
@Data
public class BatchApprovalRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 申请ID列表
     */
    @NotEmpty(message = "申请ID列表不能为空")
    private List<String> pkCaList;

    /**
     * 审批结果：1-通过 2-拒绝 3-退回
     */
    @NotNull(message = "审批结果不能为空")
    private Integer approvalResult;

    /**
     * 审批意见
     */
    private String approvalOpinion;
}
