-- ===================================================
-- 部门 / 系别信息表
-- 主键设计：D + 年份 + 顺序号
-- ===================================================
CREATE TABLE `department_info`
(
    `pk_department`   varchar(20) NOT NULL COMMENT '部门主键（D+年份+顺序号，如：D20250001）',
    `department_code` varchar(20) NOT NULL COMMENT '部门编码（如：CS01、EE02）',
    `department_name` varchar(100) NOT NULL COMMENT '部门名称',
    `pk_college`      varchar(20) NOT NULL COMMENT '所属学院主键（college_info.pk_college）',
    `leader_name`     varchar(50)  DEFAULT NULL COMMENT '部门负责人',
    `phone`           varchar(20)  DEFAULT NULL COMMENT '联系电话',
    `office_addr`     varchar(100) DEFAULT NULL COMMENT '办公地址',
    `sort_order`      int          DEFAULT 0 COMMENT '排序号',
    `is_active`       tinyint      DEFAULT 1 COMMENT '是否启用：0-禁用 1-启用',
    `remark`          varchar(200) DEFAULT NULL COMMENT '备注',
    `create_time`     datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk_department`),
    UNIQUE KEY `uk_department_code` (`department_code`),
    KEY `idx_pk_college` (`pk_college`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
    COMMENT='部门信息表';
