CREATE TABLE `student_info`
(
    `pk_student`        varchar(20) NOT NULL COMMENT '学生主键（S+年份+顺序号，如：S2021000001）',
    `pk_user`           varchar(20) NOT NULL COMMENT '关联用户表ID（sys_user.pk_user）',
    `student_no`        varchar(30) NOT NULL COMMENT '学号',
    `name`              varchar(50) NOT NULL COMMENT '姓名',
    `gender`            tinyint      DEFAULT NULL COMMENT '性别：0-女 1-男',
    `id_card`           varchar(18)  DEFAULT NULL COMMENT '身份证号',
    `college`           varchar(100) DEFAULT NULL COMMENT '学院',
    `major`             varchar(100) DEFAULT NULL COMMENT '专业',
    `class_name`        varchar(50)  DEFAULT NULL COMMENT '班级',
    `grade`             varchar(10)  DEFAULT NULL COMMENT '年级',
    `enrollment_date`   date         DEFAULT NULL COMMENT '入学日期',
    `graduation_date`   date         DEFAULT NULL COMMENT '毕业日期',
    `education_level`   varchar(20)  DEFAULT NULL COMMENT '学历层次：本科/硕士/博士',
    `study_type`        varchar(20)  DEFAULT NULL COMMENT '学习形式：全日制/非全日制',
    `graduation_status` tinyint      DEFAULT '0' COMMENT '毕业状态：0-在读 1-已毕业 2-结业',
    `pk_teacher`        varchar(20)  DEFAULT NULL COMMENT '导师ID（teacher_info.pk_teacher）',
    `create_time`       datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `department`        varchar(100) DEFAULT NULL COMMENT '所属部门',
    `pk_department`     varchar(20)  DEFAULT NULL COMMENT '部门主键(所属系别)',
    `pk_college`        varchar(20)  DEFAULT NULL COMMENT '学院主键',
    PRIMARY KEY (`pk_student`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    UNIQUE KEY `uk_pk_user` (`pk_user`),
    KEY `idx_college` (`college`),
    KEY `idx_major` (`major`),
    KEY `idx_graduation_status` (`graduation_status`),
    KEY `idx_pk_teacher` (`pk_teacher`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='学生信息表';