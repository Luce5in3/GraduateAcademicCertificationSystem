-- ===================================================
-- 操作日志表
-- 主键设计：业务编码型主键（OPL + 年份 + 顺序号）
-- ===================================================
CREATE TABLE `operation_log`
(
    `pk_operation_log` VARCHAR(30) NOT NULL COMMENT '操作日志主键（OPL+年份+顺序号，如：OPL202500000001）',
    `pk_user`          VARCHAR(20)  DEFAULT NULL COMMENT '操作人ID（sys_user.pk_user）',
    `username`         VARCHAR(50)  DEFAULT NULL COMMENT '操作人用户名',
    `operation_type`   VARCHAR(50)  DEFAULT NULL COMMENT '操作类型：登录/新增/修改/删除/审批等',
    `operation_module` VARCHAR(50)  DEFAULT NULL COMMENT '操作模块',
    `operation_desc`   VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
    `request_method`   VARCHAR(10)  DEFAULT NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
    `request_url`      VARCHAR(255) DEFAULT NULL COMMENT '请求URL',
    `request_params`   TEXT         DEFAULT NULL COMMENT '请求参数',
    `response_result`  TEXT         DEFAULT NULL COMMENT '响应结果',
    `ip_address`       VARCHAR(50)  DEFAULT NULL COMMENT 'IP地址',
    `location`         VARCHAR(100) DEFAULT NULL COMMENT '操作地点',
    `browser`          VARCHAR(100) DEFAULT NULL COMMENT '浏览器',
    `os`               VARCHAR(100) DEFAULT NULL COMMENT '操作系统',
    `status`           TINYINT      DEFAULT 1 COMMENT '操作状态：0-失败 1-成功',
    `error_msg`        VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `execute_time`     INT          DEFAULT NULL COMMENT '执行时长（毫秒）',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`pk_operation_log`),
    KEY `idx_pk_user` (`pk_user`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='操作日志表';
