package com.graduate.certificate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduate.certificate.entity.CertificateTemplate;

/**
 * 证明模板服务接口
 */
public interface CertificateTemplateService extends IService<CertificateTemplate> {
    
    /**
     * 分页查询模板
     */
    IPage<CertificateTemplate> pageTemplates(int current, int size);
    
    /**
     * 保存模板
     */
    void saveTemplate(CertificateTemplate template);
    
    /**
     * 更新模板
     */
    void updateTemplate(CertificateTemplate template);
    
    /**
     * 删除模板
     */
    void deleteTemplate(String pkCt);
}
