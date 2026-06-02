package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduate.certificate.entity.CollegeInfo;
import com.graduate.certificate.mapper.CollegeInfoMapper;
import com.graduate.certificate.service.CollegeInfoService;
import com.graduate.certificate.util.PrimaryKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学院信息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollegeInfoServiceImpl implements CollegeInfoService {

    private final CollegeInfoMapper collegeInfoMapper;

    @Override
    public IPage<CollegeInfo> pageColleges(int current, int size) {
        LambdaQueryWrapper<CollegeInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CollegeInfo::getSortOrder)
               .orderByDesc(CollegeInfo::getCreateTime);
        return collegeInfoMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public List<CollegeInfo> listAllActive() {
        LambdaQueryWrapper<CollegeInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CollegeInfo::getIsActive, 1)
               .orderByAsc(CollegeInfo::getSortOrder);
        return collegeInfoMapper.selectList(wrapper);
    }

    @Override
    public CollegeInfo getById(String pkCollege) {
        return collegeInfoMapper.selectById(pkCollege);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(CollegeInfo college) {
        college.setPkCollege(PrimaryKeyGenerator.generateCollegeKey());
        collegeInfoMapper.insert(college);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CollegeInfo college) {
        collegeInfoMapper.updateById(college);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String pkCollege) {
        collegeInfoMapper.deleteById(pkCollege);
    }
}
