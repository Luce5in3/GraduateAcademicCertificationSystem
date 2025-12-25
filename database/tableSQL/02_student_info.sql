-- ===================================================
-- 学生信息表
-- 主键设计：业务编码型主键（S + 入学年份 + 顺序号）
-- ===================================================
CREATE TABLE `student_info`
(
    `pk_student`        VARCHAR(20) NOT NULL COMMENT '学生主键（S+年份+顺序号，如：S2021000001）',
    `pk_user`           VARCHAR(20) NOT NULL COMMENT '关联用户表ID（sys_user.pk_user）',
    `student_no`        VARCHAR(30) NOT NULL COMMENT '学号',
    `name`              VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender`            TINYINT      DEFAULT NULL COMMENT '性别：0-女 1-男',
    `id_card`           VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
    `college`           VARCHAR(100) DEFAULT NULL COMMENT '学院',
    `major`             VARCHAR(100) DEFAULT NULL COMMENT '专业',
    `class_name`        VARCHAR(50)  DEFAULT NULL COMMENT '班级',
    `grade`             VARCHAR(10)  DEFAULT NULL COMMENT '年级',
    `enrollment_date`   DATE         DEFAULT NULL COMMENT '入学日期',
    `graduation_date`   DATE         DEFAULT NULL COMMENT '毕业日期',
    `education_level`   VARCHAR(20)  DEFAULT NULL COMMENT '学历层次：本科/硕士/博士',
    `study_type`        VARCHAR(20)  DEFAULT NULL COMMENT '学习形式：全日制/非全日制',
    `graduation_status` TINYINT      DEFAULT 0 COMMENT '毕业状态：0-在读 1-已毕业 2-结业',
    `pk_teacher`        VARCHAR(20)  DEFAULT NULL COMMENT '导师ID（teacher_info.pk_teacher）',
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`pk_student`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    UNIQUE KEY `uk_pk_user` (`pk_user`),
    KEY `idx_college` (`college`),
    KEY `idx_major` (`major`),
    KEY `idx_graduation_status` (`graduation_status`),
    KEY `idx_pk_teacher` (`pk_teacher`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学生信息表';
