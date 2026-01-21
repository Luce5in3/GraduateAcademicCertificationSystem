-- ===================================================
-- 系统配置表
-- 主键设计：业务编码型主键（CFG + 顺序号）
-- ===================================================
CREATE TABLE `system_config`
(
    `pk_config`    VARCHAR(20)  NOT NULL COMMENT '配置主键（CFG+顺序号，如：CFG000001）',
    `config_key`   VARCHAR(100) NOT NULL COMMENT '配置键（唯一业务标识）',
    `config_value` VARCHAR(500) DEFAULT NULL COMMENT '配置值',
    `config_type`  VARCHAR(20)  DEFAULT 'string' COMMENT '配置类型：string/number/boolean/json',
    `config_group` VARCHAR(50)  DEFAULT NULL COMMENT '配置分组',
    `description`  VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
    `is_system`    TINYINT      DEFAULT 0 COMMENT '是否系统配置：0-否 1-是（系统配置不可删除）',
    `sort_order`   INT          DEFAULT 0 COMMENT '排序',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk_config`),
    UNIQUE KEY `uk_config_key` (`config_key`),
    KEY `idx_config_group` (`config_group`),
    KEY `idx_is_system` (`is_system`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统配置表';
