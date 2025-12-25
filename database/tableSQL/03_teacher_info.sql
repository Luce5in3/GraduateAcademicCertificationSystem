-- ===================================================
-- 教师信息表
-- 主键设计：业务编码型主键（T + 入职年份 + 顺序号）
-- ===================================================
CREATE TABLE `teacher_info`
(
    `pk_teacher`     VARCHAR(20) NOT NULL COMMENT '教师主键（T+年份+顺序号，如：T2023000001）',
    `pk_user`        VARCHAR(20) NOT NULL COMMENT '关联用户表ID（sys_user.pk_user）',
    `teacher_no`     VARCHAR(30) NOT NULL COMMENT '工号',
    `name`           VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender`         TINYINT      DEFAULT NULL COMMENT '性别：0-女 1-男',
    `id_card`        VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
    `college`        VARCHAR(100) DEFAULT NULL COMMENT '所属学院',
    `department`     VARCHAR(100) DEFAULT NULL COMMENT '所属部门',
    `title`          VARCHAR(50)  DEFAULT NULL COMMENT '职称：教授/副教授/讲师等',
    `position`       VARCHAR(50)  DEFAULT NULL COMMENT '职位：院长/系主任/辅导员等',
    `approval_level` TINYINT      DEFAULT 1 COMMENT '审批级别：1-初审 2-复审 3-终审',
    `can_approve`    TINYINT      DEFAULT 1 COMMENT '是否有审批权限：0-否 1-是',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk_teacher`),
    UNIQUE KEY `uk_teacher_no` (`teacher_no`),
    UNIQUE KEY `uk_pk_user` (`pk_user`),
    KEY `idx_college` (`college`),
    KEY `idx_approval_level` (`approval_level`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='教师信息表';
