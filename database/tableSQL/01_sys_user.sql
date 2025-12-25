-- ===================================================
-- 系统用户表（统一用户管理）
-- 主键设计：业务编码型主键（U + 年份 + 顺序号）
-- ===================================================
CREATE TABLE `sys_user`
(
    `pk_user`         VARCHAR(20)  NOT NULL COMMENT '用户主键（U+年份+顺序号，如：U2025000001）',
    `username`        VARCHAR(50)  NOT NULL COMMENT '用户名（学号/工号）',
    `password`        VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `real_name`       VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    `user_type`       TINYINT      NOT NULL COMMENT '用户类型：1-学生 2-教师 3-管理员',
    `email`           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `avatar`          VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status`          TINYINT      DEFAULT 1 COMMENT '账号状态：0-禁用 1-正常',
    `last_login_time` DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip`   VARCHAR(50)  DEFAULT NULL COMMENT '最后登录IP',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`pk_user`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统用户表';
