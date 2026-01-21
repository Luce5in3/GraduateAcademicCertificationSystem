-- ===================================================
-- 学院信息表
-- 主键设计：C + 年份 + 顺序号
-- ===================================================
CREATE TABLE `college_info`
(
    `pk_college`   varchar(20) NOT NULL COMMENT '学院主键（C+年份+顺序号，如：C20250001）',
    `college_code` varchar(20) NOT NULL COMMENT '学院编码（如：CS、EE、LAW）',
    `college_name` varchar(100) NOT NULL COMMENT '学院名称',
    `dean_name`    varchar(50)  DEFAULT NULL COMMENT '学院院长',
    `phone`        varchar(20)  DEFAULT NULL COMMENT '联系电话',
    `office_addr`  varchar(100) DEFAULT NULL COMMENT '办公地址',
    `sort_order`   int          DEFAULT 0 COMMENT '排序号',
    `is_active`    tinyint      DEFAULT 1 COMMENT '是否启用：0-禁用 1-启用',
    `remark`       varchar(200) DEFAULT NULL COMMENT '备注',
    `create_time`  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk_college`),
    UNIQUE KEY `uk_college_code` (`college_code`),
    UNIQUE KEY `uk_college_name` (`college_name`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
    COMMENT='学院信息表';
