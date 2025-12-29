package com.graduate.certificate.common.constant;

/**
 * 审批结果常量
 */
public class ApprovalResultConstant {
    
    /**
     * 通过
     */
    public static final Integer APPROVED = 1;
    
    /**
     * 拒绝
     */
    public static final Integer REJECTED = 2;
    
    /**
     * 退回
     */
    public static final Integer RETURNED = 3;
    
    /**
     * 获取结果描述
     */
    public static String getResultDesc(Integer result) {
        if (result == null) {
            return "未知";
        }
        return switch (result) {
            case 1 -> "通过";
            case 2 -> "拒绝";
            case 3 -> "退回";
            default -> "未知";
        };
    }
    
    private ApprovalResultConstant() {
    }
}
