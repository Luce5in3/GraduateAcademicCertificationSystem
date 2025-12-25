-- ===================================================
-- 证明申请表
-- 主键设计：业务编码型主键（CA + 年份 + 顺序号）
-- ===================================================
CREATE TABLE `certificate_application`
(
    `pk_ca`                  VARCHAR(25) NOT NULL COMMENT '申请主键（CA+年份+顺序号，如：CA2025000001）',
    `application_no`         VARCHAR(50) NOT NULL COMMENT '申请编号（对外展示编号）',
    `pk_student`             VARCHAR(20) NOT NULL COMMENT '学生ID（student_info.pk_student）',
    `pk_user`                VARCHAR(20) NOT NULL COMMENT '学生用户ID（sys_user.pk_user）',
    `pk_ct`                  VARCHAR(20) NOT NULL COMMENT '证明模板ID（certificate_template.pk_ct）',
    `certificate_type`       VARCHAR(50) NOT NULL COMMENT '证明类型',
    `application_reason`     VARCHAR(500) DEFAULT NULL COMMENT '申请理由',
    `application_data`       TEXT         DEFAULT NULL COMMENT '申请数据（JSON格式，表单填写内容）',
    `status`                 TINYINT      DEFAULT 0 COMMENT '申请状态：0-待审批 1-审批中 2-已通过 3-已拒绝 4-已撤销',
    `current_approval_level` TINYINT      DEFAULT 1 COMMENT '当前审批级别',
    `pk_teacher`             VARCHAR(20)  DEFAULT NULL COMMENT '当前审批人ID（teacher_info.pk_teacher）',
    `certificate_file_url`   VARCHAR(255) DEFAULT NULL COMMENT '生成的证明文件URL',
    `certificate_no`         VARCHAR(50)  DEFAULT NULL COMMENT '证明编号',
    `issue_date`             DATETIME     DEFAULT NULL COMMENT '开具日期',
    `copies`                 INT          DEFAULT 1 COMMENT '申请份数',
    `urgent`                 TINYINT      DEFAULT 0 COMMENT '是否加急：0-否 1-是',
    `reject_reason`          VARCHAR(500) DEFAULT NULL COMMENT '拒绝原因',
    `complete_time`          DATETIME     DEFAULT NULL COMMENT '完成时间',
    `create_time`            DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`            DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk_ca`),
    UNIQUE KEY `uk_application_no` (`application_no`),
    KEY                      `idx_pk_student` (`pk_student`),
    KEY                      `idx_pk_user` (`pk_user`),
    KEY                      `idx_pk_ct` (`pk_ct`),
    KEY                      `idx_status` (`status`),
    KEY                      `idx_pk_teacher` (`pk_teacher`),
    KEY                      `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证明申请表';
