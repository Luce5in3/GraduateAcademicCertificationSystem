package com.graduate.certificate.dto.application;

import lombok.Data;

/**
 * 申请请求DTO
 */
@Data
public class ApplicationRequest {
    
    /**
     * 证明模板ID
     */
    private String pkCt;
    
    /**
     * 申请理由
     */
    private String applicationReason;
    
    /**
     * 申请数据（JSON格式）
     */
    private String applicationData;
    
    /**
     * 申请份数
     */
    private Integer copies;
    
    /**
     * 是否加急：0-否 1-是
     */
    private Integer urgent;
}
