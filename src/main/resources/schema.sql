-- 简历表
CREATE TABLE IF NOT EXISTS resumes (
    resume_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    mime_type VARCHAR(100),
    status VARCHAR(20) DEFAULT 'active',
    analyzed BOOLEAN DEFAULT FALSE,
    analysis_result TEXT,
    upload_time DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_upload_time (upload_time)
);

-- 简历投递申请表
CREATE TABLE IF NOT EXISTS applications (
    application_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    resume_id BIGINT NOT NULL,
    cover_letter TEXT,
    status VARCHAR(20) DEFAULT 'pending',
    apply_time DATETIME NOT NULL,
    review_time DATETIME,
    reviewer_notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_job_id (job_id),
    INDEX idx_resume_id (resume_id),
    INDEX idx_status (status),
    INDEX idx_apply_time (apply_time),
    UNIQUE KEY uk_user_job (user_id, job_id)
);

-- 确保jobs表存在（如果不存在的话）
CREATE TABLE IF NOT EXISTS jobs (
    job_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    job_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    requirements TEXT NOT NULL,
    min_salary DECIMAL(10,2),
    max_salary DECIMAL(10,2),
    location VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    post_date DATE NOT NULL,
    expiration_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_company_id (company_id),
    INDEX idx_is_active (is_active),
    INDEX idx_post_date (post_date),
    INDEX idx_job_type (job_type)
);

-- 确保companies表存在（如果不存在的话）
CREATE TABLE IF NOT EXISTS companies (
    company_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    company_name VARCHAR(255) NOT NULL,
    industry VARCHAR(100),
    company_size VARCHAR(50),
    location VARCHAR(255),
    description TEXT,
    website VARCHAR(255),
    logo_url VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_company_name (company_name)
);

-- 确保users表存在（如果不存在的话）
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
);


-- 教师扩展信息表
CREATE TABLE IF NOT EXISTS teachers (
    teacher_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    teacher_no VARCHAR(50) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    college_id BIGINT,
    role_type VARCHAR(20) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    managed_colleges TEXT,
    managed_majors TEXT,
    status VARCHAR(20) DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_teacher_user_id (user_id),
    INDEX idx_teacher_college_id (college_id),
    INDEX idx_teacher_role_type (role_type)
);

-- 学院表
CREATE TABLE IF NOT EXISTS colleges (
    college_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    college_code VARCHAR(50) NOT NULL UNIQUE,
    college_name VARCHAR(100) NOT NULL,
    dean VARCHAR(50),
    student_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 专业表
CREATE TABLE IF NOT EXISTS majors (
    major_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    college_id BIGINT NOT NULL,
    major_code VARCHAR(50) NOT NULL UNIQUE,
    major_name VARCHAR(100) NOT NULL,
    education_level VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_major_college_id (college_id)
);

-- 学生档案表
CREATE TABLE IF NOT EXISTS student_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    student_no VARCHAR(50) NOT NULL UNIQUE,
    real_name VARCHAR(50) NOT NULL,
    college_id BIGINT,
    major_id BIGINT,
    class_name VARCHAR(100),
    education_level VARCHAR(20),
    graduation_year INT,
    gpa DECIMAL(4,2),
    skills TEXT,
    expected_city VARCHAR(100),
    expected_salary_min INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student_college_id (college_id),
    INDEX idx_student_major_id (major_id),
    INDEX idx_student_graduation_year (graduation_year)
);

-- 就业台账表
CREATE TABLE IF NOT EXISTS employment_ledger (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    college_id BIGINT,
    major_id BIGINT,
    employment_status VARCHAR(30),
    company_name VARCHAR(255),
    position VARCHAR(255),
    salary_range VARCHAR(100),
    employment_city VARCHAR(100),
    further_study_school VARCHAR(255),
    verify_status VARCHAR(20) DEFAULT 'pending',
    verified_by BIGINT,
    verified_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ledger_student_id (student_id),
    INDEX idx_ledger_college_id (college_id),
    INDEX idx_ledger_major_id (major_id),
    INDEX idx_ledger_verify_status (verify_status)
);

-- 就业活动表
CREATE TABLE IF NOT EXISTS activities (
    activity_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    college_id BIGINT,
    teacher_id BIGINT,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    mode VARCHAR(30),
    location VARCHAR(255),
    start_time DATETIME,
    end_time DATETIME,
    max_participants INT,
    current_participants INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'draft',
    poster_url VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 活动报名签到表
CREATE TABLE IF NOT EXISTS activity_registrations (
    registration_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    register_time DATETIME,
    sign_in_time DATETIME,
    sign_in_method VARCHAR(50),
    status VARCHAR(20),
    feedback TEXT,
    rating INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_registration_activity_id (activity_id),
    INDEX idx_registration_student_id (student_id)
);

-- 帮扶记录表
CREATE TABLE IF NOT EXISTS assistance_records (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    difficulty_type VARCHAR(50),
    difficulty_level VARCHAR(20),
    description TEXT,
    assistance_plan TEXT,
    assistance_actions TEXT,
    start_date DATE,
    end_date DATE,
    result TEXT,
    follow_up_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_assistance_student_id (student_id),
    INDEX idx_assistance_teacher_id (teacher_id)
);

-- 预警结果表
CREATE TABLE IF NOT EXISTS early_warning_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    warning_type VARCHAR(50),
    warning_level VARCHAR(20),
    warning_score INT,
    trigger_reason TEXT,
    detection_time DATETIME,
    assigned_to BIGINT,
    handle_status VARCHAR(20) DEFAULT 'pending',
    handle_time DATETIME,
    handle_remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_warning_student_id (student_id),
    INDEX idx_warning_assigned_to (assigned_to),
    INDEX idx_warning_handle_status (handle_status)
);

-- 校企合作申请表
CREATE TABLE IF NOT EXISTS cooperation_applications (
    application_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enterprise_id BIGINT NOT NULL,
    college_id BIGINT NOT NULL,
    cooperation_type VARCHAR(50),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    proposal_url VARCHAR(500),
    expected_start_date DATE,
    expected_students INT,
    status VARCHAR(20) DEFAULT 'pending',
    review_comment TEXT,
    reviewed_by BIGINT,
    signed_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_coop_enterprise_id (enterprise_id),
    INDEX idx_coop_college_id (college_id),
    INDEX idx_coop_status (status)
);

-- 企业人才库表
CREATE TABLE IF NOT EXISTS talent_pool (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    status VARCHAR(20),
    tags VARCHAR(500),
    rating INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_talent_company_id (company_id),
    INDEX idx_talent_applicant_id (applicant_id)
);

-- 为企业表补充教师端审核字段
ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS credit_code VARCHAR(100),
    ADD COLUMN IF NOT EXISTS license_url VARCHAR(500),
    ADD COLUMN IF NOT EXISTS verify_status VARCHAR(20) DEFAULT 'pending',
    ADD COLUMN IF NOT EXISTS verify_remark VARCHAR(500),
    ADD COLUMN IF NOT EXISTS credit_score INT DEFAULT 60;

-- 为职位表补充审核字段
ALTER TABLE jobs
    ADD COLUMN IF NOT EXISTS verify_status VARCHAR(20) DEFAULT 'pending',
    ADD COLUMN IF NOT EXISTS verify_remark VARCHAR(500);
