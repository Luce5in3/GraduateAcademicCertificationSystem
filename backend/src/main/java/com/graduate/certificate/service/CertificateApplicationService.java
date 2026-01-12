package com.graduate.certificate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.dto.application.ApplicationRequest;
import com.graduate.certificate.dto.application.ApplicationResponse;

/**
 * 证明申请服务接口
 */
public interface CertificateApplicationService {
    
    /**
     * 提交申请
     */
    ApplicationResponse submitApplication(ApplicationRequest request);
    
    /**
     * 撤销申请
     */
    void cancelApplication(String pkCa);
    
    /**
     * 根据ID查询申请
     */
    ApplicationResponse getApplicationById(String pkCa);
    
    /**
     * 查询我的申请列表
     */
    IPage<ApplicationResponse> getMyApplications(int current, int size, Integer status);
    
    /**
     * 查询待审批列表
     */
    IPage<ApplicationResponse> getPendingApprovals(int current, int size);
    
    /**
     * 获取证明详情（包含学生信息和模板信息）
     */
    ApplicationResponse getCertificateDetail(String pkCa);
}
