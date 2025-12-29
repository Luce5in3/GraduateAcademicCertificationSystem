package com.graduate.certificate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduate.certificate.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批记录Mapper
 */
@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {
}
