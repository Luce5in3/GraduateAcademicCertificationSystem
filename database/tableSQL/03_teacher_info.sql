CREATE TABLE `teacher_info`
(
    `pk_teacher`     varchar(20) NOT NULL COMMENT '教师主键（T+年份+顺序号，如：T2023000001）',
    `pk_user`        varchar(20) NOT NULL COMMENT '关联用户表ID（sys_user.pk_user）',
    `teacher_no`     varchar(30) NOT NULL COMMENT '工号',
    `name`           varchar(50) NOT NULL COMMENT '姓名',
    `gender`         tinyint      DEFAULT NULL COMMENT '性别：0-女 1-男',
    `id_card`        varchar(18)  DEFAULT NULL COMMENT '身份证号',
    `college`        varchar(100) DEFAULT NULL COMMENT '所属学院',
    `department`     varchar(100) DEFAULT NULL COMMENT '所属部门',
    `title`          varchar(50)  DEFAULT NULL COMMENT '职称：教授/副教授/讲师等',
    `position`       varchar(50)  DEFAULT NULL COMMENT '职位：院长/系主任/辅导员等',
    `approval_level` tinyint      DEFAULT '1' COMMENT '审批级别：1-辅导员 2-学院 3-学校',
    `can_approve`    tinyint      DEFAULT '1' COMMENT '是否有审批权限：0-否 1-是',
    `create_time`    datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `pk_department`  varchar(20)  DEFAULT NULL COMMENT '部门主键(所属系别)',
    `pk_college`     varchar(20)  DEFAULT NULL COMMENT '学院主键',
    PRIMARY KEY (`pk_teacher`),
    UNIQUE KEY `uk_teacher_no` (`teacher_no`),
    UNIQUE KEY `uk_pk_user` (`pk_user`),
    KEY `idx_college` (`college`),
    KEY `idx_approval_level` (`approval_level`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='教师信息表';