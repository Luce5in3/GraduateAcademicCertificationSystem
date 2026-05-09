-- ===================================================
-- 测试数据初始化脚本
-- 密码统一为：123456（BCrypt加密）
-- ===================================================

-- ===================================================
-- 1. 学院信息
-- ===================================================
INSERT INTO `college_info` (`pk_college`, `college_code`, `college_name`, `dean_name`, `phone`, `office_addr`, `sort_order`, `is_active`, `remark`) VALUES
('C20250001', 'CS', '计算机科学与技术学院', '张明德', '010-62781001', '信息楼A座301', 1, 1, ''),
('C20250002', 'EE', '电子工程学院', '李建国', '010-62781002', '电子楼B座201', 2, 1, ''),
('C20250003', 'LAW', '法学院', '王秀芳', '010-62781003', '文科楼C座401', 3, 1, '');

-- ===================================================
-- 2. 部门/系别信息
-- ===================================================
INSERT INTO `department_info` (`pk_department`, `department_code`, `department_name`, `pk_college`, `leader_name`, `phone`, `office_addr`, `sort_order`, `is_active`, `remark`) VALUES
('D20250001', 'CS01', '软件工程系', 'C20250001', '刘志强', '010-62781101', '信息楼A座302', 1, 1, ''),
('D20250002', 'CS02', '计算机应用系', 'C20250001', '陈伟', '010-62781102', '信息楼A座303', 2, 1, ''),
('D20250003', 'EE01', '通信工程系', 'C20250002', '赵刚', '010-62781201', '电子楼B座202', 1, 1, ''),
('D20250004', 'LAW01', '民商法系', 'C20250003', '孙丽华', '010-62781301', '文科楼C座402', 1, 1, '');

-- ===================================================
-- 3. 系统用户表（密码：123456 -> BCrypt）
-- ===================================================
INSERT INTO `sys_user` (`pk_user`, `username`, `password`, `real_name`, `user_type`, `email`, `phone`, `avatar`, `status`, `last_login_time`, `last_login_ip`, `remark`) VALUES
-- 管理员
('U2025000001', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 3, 'admin@university.edu.cn', '13800000001', NULL, 1, '2026-05-07 09:00:00', '192.168.1.100', '超级管理员'),
-- 教师
('U2025000002', 'T20230001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王建华', 2, 'wangjh@university.edu.cn', '13800000002', NULL, 1, '2026-05-06 14:30:00', '192.168.1.101', '导师-初审'),
('U2025000003', 'T20230002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李明远', 2, 'limy@university.edu.cn', '13800000003', NULL, 1, '2026-05-05 16:00:00', '192.168.1.102', '系主任-复审'),
('U2025000004', 'T20230003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张明德', 2, 'zhangmd@university.edu.cn', '13800000004', NULL, 1, '2026-05-04 10:00:00', '192.168.1.103', '院长-终审'),
-- 学生
('U2025000005', '2021010101', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '陈小明', 1, 'chenxm@stu.university.edu.cn', '13900000001', NULL, 1, '2026-05-07 08:30:00', '10.0.0.50', '在读硕士'),
('U2025000006', '2021010102', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张晓丽', 1, 'zhangxl@stu.university.edu.cn', '13900000002', NULL, 1, '2026-05-06 20:00:00', '10.0.0.51', '在读硕士'),
('U2025000007', '2020010101', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '刘大伟', 1, 'liudw@stu.university.edu.cn', '13900000003', NULL, 1, '2026-05-05 19:00:00', '10.0.0.52', '已毕业硕士'),
('U2025000008', '2022010101', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵雨萱', 1, 'zhaoyx@stu.university.edu.cn', '13900000004', NULL, 1, '2026-05-07 07:00:00', '10.0.0.53', '在读博士'),
('U2025000009', '2021010201', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙浩然', 1, 'sunhr@stu.university.edu.cn', '13900000005', NULL, 1, '2026-05-06 12:00:00', '10.0.0.54', '在读硕士');

-- ===================================================
-- 4. 教师信息表
-- ===================================================
INSERT INTO `teacher_info` (`pk_teacher`, `pk_user`, `teacher_no`, `name`, `gender`, `id_card`, `college`, `department`, `title`, `position`, `approval_level`, `can_approve`, `pk_department`, `pk_college`, `image_url`) VALUES
('T2023000001', 'U2025000002', 'T20230001', '王建华', 1, '110101197001011234', '计算机科学与技术学院', '软件工程系', '副教授', '硕士生导师', 1, 1, 'D20250001', 'C20250001', NULL),
('T2023000002', 'U2025000003', 'T20230002', '李明远', 1, '110101196801021234', '计算机科学与技术学院', '软件工程系', '教授', '系主任', 2, 1, 'D20250001', 'C20250001', NULL),
('T2023000003', 'U2025000004', 'T20230003', '张明德', 1, '110101196501031234', '计算机科学与技术学院', NULL, '教授', '院长', 3, 1, NULL, 'C20250001', NULL);

-- ===================================================
-- 5. 学生信息表
-- ===================================================
INSERT INTO `student_info` (`pk_student`, `pk_user`, `student_no`, `name`, `gender`, `id_card`, `college`, `major`, `class_name`, `grade`, `enrollment_date`, `graduation_date`, `education_level`, `study_type`, `graduation_status`, `pk_teacher`, `department`, `pk_department`, `pk_college`, `image_url`) VALUES
('S2021000001', 'U2025000005', '2021010101', '陈小明', 1, '320102200001011234', '计算机科学与技术学院', '软件工程', '软工2021级1班', '2021', '2021-09-01', NULL, '硕士', '全日制', 0, 'T2023000001', '软件工程系', 'D20250001', 'C20250001', NULL),
('S2021000002', 'U2025000006', '2021010102', '张晓丽', 0, '320102200002021234', '计算机科学与技术学院', '软件工程', '软工2021级1班', '2021', '2021-09-01', NULL, '硕士', '全日制', 0, 'T2023000001', '软件工程系', 'D20250001', 'C20250001', NULL),
('S2020000001', 'U2025000007', '2020010101', '刘大伟', 1, '320102199903031234', '计算机科学与技术学院', '计算机科学与技术', '计科2020级1班', '2020', '2020-09-01', '2023-06-30', '硕士', '全日制', 1, 'T2023000002', '计算机应用系', 'D20250002', 'C20250001', NULL),
('S2022000001', 'U2025000008', '2022010101', '赵雨萱', 0, '320102199804041234', '计算机科学与技术学院', '软件工程', '博士2022级1班', '2022', '2022-09-01', NULL, '博士', '全日制', 0, 'T2023000002', '软件工程系', 'D20250001', 'C20250001', NULL),
('S2021000003', 'U2025000009', '2021010201', '孙浩然', 1, '320102200005051234', '计算机科学与技术学院', '计算机科学与技术', '计科2021级1班', '2021', '2021-09-01', NULL, '硕士', '全日制', 0, 'T2023000001', '计算机应用系', 'D20250002', 'C20250001', NULL);

-- ===================================================
-- 6. 证明模板表
-- ===================================================
INSERT INTO `certificate_template` (`pk_ct`, `template_name`, `template_code`, `template_type`, `template_content`, `required_fields`, `approval_flow`, `is_active`, `sort_order`, `create_by`, `remark`) VALUES
('CT202500001', '在读证明', 'ENROLLMENT_CERT', '在读证明',
 '兹证明 ${name}，性别 ${gender}，身份证号 ${idCard}，系我校 ${college} ${major} 专业 ${educationLevel} 研究生，学号 ${studentNo}，于 ${enrollmentDate} 入学，学制 ${duration} 年，学习形式为 ${studyType}，目前在读。\n\n特此证明。',
 '["name","gender","idCard","college","major","educationLevel","studentNo","enrollmentDate"]',
 '{"levels":[{"level":1,"role":"导师"},{"level":2,"role":"系主任"}]}',
 1, 1, 'U2025000001', '用于办理各类需要在读证明的事务'),

('CT202500002', '学历证明', 'DEGREE_CERT', '学历证明',
 '兹证明 ${name}，性别 ${gender}，身份证号 ${idCard}，于 ${enrollmentDate} 至 ${graduationDate} 在我校 ${college} ${major} 专业攻读 ${educationLevel} 学位，学习形式为 ${studyType}，已于 ${graduationDate} 毕业，准予毕业并授予相应学位。\n\n特此证明。',
 '["name","gender","idCard","college","major","educationLevel","enrollmentDate","graduationDate"]',
 '{"levels":[{"level":1,"role":"导师"},{"level":2,"role":"系主任"},{"level":3,"role":"院长"}]}',
 1, 2, 'U2025000001', '用于已毕业学生开具学历证明'),

('CT202500003', '成绩证明', 'TRANSCRIPT_CERT', '成绩证明',
 '兹证明 ${name}，学号 ${studentNo}，系我校 ${college} ${major} 专业 ${educationLevel} 研究生，在校期间各科成绩如附件所示，学业情况属实。\n\n特此证明。',
 '["name","studentNo","college","major","educationLevel"]',
 '{"levels":[{"level":1,"role":"导师"},{"level":2,"role":"系主任"}]}',
 1, 3, 'U2025000001', '用于出国留学或求职等需要提供成绩的场景'),

('CT202500004', '在职证明（导师）', 'TEACHER_EMPLOYMENT_CERT', '在职证明',
 '兹证明 ${name}，性别 ${gender}，身份证号 ${idCard}，系我校 ${college} ${department} ${title}，工号 ${teacherNo}，自入职以来一直在我校从事教学科研工作，目前在职。\n\n特此证明。',
 '["name","gender","idCard","college","department","title","teacherNo"]',
 '{"levels":[{"level":1,"role":"系主任"},{"level":2,"role":"院长"}]}',
 1, 4, 'U2025000001', '教师在职证明模板');

-- ===================================================
-- 7. 证明申请表（模拟不同状态的申请）
-- ===================================================
INSERT INTO `certificate_application` (`pk_ca`, `application_no`, `pk_student`, `pk_user`, `pk_ct`, `certificate_type`, `application_reason`, `application_data`, `status`, `current_approval_level`, `pk_teacher`, `certificate_file_url`, `certificate_no`, `issue_date`, `copies`, `urgent`, `reject_reason`, `complete_time`, `create_time`) VALUES
-- 状态0：待审批
('CA2026000001', 'CERT-2026-0501-001', 'S2021000001', 'U2025000005', 'CT202500001', '在读证明', '办理出国签证需要', '{"purpose":"签证办理","destination":"美国"}', 0, 1, 'T2023000001', NULL, NULL, NULL, 1, 0, NULL, NULL, '2026-05-01 10:00:00'),
-- 状态1：审批中（初审已过，复审中）
('CA2026000002', 'CERT-2026-0428-001', 'S2021000002', 'U2025000006', 'CT202500001', '在读证明', '办理银行贷款需要', '{"purpose":"银行贷款"}', 1, 2, 'T2023000002', NULL, NULL, NULL, 1, 0, NULL, NULL, '2026-04-28 14:00:00'),
-- 状态2：已通过
('CA2026000003', 'CERT-2026-0420-001', 'S2020000001', 'U2025000007', 'CT202500002', '学历证明', '求职需要学历证明', '{"purpose":"求职","company":"XX科技有限公司"}', 2, 3, NULL, '/certificates/CERT-2026-0420-001.pdf', 'XL-2026-00001', '2026-04-25 09:00:00', 2, 0, NULL, '2026-04-25 09:00:00', '2026-04-20 09:30:00'),
-- 状态3：已拒绝
('CA2026000004', 'CERT-2026-0425-001', 'S2022000001', 'U2025000008', 'CT202500003', '成绩证明', '出国留学申请', '{"purpose":"留学申请","country":"英国"}', 3, 1, 'T2023000002', NULL, NULL, NULL, 1, 1, '申请材料不完整，请补充出国院校录取通知书', NULL, '2026-04-25 16:00:00'),
-- 状态4：已撤销
('CA2026000005', 'CERT-2026-0426-001', 'S2021000003', 'U2025000009', 'CT202500001', '在读证明', '租房需要', '{"purpose":"租房"}', 4, 1, 'T2023000001', NULL, NULL, NULL, 1, 0, NULL, NULL, '2026-04-26 11:00:00'),
-- 加急申请-待审批
('CA2026000006', 'CERT-2026-0507-001', 'S2021000001', 'U2025000005', 'CT202500003', '成绩证明', '紧急出国申请需要成绩单', '{"purpose":"留学申请","country":"新加坡","urgent_reason":"截止日期临近"}', 0, 1, 'T2023000001', NULL, NULL, NULL, 3, 1, NULL, NULL, '2026-05-07 08:00:00');

-- ===================================================
-- 8. 审批记录表
-- ===================================================
INSERT INTO `approval_record` (`pk_ar`, `pk_ca`, `application_no`, `pk_teacher`, `pk_user`, `approver_name`, `approval_level`, `approval_result`, `approval_opinion`, `approval_time`, `time_cost`, `attachment_url`, `ip_address`) VALUES
-- CA2026000002 的初审记录（通过）
('AR20260000001', 'CA2026000002', 'CERT-2026-0428-001', 'T2023000001', 'U2025000002', '王建华', 1, 1, '情况属实，同意通过', '2026-04-29 09:00:00', 1140, NULL, '192.168.1.101'),
-- CA2026000003 的完整审批链（三级审批全部通过）
('AR20260000002', 'CA2026000003', 'CERT-2026-0420-001', 'T2023000001', 'U2025000002', '王建华', 1, 1, '该生已毕业，信息核实无误', '2026-04-21 10:00:00', 1470, NULL, '192.168.1.101'),
('AR20260000003', 'CA2026000003', 'CERT-2026-0420-001', 'T2023000002', 'U2025000003', '李明远', 2, 1, '同意，学历信息已核实', '2026-04-23 14:00:00', 3120, NULL, '192.168.1.102'),
('AR20260000004', 'CA2026000003', 'CERT-2026-0420-001', 'T2023000003', 'U2025000004', '张明德', 3, 1, '批准开具学历证明', '2026-04-25 09:00:00', 2580, NULL, '192.168.1.103'),
-- CA2026000004 的拒绝记录
('AR20260000005', 'CA2026000004', 'CERT-2026-0425-001', 'T2023000002', 'U2025000003', '李明远', 1, 2, '申请材料不完整，请补充出国院校录取通知书', '2026-04-26 10:00:00', 1080, NULL, '192.168.1.102');

-- ===================================================
-- 9. 系统配置表
-- ===================================================
INSERT INTO `system_config` (`pk_config`, `config_key`, `config_value`, `config_type`, `config_group`, `description`, `is_system`, `sort_order`) VALUES
('CFG000001', 'system.name', '研究生学业认证系统', 'string', 'basic', '系统名称', 1, 1),
('CFG000002', 'system.version', '1.0.0', 'string', 'basic', '系统版本号', 1, 2),
('CFG000003', 'approval.max_level', '3', 'number', 'approval', '最大审批级别', 1, 3),
('CFG000004', 'approval.timeout_days', '7', 'number', 'approval', '审批超时天数', 0, 4),
('CFG000005', 'certificate.valid_days', '90', 'number', 'certificate', '证明有效天数', 0, 5),
('CFG000006', 'upload.max_size', '10485760', 'number', 'upload', '文件上传最大大小（字节）', 0, 6),
('CFG000007', 'upload.allowed_types', '["pdf","jpg","png","doc","docx"]', 'json', 'upload', '允许上传的文件类型', 0, 7),
('CFG000008', 'notification.email_enabled', 'true', 'boolean', 'notification', '是否启用邮件通知', 0, 8);

-- ===================================================
-- 10. 操作日志表（示例日志）
-- ===================================================
INSERT INTO `operation_log` (`pk_operation_log`, `pk_user`, `username`, `operation_type`, `operation_module`, `operation_desc`, `request_method`, `request_url`, `ip_address`, `browser`, `os`, `status`, `execute_time`, `create_time`) VALUES
('OPL202600000001', 'U2025000001', 'admin', '登录', '系统登录', '管理员登录系统', 'POST', '/api/auth/login', '192.168.1.100', 'Chrome 125', 'Windows 11', 1, 120, '2026-05-07 09:00:00'),
('OPL202600000002', 'U2025000005', '2021010101', '登录', '系统登录', '学生陈小明登录系统', 'POST', '/api/auth/login', '10.0.0.50', 'Chrome 125', 'Windows 11', 1, 98, '2026-05-07 08:30:00'),
('OPL202600000003', 'U2025000005', '2021010101', '新增', '证明申请', '提交在读证明申请', 'POST', '/api/applications', '10.0.0.50', 'Chrome 125', 'Windows 11', 1, 256, '2026-05-01 10:00:00'),
('OPL202600000004', 'U2025000002', 'T20230001', '审批', '审批管理', '初审通过申请CERT-2026-0428-001', 'POST', '/api/approval/approve', '192.168.1.101', 'Chrome 125', 'Windows 11', 1, 180, '2026-04-29 09:00:00'),
('OPL202600000005', 'U2025000008', '2022010101', '新增', '证明申请', '提交成绩证明申请（加急）', 'POST', '/api/applications', '10.0.0.53', 'Firefox 126', 'macOS 14', 1, 312, '2026-04-25 16:00:00'),
('OPL202600000006', 'U2025000003', 'T20230002', '审批', '审批管理', '拒绝申请CERT-2026-0425-001', 'POST', '/api/approval/reject', '192.168.1.102', 'Chrome 125', 'Windows 11', 1, 145, '2026-04-26 10:00:00'),
('OPL202600000007', 'U2025000009', '2021010201', '修改', '证明申请', '撤销在读证明申请', 'PUT', '/api/applications/CA2026000005/cancel', '10.0.0.54', 'Edge 125', 'Windows 11', 1, 95, '2026-04-27 08:00:00'),
('OPL202600000008', 'U2025000001', 'admin', '新增', '模板管理', '新增在职证明模板', 'POST', '/api/admin/templates', '192.168.1.100', 'Chrome 125', 'Windows 11', 1, 203, '2026-04-15 14:30:00');
