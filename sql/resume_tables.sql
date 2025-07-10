-- 简历管理系统数据库表结构

-- 用户表（如果已有可复用）
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `phone` VARCHAR(20) UNIQUE,
    `email` VARCHAR(100) UNIQUE,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 简历主表
CREATE TABLE IF NOT EXISTS `resume` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_key` VARCHAR(100) NOT NULL COMMENT '用户唯一标识（手机号或邮箱）',
    `name` VARCHAR(100) NOT NULL COMMENT '简历名称',
    `full_name` VARCHAR(50) COMMENT '姓名',
    `phone` VARCHAR(20) COMMENT '电话',
    `email` VARCHAR(100) COMMENT '邮箱',
    `position` VARCHAR(100) COMMENT '期望职位',
    `work_years` INT COMMENT '工作年限',
    `location` VARCHAR(100) COMMENT '所在地',
    `profile` TEXT COMMENT '个人简介',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认简历',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '软删除标记',
    `share_url` VARCHAR(255) COMMENT '分享链接',
    `view_count` INT DEFAULT 0 COMMENT '查看次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_key` (`user_key`),
    INDEX `idx_is_deleted` (`is_deleted`)
);

-- 工作经历表
CREATE TABLE IF NOT EXISTS `work_experience` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `company` VARCHAR(100) COMMENT '公司名称',
    `position` VARCHAR(100) COMMENT '职位',
    `start_date` DATE COMMENT '开始时间',
    `end_date` DATE COMMENT '结束时间',
    `responsibility` TEXT COMMENT '工作职责',
    `achievement` TEXT COMMENT '工作成果',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_resume_id` (`resume_id`)
);

-- 教育经历表
CREATE TABLE IF NOT EXISTS `education` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `school` VARCHAR(100) COMMENT '学校名称',
    `major` VARCHAR(100) COMMENT '专业',
    `degree` VARCHAR(50) COMMENT '学历',
    `start_date` DATE COMMENT '开始时间',
    `end_date` DATE COMMENT '结束时间',
    `description` TEXT COMMENT '在校经历',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_resume_id` (`resume_id`)
);

-- 项目经验表
CREATE TABLE IF NOT EXISTS `project_experience` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `project_name` VARCHAR(100) COMMENT '项目名称',
    `role` VARCHAR(100) COMMENT '担任角色',
    `start_date` DATE COMMENT '开始时间',
    `end_date` DATE COMMENT '结束时间',
    `description` TEXT COMMENT '项目描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_resume_id` (`resume_id`)
);

-- 技能专长表
CREATE TABLE IF NOT EXISTS `skill` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `skill_name` VARCHAR(100) COMMENT '技能名称',
    `proficiency` INT COMMENT '熟练度(1-5星)',
    `description` TEXT COMMENT '技能描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_resume_id` (`resume_id`)
);

-- 其他信息表
CREATE TABLE IF NOT EXISTS `additional_info` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `type` VARCHAR(50) COMMENT '信息类型',
    `name` VARCHAR(100) COMMENT '名称',
    `start_date` DATE COMMENT '开始时间',
    `end_date` DATE COMMENT '结束时间',
    `description` TEXT COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_resume_id` (`resume_id`)
); 