package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduate.certificate.entity.DepartmentInfo;
import com.graduate.certificate.mapper.DepartmentInfoMapper;
import com.graduate.certificate.service.DepartmentInfoService;
import com.graduate.certificate.util.PrimaryKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门信息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentInfoServiceImpl implements DepartmentInfoService {

    private final DepartmentInfoMapper departmentInfoMapper;

    @Override
    public IPage<DepartmentInfo> pageDepartments(int current, int size, String pkCollege) {
        LambdaQueryWrapper<DepartmentInfo> wrapper = new LambdaQueryWrapper<>();
        if (pkCollege != null && !pkCollege.isEmpty()) {
            wrapper.eq(DepartmentInfo::getPkCollege, pkCollege);
        }
        wrapper.orderByAsc(DepartmentInfo::getSortOrder)
               .orderByDesc(DepartmentInfo::getCreateTime);
        return departmentInfoMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public List<DepartmentInfo> listByCollege(String pkCollege) {
        LambdaQueryWrapper<DepartmentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepartmentInfo::getPkCollege, pkCollege)
               .eq(DepartmentInfo::getIsActive, 1)
               .orderByAsc(DepartmentInfo::getSortOrder);
        return departmentInfoMapper.selectList(wrapper);
    }

    @Override
    public DepartmentInfo getById(String pkDepartment) {
        return departmentInfoMapper.selectById(pkDepartment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DepartmentInfo department) {
        department.setPkDepartment(PrimaryKeyGenerator.generateDepartmentKey());
        departmentInfoMapper.insert(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DepartmentInfo department) {
        departmentInfoMapper.updateById(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String pkDepartment) {
        departmentInfoMapper.deleteById(pkDepartment);
    }
}
