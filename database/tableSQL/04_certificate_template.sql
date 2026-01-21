-- ===================================================
-- 证明模板表
-- 主键设计：业务编码型主键（CT + 年份 + 顺序号）
-- ===================================================
CREATE TABLE `certificate_template`
(
    `pk_ct`            VARCHAR(20)  NOT NULL COMMENT '模板主键（CT+年份+顺序号，如：CT202500001）',
    `template_name`    VARCHAR(100) NOT NULL COMMENT '模板名称',
    `template_code`    VARCHAR(50)  NOT NULL COMMENT '模板编码',
    `template_type`    VARCHAR(50)  NOT NULL COMMENT '模板类型：学历证明/在读证明/成绩证明等',
    `template_content` TEXT         NOT NULL COMMENT '模板内容（支持变量替换）',
    `required_fields`  VARCHAR(500) DEFAULT NULL COMMENT '必填字段（JSON格式）',
    `approval_flow`    VARCHAR(500) DEFAULT NULL COMMENT '审批流程配置（JSON格式）',
    `is_active`        TINYINT      DEFAULT 1 COMMENT '是否启用：0-禁用 1-启用',
    `sort_order`       INT          DEFAULT 0 COMMENT '排序',
    `create_by`        VARCHAR(20)  DEFAULT NULL COMMENT '创建人ID（sys_user.pk_user）',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`           VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`pk_ct`),
    UNIQUE KEY `uk_template_code` (`template_code`),
    KEY                `idx_template_type` (`template_type`),
    KEY                `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证明模板表';
