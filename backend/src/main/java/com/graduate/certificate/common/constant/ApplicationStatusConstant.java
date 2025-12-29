package com.graduate.certificate.common.constant;

/**
 * 申请状态常量
 */
public class ApplicationStatusConstant {
    
    /**
     * 待审批
     */
    public static final Integer PENDING = 0;
    
    /**
     * 审批中
     */
    public static final Integer IN_PROGRESS = 1;
    
    /**
     * 已通过
     */
    public static final Integer APPROVED = 2;
    
    /**
     * 已拒绝
     */
    public static final Integer REJECTED = 3;
    
    /**
     * 已撤销
     */
    public static final Integer CANCELLED = 4;
    
    /**
     * 获取状态描述
     */
    public static String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "待审批";
            case 1 -> "审批中";
            case 2 -> "已通过";
            case 3 -> "已拒绝";
            case 4 -> "已撤销";
            default -> "未知";
        };
    }
    
    private ApplicationStatusConstant() {
    }
}
