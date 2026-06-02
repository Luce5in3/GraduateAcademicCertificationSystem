package com.graduate.certificate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.entity.CollegeInfo;

import java.util.List;

/**
 * 学院信息服务接口
 */
public interface CollegeInfoService {

    IPage<CollegeInfo> pageColleges(int current, int size);

    List<CollegeInfo> listAllActive();

    CollegeInfo getById(String pkCollege);

    void create(CollegeInfo college);

    void update(CollegeInfo college);

    void delete(String pkCollege);
}
