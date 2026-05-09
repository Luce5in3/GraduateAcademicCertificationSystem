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
CREATE TABLE `student_info` (
                                `pk_student` varchar(20) NOT NULL COMMENT '学生主键（S+年份+顺序号，如：S2021000001）',
                                `pk_user` varchar(20) NOT NULL COMMENT '关联用户表ID（sys_user.pk_user）',
                                `student_no` varchar(30) NOT NULL COMMENT '学号',
                                `name` varchar(50) NOT NULL COMMENT '姓名',
                                `gender` tinyint DEFAULT NULL COMMENT '性别：0-女 1-男',
                                `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
                                `college` varchar(100) DEFAULT NULL COMMENT '学院',
                                `major` varchar(100) DEFAULT NULL COMMENT '专业',
                                `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
                                `grade` varchar(10) DEFAULT NULL COMMENT '年级',
                                `enrollment_date` date DEFAULT NULL COMMENT '入学日期',
                                `graduation_date` date DEFAULT NULL COMMENT '毕业日期',
                                `education_level` varchar(20) DEFAULT NULL COMMENT '学历层次：本科/硕士/博士',
                                `study_type` varchar(20) DEFAULT NULL COMMENT '学习形式：全日制/非全日制',
                                `graduation_status` tinyint DEFAULT '0' COMMENT '毕业状态：0-在读 1-已毕业 2-结业',
                                `pk_teacher` varchar(20) DEFAULT NULL COMMENT '导师ID（teacher_info.pk_teacher）',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `department` varchar(100) DEFAULT NULL COMMENT '所属部门',
                                `pk_department` varchar(20) DEFAULT NULL COMMENT '部门主键(所属系别)',
                                `pk_college` varchar(20) DEFAULT NULL COMMENT '学院主键',
                                `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户头像url',
                                PRIMARY KEY (`pk_student`),
                                UNIQUE KEY `uk_student_no` (`student_no`),
                                UNIQUE KEY `uk_pk_user` (`pk_user`),
                                KEY `idx_college` (`college`),
                                KEY `idx_major` (`major`),
                                KEY `idx_graduation_status` (`graduation_status`),
                                KEY `idx_pk_teacher` (`pk_teacher`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生信息表';CREATE TABLE `teacher_info` (
                                                                                                                        `pk_teacher` varchar(20) NOT NULL COMMENT '教师主键（T+年份+顺序号，如：T2023000001）',
                                                                                                                        `pk_user` varchar(20) NOT NULL COMMENT '关联用户表ID（sys_user.pk_user）',
                                                                                                                        `teacher_no` varchar(30) NOT NULL COMMENT '工号',
                                                                                                                        `name` varchar(50) NOT NULL COMMENT '姓名',
                                                                                                                        `gender` tinyint DEFAULT NULL COMMENT '性别：0-女 1-男',
                                                                                                                        `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
                                                                                                                        `college` varchar(100) DEFAULT NULL COMMENT '所属学院',
                                                                                                                        `department` varchar(100) DEFAULT NULL COMMENT '所属部门',
                                                                                                                        `title` varchar(50) DEFAULT NULL COMMENT '职称：教授/副教授/讲师等',
                                                                                                                        `position` varchar(50) DEFAULT NULL COMMENT '职位：院长/系主任/辅导员等',
                                                                                                                        `approval_level` tinyint DEFAULT '1' COMMENT '审批级别：1-初审 2-复审 3-终审',
                                                                                                                        `can_approve` tinyint DEFAULT '1' COMMENT '是否有审批权限：0-否 1-是',
                                                                                                                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                                                                                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                                                                                        `pk_department` varchar(20) DEFAULT NULL COMMENT '部门主键(所属系别)',
                                                                                                                        `pk_college` varchar(20) DEFAULT NULL COMMENT '学院主键',
                                                                                                                        `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户头像url',
                                                                                                                        PRIMARY KEY (`pk_teacher`),
                                                                                                                        UNIQUE KEY `uk_teacher_no` (`teacher_no`),
                                                                                                                        UNIQUE KEY `uk_pk_user` (`pk_user`),
                                                                                                                        KEY `idx_college` (`college`),
                                                                                                                        KEY `idx_approval_level` (`approval_level`)
                                                                                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师信息表';-- ===================================================
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


