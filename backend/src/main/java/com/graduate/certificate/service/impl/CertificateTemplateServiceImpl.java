package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduate.certificate.entity.CertificateTemplate;
import com.graduate.certificate.mapper.CertificateTemplateMapper;
import com.graduate.certificate.service.CertificateTemplateService;
import com.graduate.certificate.util.PrimaryKeyGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CertificateTemplateServiceImpl extends ServiceImpl<CertificateTemplateMapper, CertificateTemplate> implements CertificateTemplateService {

    @Override
    public IPage<CertificateTemplate> pageTemplates(int current, int size) {
        Page<CertificateTemplate> page = new Page<>(current, size);
        LambdaQueryWrapper<CertificateTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CertificateTemplate::getSortOrder);
        return this.page(page, wrapper);
    }

    @Override
    public void saveTemplate(CertificateTemplate template) {
        if (template.getPkCt() == null) {
            template.setPkCt(PrimaryKeyGenerator.generateCertificateTemplateKey());
        }
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        this.save(template);
    }

    @Override
    public void updateTemplate(CertificateTemplate template) {
        template.setUpdateTime(LocalDateTime.now());
        this.updateById(template);
    }

    @Override
    public void deleteTemplate(String pkCt) {
        this.removeById(pkCt);
    }
}
