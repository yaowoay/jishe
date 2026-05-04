-- 插入测试用户数据
INSERT IGNORE INTO users (user_id, username, email, password, role, full_name, phone, is_active) VALUES
(1, 'testuser', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdEBnxPRKYaVBMIu', 'applicant', '测试用户', '13800138000', true),
(2, 'testcompany', 'company@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdEBnxPRKYaVBMIu', 'company', '测试公司', '13800138001', true);

-- 插入测试公司数据
INSERT IGNORE INTO companies (company_id, user_id, company_name, industry, company_size, location, description) VALUES
(1, 2, '测试科技有限公司', '互联网', '100-500人', '北京市', '一家专注于AI技术的创新公司');

-- 插入测试职位数据
INSERT IGNORE INTO jobs (job_id, company_id, title, job_type, description, requirements, min_salary, max_salary, location, is_active, post_date) VALUES
(1, 1, 'Java开发工程师', '全职', '负责后端系统开发和维护', '3年以上Java开发经验，熟悉Spring框架', 10000.00, 18000.00, '北京市', true, CURDATE()),
(2, 1, '前端开发工程师', '全职', '负责前端页面开发', '熟悉Vue.js、React等前端框架', 8000.00, 15000.00, '北京市', true, CURDATE()),
(3, 1, '实习生', '实习', '协助开发团队完成项目', '计算机相关专业在校生', 3000.00, 5000.00, '北京市', true, CURDATE());


-- 插入测试学院与专业数据
INSERT IGNORE INTO colleges (college_id, college_code, college_name, dean, student_count) VALUES
(1, 'CS', '计算机与信息工程学院', '张院长', 1200),
(2, 'EE', '电子工程学院', '李院长', 980);

INSERT IGNORE INTO majors (major_id, college_id, major_code, major_name, education_level) VALUES
(1, 1, 'CS001', '计算机科学与技术', '本科'),
(2, 1, 'SE001', '软件工程', '本科'),
(3, 2, 'EE001', '电子信息工程', '本科');

-- 插入教师端测试用户
INSERT IGNORE INTO users (user_id, username, email, password, role, full_name, phone, is_active) VALUES
(3, 'testteacher', 'teacher@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdEBnxPRKYaVBMIu', 'teacher', '测试教师', '13800138002', true);

INSERT IGNORE INTO teachers (teacher_id, user_id, teacher_no, real_name, college_id, role_type, phone, email, status) VALUES
(1, 3, 'T2026001', '测试教师', 1, 'admin', '13800138002', 'teacher@example.com', 'active');

-- 插入学生档案与就业台账测试数据
INSERT IGNORE INTO student_profile (id, user_id, student_no, real_name, college_id, major_id, class_name, education_level, graduation_year, gpa, skills, expected_city, expected_salary_min) VALUES
(1, 101, '20220001', '张三', 1, 1, '计科2201', '本科', 2026, 3.75, 'Java,Spring Boot,MySQL', '郑州', 7000),
(2, 102, '20220002', '李四', 1, 2, '软工2201', '本科', 2026, 3.42, 'Vue,JavaScript,Node.js', '杭州', 8000);

INSERT IGNORE INTO employment_ledger (id, student_id, college_id, major_id, employment_status, company_name, position, salary_range, employment_city, verify_status) VALUES
(1, 1, 1, 1, '已就业', '测试科技有限公司', 'Java开发工程师', '8k-12k', '郑州', 'approved'),
(2, 2, 1, 2, '待就业', NULL, NULL, NULL, NULL, 'pending');

-- 插入预警与帮扶测试数据
INSERT IGNORE INTO early_warning_results (id, student_id, warning_type, warning_level, warning_score, trigger_reason, detection_time, assigned_to, handle_status) VALUES
(1, 2, '就业进度滞后', 'high', 85, '连续30天无投递记录', NOW(), 3, 'pending');

INSERT IGNORE INTO assistance_records (record_id, student_id, teacher_id, difficulty_type, difficulty_level, description, assistance_plan, assistance_actions, start_date, follow_up_date, result) VALUES
(1, 2, 3, '就业困难', 'high', '学生缺少面试经验', '安排简历指导和模拟面试', '已进行第一次辅导', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), '跟进中');
