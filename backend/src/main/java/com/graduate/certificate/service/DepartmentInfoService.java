package com.graduate.certificate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.entity.DepartmentInfo;

import java.util.List;

/**
 * 部门信息服务接口
 */
public interface DepartmentInfoService {

    IPage<DepartmentInfo> pageDepartments(int current, int size, String pkCollege);

    List<DepartmentInfo> listByCollege(String pkCollege);

    DepartmentInfo getById(String pkDepartment);

    void create(DepartmentInfo department);

    void update(DepartmentInfo department);

    void delete(String pkDepartment);
}
