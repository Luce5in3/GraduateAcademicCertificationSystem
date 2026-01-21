package com.graduate.certificate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduate.certificate.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生信息Mapper
 */
@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {
}
