-- ===================================================
-- 审批记录表
-- 主键设计：业务编码型主键（AR + 年份 + 顺序号）
-- ===================================================
CREATE TABLE `approval_record`
(
    `pk_ar`            VARCHAR(30) NOT NULL COMMENT '审批记录主键（AR+年份+顺序号，如：AR20250000001）',
    `pk_ca`            VARCHAR(25) NOT NULL COMMENT '申请ID（certificate_application.pk_ca）',
    `application_no`   VARCHAR(50) NOT NULL COMMENT '申请编号（冗余字段，便于查询）',
    `pk_teacher`       VARCHAR(20) NOT NULL COMMENT '审批人ID（teacher_info.pk_teacher）',
    `pk_user`          VARCHAR(20) NOT NULL COMMENT '审批人用户ID（sys_user.pk_user）',
    `approver_name`    VARCHAR(50) NOT NULL COMMENT '审批人姓名',
    `approval_level`   TINYINT     NOT NULL COMMENT '审批级别：1-初审 2-复审 3-终审',
    `approval_result`  TINYINT     NOT NULL COMMENT '审批结果：1-通过 2-拒绝 3-退回',
    `approval_opinion` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    `approval_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
    `time_cost`        INT          DEFAULT NULL COMMENT '审批耗时（分钟）',
    `attachment_url`   VARCHAR(255) DEFAULT NULL COMMENT '附件URL',
    `ip_address`       VARCHAR(50)  DEFAULT NULL COMMENT '审批IP地址',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`pk_ar`),
    KEY                `idx_pk_ca` (`pk_ca`),
    KEY                `idx_application_no` (`application_no`),
    KEY                `idx_pk_teacher` (`pk_teacher`),
    KEY                `idx_pk_user` (`pk_user`),
    KEY                `idx_approval_level` (`approval_level`),
    KEY                `idx_approval_time` (`approval_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';
