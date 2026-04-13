/*
 Navicat Premium Dump SQL

 Source Server         : 172.20.10.6
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : ai_interview_v2

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 13/04/2026 16:47:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activities
-- ----------------------------
DROP TABLE IF EXISTS `activities`;
CREATE TABLE `activities`  (
  `activity_id` bigint NOT NULL AUTO_INCREMENT,
  `college_id` bigint NULL DEFAULT NULL,
  `teacher_id` bigint NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `mode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `start_time` datetime NULL DEFAULT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `max_participants` int NULL DEFAULT NULL,
  `current_participants` int NULL DEFAULT 0,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT 'draft',
  `poster_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`activity_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for activity_registrations
-- ----------------------------
DROP TABLE IF EXISTS `activity_registrations`;
CREATE TABLE `activity_registrations`  (
  `registration_id` bigint NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `register_time` datetime NULL DEFAULT NULL,
  `sign_in_time` datetime NULL DEFAULT NULL,
  `sign_in_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `rating` int NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`registration_id`) USING BTREE,
  INDEX `idx_registration_activity_id`(`activity_id` ASC) USING BTREE,
  INDEX `idx_registration_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for additional_info
-- ----------------------------
DROP TABLE IF EXISTS `additional_info`;
CREATE TABLE `additional_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '信息类型',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `start_date` date NULL DEFAULT NULL COMMENT '开始时间',
  `end_date` date NULL DEFAULT NULL COMMENT '结束时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_resume_id`(`resume_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ai_interviews
-- ----------------------------
DROP TABLE IF EXISTS `ai_interviews`;
CREATE TABLE `ai_interviews`  (
  `interview_id` int NOT NULL AUTO_INCREMENT,
  `application_id` int NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `evaluation` json NULL COMMENT '评估JSON数据',
  `overall_score` float NULL DEFAULT NULL COMMENT '总分',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `interview_video` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`interview_id`) USING BTREE,
  INDEX `idx_interviews_time`(`start_time` ASC) USING BTREE,
  INDEX `application_id`(`application_id` ASC) USING BTREE,
  CONSTRAINT `ai_interviews_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `applications` (`application_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 55579543 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for applications
-- ----------------------------
DROP TABLE IF EXISTS `applications`;
CREATE TABLE `applications`  (
  `application_id` int NOT NULL AUTO_INCREMENT,
  `job_id` int NOT NULL,
  `user_id` int NOT NULL COMMENT '申请人user_id',
  `apply_time` datetime NOT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ai_evaluation_score` float NULL DEFAULT NULL COMMENT 'AI综合评分',
  `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '企业反馈',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `resume_id` int NULL DEFAULT NULL,
  `interview_time` datetime NULL DEFAULT NULL,
  `rejection_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`application_id`) USING BTREE,
  INDEX `applications_user_fk`(`user_id` ASC) USING BTREE,
  CONSTRAINT `applications_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for assistance_records
-- ----------------------------
DROP TABLE IF EXISTS `assistance_records`;
CREATE TABLE `assistance_records`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  `difficulty_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `difficulty_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `assistance_plan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `assistance_actions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `start_date` date NULL DEFAULT NULL,
  `end_date` date NULL DEFAULT NULL,
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `follow_up_date` date NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_assistance_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_assistance_teacher_id`(`teacher_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for candidate_answer_stats
-- ----------------------------
DROP TABLE IF EXISTS `candidate_answer_stats`;
CREATE TABLE `candidate_answer_stats`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键，自增唯一标识',
  `application_id` int NOT NULL COMMENT '关联的应聘申请ID，外键关联applications表',
  `written_answer_count` int NOT NULL DEFAULT 0 COMMENT '笔试答题数量',
  `written_answer_range` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '笔试答题范围，例如：选择题(1-20)、简答题(21-25)',
  `written_ai_generated` tinyint(1) NOT NULL DEFAULT 1 COMMENT '笔试是否AI生成，1-是，0-否',
  `interview_answer_count` int NOT NULL DEFAULT 0 COMMENT '面试答题数量',
  `interview_answer_range` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '面试答题范围，例如：技术问题(1-5)、行为问题(6-10)',
  `interview_ai_generated` tinyint(1) NOT NULL DEFAULT 1 COMMENT '面试是否AI生成，1-是，0-否',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_candidate_answer_stats_application`(`application_id` ASC) USING BTREE,
  CONSTRAINT `fk_candidate_answer_stats_application` FOREIGN KEY (`application_id`) REFERENCES `applications` (`application_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '应聘者笔试和面试答题统计信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for city
-- ----------------------------
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city`  (
  `city_id` int NOT NULL AUTO_INCREMENT,
  `city_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `province` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `region` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_tier1_city` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`city_id`) USING BTREE,
  INDEX `idx_city_name`(`city_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for colleges
-- ----------------------------
DROP TABLE IF EXISTS `colleges`;
CREATE TABLE `colleges`  (
  `college_id` bigint NOT NULL AUTO_INCREMENT,
  `college_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL,
  `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL,
  `dean` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `student_count` int NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`college_id`) USING BTREE,
  UNIQUE INDEX `college_code`(`college_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for companies
-- ----------------------------
DROP TABLE IF EXISTS `companies`;
CREATE TABLE `companies`  (
  `company_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '关联管理员账号',
  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `industry` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属行业',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `scale` enum('1-50人','51-100人','101-500人','500人以上') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `website` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `logo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `verify_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending',
  `credit_score` int NULL DEFAULT 60,
  `profile_completion` tinyint NOT NULL DEFAULT 0 COMMENT '资料完成度 0-100',
  PRIMARY KEY (`company_id`) USING BTREE,
  UNIQUE INDEX `company_name`(`company_name` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `companies_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for cooperation_applications
-- ----------------------------
DROP TABLE IF EXISTS `cooperation_applications`;
CREATE TABLE `cooperation_applications`  (
  `application_id` bigint NOT NULL AUTO_INCREMENT,
  `enterprise_id` bigint NOT NULL,
  `college_id` bigint NOT NULL,
  `cooperation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `proposal_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `expected_start_date` date NULL DEFAULT NULL,
  `expected_students` int NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT 'pending',
  `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `reviewed_by` bigint NULL DEFAULT NULL,
  `signed_at` datetime NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`application_id`) USING BTREE,
  INDEX `idx_coop_enterprise_id`(`enterprise_id` ASC) USING BTREE,
  INDEX `idx_coop_college_id`(`college_id` ASC) USING BTREE,
  INDEX `idx_coop_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for disc_questions
-- ----------------------------
DROP TABLE IF EXISTS `disc_questions`;
CREATE TABLE `disc_questions`  (
  `question_id` int NOT NULL AUTO_INCREMENT,
  `question_group` int NOT NULL COMMENT '题目组号(1-40)',
  `option_letter` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项字母(A/B/C/D)',
  `option_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项内容',
  `disc_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'DISC类型(D/I/S/C)',
  `is_positive` tinyint(1) NULL DEFAULT 1 COMMENT '是否为正面题目',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`question_id`) USING BTREE,
  INDEX `idx_question_group`(`question_group` ASC) USING BTREE,
  INDEX `idx_disc_type`(`disc_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 161 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'DISC测试题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for disc_test_answers
-- ----------------------------
DROP TABLE IF EXISTS `disc_test_answers`;
CREATE TABLE `disc_test_answers`  (
  `answer_id` bigint NOT NULL AUTO_INCREMENT,
  `record_id` bigint NOT NULL COMMENT '测试记录ID',
  `question_group` int NOT NULL COMMENT '题目组号',
  `selected_option` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选择的选项',
  `disc_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对应的DISC类型',
  `answer_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
  PRIMARY KEY (`answer_id`) USING BTREE,
  INDEX `idx_record_id`(`record_id` ASC) USING BTREE,
  INDEX `idx_question_group`(`question_group` ASC) USING BTREE,
  CONSTRAINT `disc_test_answers_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `disc_test_records` (`record_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 421 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'DISC测试答案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for disc_test_records
-- ----------------------------
DROP TABLE IF EXISTS `disc_test_records`;
CREATE TABLE `disc_test_records`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `test_session` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '测试会话ID',
  `test_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'in_progress' COMMENT '测试状态：in_progress, completed, abandoned',
  `start_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `complete_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `total_questions` int NULL DEFAULT 40 COMMENT '总题数',
  `answered_questions` int NULL DEFAULT 0 COMMENT '已答题数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_test_session`(`test_session` ASC) USING BTREE,
  INDEX `idx_test_status`(`test_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'DISC测试记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for disc_test_results
-- ----------------------------
DROP TABLE IF EXISTS `disc_test_results`;
CREATE TABLE `disc_test_results`  (
  `result_id` bigint NOT NULL AUTO_INCREMENT,
  `record_id` bigint NOT NULL COMMENT '测试记录ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `d_score` int NULL DEFAULT 0 COMMENT 'D型得分',
  `i_score` int NULL DEFAULT 0 COMMENT 'I型得分',
  `s_score` int NULL DEFAULT 0 COMMENT 'S型得分',
  `c_score` int NULL DEFAULT 0 COMMENT 'C型得分',
  `primary_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主要类型',
  `secondary_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '次要类型',
  `personality_profile` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性格档案(如DI, SC等)',
  `detailed_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细分析结果',
  `strengths` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '优势特点',
  `weaknesses` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '改进建议',
  `career_suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '职业建议',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`result_id`) USING BTREE,
  INDEX `idx_record_id`(`record_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_primary_type`(`primary_type` ASC) USING BTREE,
  CONSTRAINT `disc_test_results_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `disc_test_records` (`record_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'DISC测试结果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for disc_type_descriptions
-- ----------------------------
DROP TABLE IF EXISTS `disc_type_descriptions`;
CREATE TABLE `disc_type_descriptions`  (
  `type_id` int NOT NULL AUTO_INCREMENT,
  `disc_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'DISC类型',
  `type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `type_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型描述',
  `characteristics` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '特征描述',
  `strengths` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '优势',
  `weaknesses` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '劣势',
  `work_style` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '工作风格',
  `communication_style` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '沟通风格',
  `management_tips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '管理建议',
  `career_fields` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '适合职业领域',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`type_id`) USING BTREE,
  UNIQUE INDEX `uk_disc_type`(`disc_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'DISC类型描述表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for early_warning_results
-- ----------------------------
DROP TABLE IF EXISTS `early_warning_results`;
CREATE TABLE `early_warning_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `warning_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `warning_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `warning_score` int NULL DEFAULT NULL,
  `trigger_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `detection_time` datetime NULL DEFAULT NULL,
  `assigned_to` bigint NULL DEFAULT NULL,
  `handle_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT 'pending',
  `handle_time` datetime NULL DEFAULT NULL,
  `handle_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_warning_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_warning_assigned_to`(`assigned_to` ASC) USING BTREE,
  INDEX `idx_warning_handle_status`(`handle_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for education
-- ----------------------------
DROP TABLE IF EXISTS `education`;
CREATE TABLE `education`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `school` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校名称',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `degree` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学历',
  `start_date` date NULL DEFAULT NULL COMMENT '开始时间',
  `end_date` date NULL DEFAULT NULL COMMENT '结束时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '在校经历',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_resume_id`(`resume_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for employment_ledger
-- ----------------------------
DROP TABLE IF EXISTS `employment_ledger`;
CREATE TABLE `employment_ledger`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `college_id` bigint NULL DEFAULT NULL,
  `major_id` bigint NULL DEFAULT NULL,
  `employment_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `salary_range` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `employment_city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `further_study_school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `verify_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT 'pending',
  `verified_by` bigint NULL DEFAULT NULL,
  `verified_at` datetime NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ledger_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_ledger_college_id`(`college_id` ASC) USING BTREE,
  INDEX `idx_ledger_major_id`(`major_id` ASC) USING BTREE,
  INDEX `idx_ledger_verify_status`(`verify_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for experience_requirement
-- ----------------------------
DROP TABLE IF EXISTS `experience_requirement`;
CREATE TABLE `experience_requirement`  (
  `experience_id` int NOT NULL AUTO_INCREMENT,
  `experience_range` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `experience_level` int NOT NULL,
  PRIMARY KEY (`experience_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for interview_answers
-- ----------------------------
DROP TABLE IF EXISTS `interview_answers`;
CREATE TABLE `interview_answers`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` int NOT NULL COMMENT '申请ID',
  `interview_result_id` bigint NOT NULL COMMENT '面试结果ID',
  `answers_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户答案JSON',
  `score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '总得分',
  `total_questions` int NULL DEFAULT 0 COMMENT '总题目数',
  `completion_time` int NULL DEFAULT NULL COMMENT '完成时间（分钟）',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始面试时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交面试时间',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'not_started' COMMENT '面试状态：not_started, in_progress, completed',
  `interviewer_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '面试官反馈',
  `technical_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '技术能力评分',
  `communication_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '沟通能力评分',
  `problem_solving_score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '问题解决能力评分',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_application_interview`(`application_id` ASC, `interview_result_id` ASC) USING BTREE,
  INDEX `idx_interview_result_id`(`interview_result_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_interview_answers_app_status`(`application_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_interview_answers_submit_time`(`submit_time` ASC) USING BTREE,
  CONSTRAINT `fk_interview_answers_result` FOREIGN KEY (`interview_result_id`) REFERENCES `interview_results` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '面试答案记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for interview_evaluations
-- ----------------------------
DROP TABLE IF EXISTS `interview_evaluations`;
CREATE TABLE `interview_evaluations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评估ID',
  `application_id` int NOT NULL COMMENT '申请ID',
  `interview_id` bigint NULL DEFAULT NULL COMMENT '面试ID',
  `written_test_answer_id` bigint NULL DEFAULT NULL COMMENT '笔试答案ID',
  `job_position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '应聘岗位',
  `candidate_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '候选人ID',
  `interview_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '面试得分',
  `written_test_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '笔试得分',
  `weighted_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '权重后的综合得分',
  `interview_evaluation_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '面试评估详情JSON',
  `written_test_evaluation_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '笔试评估详情JSON',
  `improvement_areas` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '需要改进的领域JSON数组',
  `recommendations` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学习建议JSON数组',
  `resources_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '推荐资源JSON数组',
  `evaluation_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '评估状态：pending, completed, failed',
  `dify_workflow_run_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Dify工作流运行ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_interview_id`(`interview_id` ASC) USING BTREE,
  INDEX `idx_written_test_answer_id`(`written_test_answer_id` ASC) USING BTREE,
  INDEX `idx_candidate_id`(`candidate_id` ASC) USING BTREE,
  INDEX `idx_evaluation_status`(`evaluation_status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '面试评估结果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for interview_progress
-- ----------------------------
DROP TABLE IF EXISTS `interview_progress`;
CREATE TABLE `interview_progress`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` int NOT NULL COMMENT '申请ID，关联applications表',
  `written_test_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'not_started' COMMENT '笔试状态：not_started, in_progress, completed, passed, failed',
  `written_test_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '笔试得分',
  `written_test_passed` tinyint(1) NULL DEFAULT NULL COMMENT '笔试是否通过',
  `written_test_completed_at` timestamp NULL DEFAULT NULL COMMENT '笔试完成时间',
  `written_test_result_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '笔试详细结果JSON',
  `interview_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'not_started' COMMENT '面试状态：not_started, scheduled, in_progress, completed, passed, failed',
  `interview_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '面试得分',
  `interview_passed` tinyint(1) NULL DEFAULT NULL COMMENT '面试是否通过',
  `interview_scheduled_at` timestamp NULL DEFAULT NULL COMMENT '面试安排时间',
  `interview_completed_at` timestamp NULL DEFAULT NULL COMMENT '面试完成时间',
  `interview_result_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '面试详细结果JSON',
  `overall_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '整体状态：pending, written_test, interview, completed, rejected',
  `final_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '最终结果：pending, passed, failed',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注信息',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_written_test_status`(`written_test_status` ASC) USING BTREE,
  INDEX `idx_interview_status`(`interview_status` ASC) USING BTREE,
  INDEX `idx_overall_status`(`overall_status` ASC) USING BTREE,
  INDEX `idx_final_result`(`final_result` ASC) USING BTREE,
  INDEX `idx_progress_composite`(`application_id` ASC, `overall_status` ASC, `final_result` ASC) USING BTREE,
  CONSTRAINT `fk_interview_progress_application` FOREIGN KEY (`application_id`) REFERENCES `applications` (`application_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '面试进度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for interview_questions
-- ----------------------------
DROP TABLE IF EXISTS `interview_questions`;
CREATE TABLE `interview_questions`  (
  `question_id` int NOT NULL AUTO_INCREMENT,
  `job_id` int NOT NULL,
  `question_type` enum('技术','行为','情景模拟','通用') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `reference_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `difficulty_level` enum('简单','中等','困难') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`question_id`) USING BTREE,
  INDEX `idx_job_id`(`job_id` ASC) USING BTREE,
  CONSTRAINT `fk_question_job` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for interview_results
-- ----------------------------
DROP TABLE IF EXISTS `interview_results`;
CREATE TABLE `interview_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` int NOT NULL COMMENT '申请ID',
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `job_position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '职位名称',
  `job_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '职位描述',
  `candidate_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '候选人姓名',
  `questions_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '面试题目JSON',
  `total_questions` int NULL DEFAULT 5 COMMENT '总题目数',
  `workflow_run_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Dify工作流运行ID',
  `generation_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '生成状态：pending, success, failed',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_resume_id`(`resume_id` ASC) USING BTREE,
  INDEX `idx_generation_status`(`generation_status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_interview_results_app_status`(`application_id` ASC, `generation_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '面试结果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for interview_scores
-- ----------------------------
DROP TABLE IF EXISTS `interview_scores`;
CREATE TABLE `interview_scores`  (
  `score_id` int NOT NULL AUTO_INCREMENT,
  `interview_id` int NOT NULL,
  `technical_ability` float NOT NULL COMMENT '技术能力',
  `learning_ability` float NOT NULL COMMENT '学习能力',
  `team_collaboration` float NOT NULL COMMENT '团队协作能力',
  `problem_solving` float NOT NULL COMMENT '问题解决能力',
  `communication_expression` float NOT NULL COMMENT '沟通表达能力',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`score_id`) USING BTREE,
  INDEX `interview_id`(`interview_id` ASC) USING BTREE,
  CONSTRAINT `interview_scores_ibfk_1` FOREIGN KEY (`interview_id`) REFERENCES `ai_interviews` (`interview_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `interview_scores_chk_1` CHECK (`technical_ability` between 0 and 100),
  CONSTRAINT `interview_scores_chk_2` CHECK (`learning_ability` between 0 and 100),
  CONSTRAINT `interview_scores_chk_3` CHECK (`team_collaboration` between 0 and 100),
  CONSTRAINT `interview_scores_chk_4` CHECK (`problem_solving` between 0 and 100),
  CONSTRAINT `interview_scores_chk_5` CHECK (`communication_expression` between 0 and 100)
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for jobs
-- ----------------------------
DROP TABLE IF EXISTS `jobs`;
CREATE TABLE `jobs`  (
  `job_id` int NOT NULL AUTO_INCREMENT,
  `company_id` int NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_type` enum('全职','兼职','实习') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_duty` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_requirements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `min_salary` int NULL DEFAULT NULL,
  `max_salary` int NULL DEFAULT NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '职位是否有效',
  `post_date` date NOT NULL,
  `expiration_date` date NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `education_requirement` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `experience_requirement` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `job_skills` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`job_id`) USING BTREE,
  INDEX `idx_jobs_company`(`company_id` ASC) USING BTREE,
  CONSTRAINT `jobs_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `companies` (`company_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for majors
-- ----------------------------
DROP TABLE IF EXISTS `majors`;
CREATE TABLE `majors`  (
  `major_id` bigint NOT NULL AUTO_INCREMENT,
  `college_id` bigint NOT NULL,
  `major_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL,
  `major_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL,
  `education_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`major_id`) USING BTREE,
  UNIQUE INDEX `major_code`(`major_code` ASC) USING BTREE,
  INDEX `idx_major_college_id`(`college_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for position_skill
-- ----------------------------
DROP TABLE IF EXISTS `position_skill`;
CREATE TABLE `position_skill`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `position_id` int NOT NULL,
  `skill_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `position_id`(`position_id` ASC, `skill_id` ASC) USING BTREE,
  INDEX `skill_id`(`skill_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 262141 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for problem
-- ----------------------------
DROP TABLE IF EXISTS `problem`;
CREATE TABLE `problem`  (
  `id` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_favorite` tinyint(1) NULL DEFAULT 0,
  `status` enum('未尝试','尝试中','通过') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '未尝试',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for project_experience
-- ----------------------------
DROP TABLE IF EXISTS `project_experience`;
CREATE TABLE `project_experience`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `experience_type` enum('project','work','internship','volunteer') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'project' COMMENT '经历类型',
  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司/组织名称',
  `project_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '项目名称',
  `role` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '担任角色',
  `start_date` date NULL DEFAULT NULL COMMENT '开始时间',
  `end_date` date NULL DEFAULT NULL COMMENT '结束时间',
  `is_current` tinyint NULL DEFAULT 0 COMMENT '是否至今',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '项目描述',
  `achievements` json NULL COMMENT '成果JSON',
  `skills_used` json NULL COMMENT '使用的技能',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_resume_id`(`resume_id` ASC) USING BTREE,
  INDEX `idx_experience_type`(`experience_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for recommendation_logs
-- ----------------------------
DROP TABLE IF EXISTS `recommendation_logs`;
CREATE TABLE `recommendation_logs`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `job_id` int NOT NULL,
  `algorithm_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '推荐算法类型：user_cf, item_cf, content_based等',
  `score` decimal(5, 3) NULL DEFAULT NULL COMMENT '推荐得分',
  `position_in_list` int NULL DEFAULT NULL COMMENT '在推荐列表中的位置',
  `is_clicked` tinyint(1) NULL DEFAULT 0 COMMENT '是否被点击',
  `is_applied` tinyint(1) NULL DEFAULT 0 COMMENT '是否被申请',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_rec_logs_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_rec_logs_job`(`job_id` ASC) USING BTREE,
  INDEX `idx_rec_logs_algorithm`(`algorithm_type` ASC) USING BTREE,
  INDEX `idx_rec_logs_time`(`created_at` ASC) USING BTREE,
  CONSTRAINT `recommendation_logs_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job_position` (`position_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `recommendation_logs_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for recruitmenttable
-- ----------------------------
DROP TABLE IF EXISTS `recruitmenttable`;
CREATE TABLE `recruitmenttable`  (
  `id` int NOT NULL,
  `position_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职位名称，对应job_title',
  `experience_requirement` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经验要求',
  `education_requirement` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学历要求',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '职位描述，对应job_tag',
  `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司名称',
  `industry_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '行业名称，对应company_industry',
  `company_size` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司规模',
  `scale_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司规模类型，对应company_size_category',
  `status_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '融资状态，对应financing_status',
  `city_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市名称，对应standardized_city',
  `province` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `salary` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '薪资描述',
  `max_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '最高月薪(千)，对应monthly_upper_K',
  `min_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '最低月薪(千)，对应monthly_lower_K',
  `avg_salary` decimal(10, 2) NULL DEFAULT NULL COMMENT '平均年薪(万)，对应annual_mid_W',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '职位信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`  (
  `report_id` int NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `report` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `resume_id` int NOT NULL COMMENT '外键关联简历',
  `job_id` int NOT NULL COMMENT '外键关联职位',
  PRIMARY KEY (`report_id`) USING BTREE,
  INDEX `fk_resume`(`resume_id` ASC) USING BTREE,
  INDEX `fk_job`(`job_id` ASC) USING BTREE,
  CONSTRAINT `fk_job` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_resume` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`resume_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for resource_recommendations
-- ----------------------------
DROP TABLE IF EXISTS `resource_recommendations`;
CREATE TABLE `resource_recommendations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
  `interview_evaluation_id` bigint NOT NULL COMMENT '面试评估ID',
  `application_id` int NOT NULL COMMENT '申请ID',
  `resource_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源名称',
  `resource_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源类型：book, course, article, video, practice',
  `resource_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '资源链接',
  `resource_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '资源描述',
  `improvement_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '对应的改进领域',
  `priority` int NULL DEFAULT 1 COMMENT '推荐优先级 1-5',
  `is_verified` tinyint(1) NULL DEFAULT 0 COMMENT '资源链接是否已验证可用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_interview_evaluation_id`(`interview_evaluation_id` ASC) USING BTREE,
  INDEX `idx_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_resource_type`(`resource_type` ASC) USING BTREE,
  INDEX `idx_improvement_area`(`improvement_area` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE,
  INDEX `idx_is_verified`(`is_verified` ASC) USING BTREE,
  CONSTRAINT `resource_recommendations_ibfk_1` FOREIGN KEY (`interview_evaluation_id`) REFERENCES `interview_evaluations` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '资源推荐表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resume
-- ----------------------------
DROP TABLE IF EXISTS `resume`;
CREATE TABLE `resume`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '简历ID',
  `user_id` int NOT NULL COMMENT '关联users表（简历所有者）',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '简历名称（用户自定义，如\"后端开发简历\"）',
  `full_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '期望职位',
  `work_years` tinyint NULL DEFAULT 0 COMMENT '工作年限',
  `expected_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '期望城市',
  `expected_salary_min` int NULL DEFAULT NULL COMMENT '期望薪资下限',
  `expected_salary_max` int NULL DEFAULT NULL COMMENT '期望薪资上限',
  `expected_industry` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '期望行业',
  `profile` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个人简介/自我评价',
  `template_id` int NULL DEFAULT NULL COMMENT '使用的简历模板ID，NULL表示无模板',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认简历',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '软删除标记',
  `view_count` int NULL DEFAULT 0 COMMENT '被查看次数',
  `download_count` int NULL DEFAULT 0 COMMENT '被下载次数',
  `share_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分享码',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'draft' COMMENT '简历状态：draft, published, archived',
  `template_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板类型',
  `template_config` json NULL COMMENT '模板配置参数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_default`(`is_default` ASC) USING BTREE,
  INDEX `idx_expected_city`(`expected_city` ASC) USING BTREE,
  INDEX `idx_expected_salary`(`expected_salary_min` ASC, `expected_salary_max` ASC) USING BTREE,
  INDEX `idx_position`(`position` ASC) USING BTREE,
  CONSTRAINT `resume_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '简历主表（核心摘要信息）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resume_scores
-- ----------------------------
DROP TABLE IF EXISTS `resume_scores`;
CREATE TABLE `resume_scores`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `report_id` int NOT NULL,
  `total_score` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '总分',
  `tech_match` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '技术匹配度得分',
  `experience_match` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '经验匹配度得分',
  `education_match` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '教育匹配度得分',
  `potential_match` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '发展潜力匹配度得分',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `report_id`(`report_id` ASC) USING BTREE,
  CONSTRAINT `resume_scores_ibfk_1` FOREIGN KEY (`report_id`) REFERENCES `report` (`report_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for resume_templates
-- ----------------------------
DROP TABLE IF EXISTS `resume_templates`;
CREATE TABLE `resume_templates`  (
  `template_id` int NOT NULL AUTO_INCREMENT,
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '模板名称',
  `template_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '模板类型：latex, html, markdown',
  `template_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '模板文件路径',
  `preview_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '预览图片路径',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL COMMENT '模板描述',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT 'general' COMMENT '模板分类：it, business, academic等',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`template_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '简历模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resumes
-- ----------------------------
DROP TABLE IF EXISTS `resumes`;
CREATE TABLE `resumes`  (
  `resume_id` int NOT NULL AUTO_INCREMENT,
  `applicant_id` int NOT NULL,
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_size` int NULL DEFAULT NULL COMMENT '文件大小(KB)',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
  `upload_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `parsed_data` json NULL,
  `parse_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '解析状态',
  `parse_time` datetime NULL DEFAULT NULL COMMENT '解析完成时间',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '解析失败原因',
  `retry_count` int NULL DEFAULT 0 COMMENT '解析重试次数',
  `filled_resume_id` bigint NULL DEFAULT NULL COMMENT '解析后填充的简历ID',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '软删除标记',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'upload' COMMENT '来源',
  PRIMARY KEY (`resume_id`) USING BTREE,
  INDEX `idx_parse_status`(`parse_status` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE,
  INDEX `idx_upload_date`(`upload_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for score_weights
-- ----------------------------
DROP TABLE IF EXISTS `score_weights`;
CREATE TABLE `score_weights`  (
  `weight_id` int NOT NULL AUTO_INCREMENT,
  `job_id` int NOT NULL,
  `technical_weight` decimal(5, 2) NOT NULL DEFAULT 0.40 COMMENT '专业技术能力权重',
  `learning_weight` decimal(5, 2) NOT NULL DEFAULT 0.20 COMMENT '学习能力权重',
  `team_weight` decimal(5, 2) NOT NULL DEFAULT 0.15 COMMENT '团队协作权重',
  `problem_solving_weight` decimal(5, 2) NOT NULL DEFAULT 0.15 COMMENT '问题解决权重',
  `communication_weight` decimal(5, 2) NOT NULL DEFAULT 0.10 COMMENT '沟通表达权重',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`weight_id`) USING BTREE,
  UNIQUE INDEX `idx_job_weight`(`job_id` ASC) USING BTREE,
  CONSTRAINT `fk_weights_job` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `chk_communication_weight` CHECK (`communication_weight` between 0 and 1),
  CONSTRAINT `chk_learning_weight` CHECK (`learning_weight` between 0 and 1),
  CONSTRAINT `chk_problem_solving_weight` CHECK (`problem_solving_weight` between 0 and 1),
  CONSTRAINT `chk_team_weight` CHECK (`team_weight` between 0 and 1),
  CONSTRAINT `chk_technical_weight` CHECK (`technical_weight` between 0 and 1),
  CONSTRAINT `chk_weights_sum` CHECK (((((`technical_weight` + `learning_weight`) + `team_weight`) + `problem_solving_weight`) + `communication_weight`) = 1.00)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for skill
-- ----------------------------
DROP TABLE IF EXISTS `skill`;
CREATE TABLE `skill`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `skill_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '技能名称',
  `proficiency` int NULL DEFAULT NULL COMMENT '熟练度(1-5星)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '技能描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_resume_id`(`resume_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for student_profile
-- ----------------------------
DROP TABLE IF EXISTS `student_profile`;
CREATE TABLE `student_profile`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '关联users表',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '学号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '真实姓名',
  `gender` enum('male','female','other') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `birth_date` date NULL DEFAULT NULL,
  `college` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '所属学院',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '专业',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '班级',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '年级',
  `education_level` enum('专科','本科','硕士','博士') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '学历',
  `graduation_year` year NULL DEFAULT NULL COMMENT '毕业年份',
  `profile_completion` tinyint NULL DEFAULT 0 COMMENT '资料完整度 0-100',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_student_no`(`student_no` ASC) USING BTREE,
  INDEX `idx_college`(`college` ASC) USING BTREE,
  INDEX `idx_major`(`major` ASC) USING BTREE,
  CONSTRAINT `student_profile_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '学生基本信息表（仅身份信息，技能/GPA等放简历）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for system_logs
-- ----------------------------
DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE `system_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `table_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表名',
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作用户ID',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '操作详情',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_table_name`(`table_name` ASC) USING BTREE,
  INDEX `idx_operation`(`operation` ASC) USING BTREE,
  INDEX `idx_record_id`(`record_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for talent_pool
-- ----------------------------
DROP TABLE IF EXISTS `talent_pool`;
CREATE TABLE `talent_pool`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_id` bigint NOT NULL,
  `applicant_id` bigint NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `rating` int NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_talent_company_id`(`company_id` ASC) USING BTREE,
  INDEX `idx_talent_applicant_id`(`applicant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for teachers
-- ----------------------------
DROP TABLE IF EXISTS `teachers`;
CREATE TABLE `teachers`  (
  `teacher_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `teacher_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL,
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `college_id` bigint NULL DEFAULT NULL,
  `role_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `managed_colleges` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `managed_majors` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `profile_completion` tinyint NOT NULL DEFAULT 0 COMMENT '资料完成度 0-100',
  PRIMARY KEY (`teacher_id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_teacher_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_teacher_college_id`(`college_id` ASC) USING BTREE,
  INDEX `idx_teacher_role_type`(`role_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for test_questions
-- ----------------------------
DROP TABLE IF EXISTS `test_questions`;
CREATE TABLE `test_questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `test_result_id` bigint NOT NULL COMMENT '测试结果ID',
  `question_id` int NOT NULL COMMENT '题库中的题目ID',
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容',
  `english_question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '英文题目',
  `question_type` enum('single','multiple','text') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目类型',
  `options` json NULL COMMENT '选项列表',
  `correct_answer` json NOT NULL COMMENT '正确答案',
  `chinese_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '中文答案解析',
  `english_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '英文答案解析',
  `difficulty` enum('简单','中等','困难') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '难度级别',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '技术分类',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_test_result_id`(`test_result_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_difficulty`(`difficulty` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  CONSTRAINT `test_questions_ibfk_1` FOREIGN KEY (`test_result_id`) REFERENCES `test_results` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '测试题目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for test_results
-- ----------------------------
DROP TABLE IF EXISTS `test_results`;
CREATE TABLE `test_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '测试结果ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '测试类别代码(backend/frontend/dataAnalysis)',
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '测试类别名称',
  `score` int NOT NULL COMMENT '测试分数(0-100)',
  `total_questions` int NOT NULL COMMENT '总题目数',
  `correct_answers` int NOT NULL COMMENT '正确答案数',
  `duration` int NOT NULL COMMENT '测试用时(秒)',
  `completed_at` datetime NOT NULL COMMENT '完成时间',
  `analysis_report` json NULL COMMENT '能力分析报告',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_completed_at`(`completed_at` ASC) USING BTREE,
  INDEX `idx_score`(`score` ASC) USING BTREE,
  CONSTRAINT `fk_test_results_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '测试结果主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_answers
-- ----------------------------
DROP TABLE IF EXISTS `user_answers`;
CREATE TABLE `user_answers`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '答题记录ID',
  `test_result_id` bigint NOT NULL COMMENT '测试结果ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `user_answer` json NULL COMMENT '用户答案',
  `is_correct` tinyint(1) NOT NULL COMMENT '是否正确',
  `answer_time` int NULL DEFAULT NULL COMMENT '答题用时(秒)',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_test_result_id`(`test_result_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  INDEX `idx_is_correct`(`is_correct` ASC) USING BTREE,
  CONSTRAINT `user_answers_ibfk_1` FOREIGN KEY (`test_result_id`) REFERENCES `test_results` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户答题记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_job_applications
-- ----------------------------
DROP TABLE IF EXISTS `user_job_applications`;
CREATE TABLE `user_job_applications`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `job_id` int NOT NULL,
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT 'pending / viewed / interview / accepted / rejected / withdrawn',
  `cover_letter` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '求职信',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_job_app`(`user_id` ASC, `job_id` ASC) USING BTREE,
  INDEX `idx_user_apps_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_apps_job`(`job_id` ASC) USING BTREE,
  INDEX `idx_user_apps_time`(`apply_time` ASC) USING BTREE,
  INDEX `idx_user_apps_status`(`status` ASC) USING BTREE,
  CONSTRAINT `user_job_applications_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job_position` (`position_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_job_applications_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_job_collection
-- ----------------------------
DROP TABLE IF EXISTS `user_job_collection`;
CREATE TABLE `user_job_collection`  (
  `collection_id` int NOT NULL AUTO_INCREMENT COMMENT '收藏记录唯一ID',
  `user_id` int NOT NULL COMMENT '关联用户ID',
  `job_id` int NOT NULL COMMENT '关联职位ID（对应recruitmenttable的id）',
  `collect_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除（1=已取消，0=正常）',
  PRIMARY KEY (`collection_id`) USING BTREE,
  UNIQUE INDEX `uk_user_job`(`user_id` ASC, `job_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_job_id`(`job_id` ASC) USING BTREE,
  CONSTRAINT `user_job_collection_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `recruitmenttable` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_job_collection_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-职位收藏关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_job_views
-- ----------------------------
DROP TABLE IF EXISTS `user_job_views`;
CREATE TABLE `user_job_views`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `job_id` int NOT NULL,
  `view_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `duration` int NULL DEFAULT 0 COMMENT '浏览时长(秒)',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览来源：search, recommend, similar等',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_views_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_views_job`(`job_id` ASC) USING BTREE,
  INDEX `idx_user_views_time`(`view_time` ASC) USING BTREE,
  CONSTRAINT `user_job_views_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job_position` (`position_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_job_views_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_preferences
-- ----------------------------
DROP TABLE IF EXISTS `user_preferences`;
CREATE TABLE `user_preferences`  (
  `user_id` int NOT NULL,
  `preferred_cities` json NULL COMMENT '偏好城市列表，JSON格式',
  `expected_salary_min` decimal(10, 2) NULL DEFAULT NULL COMMENT '期望薪资下限',
  `expected_salary_max` decimal(10, 2) NULL DEFAULT NULL COMMENT '期望薪资上限',
  `preferred_industries` json NULL COMMENT '偏好行业列表，JSON格式',
  `work_experience_years` int NULL DEFAULT 0 COMMENT '工作经验年数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`) USING BTREE,
  CONSTRAINT `user_preferences_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_search_history
-- ----------------------------
DROP TABLE IF EXISTS `user_search_history`;
CREATE TABLE `user_search_history`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `search_keywords` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `search_filters` json NULL COMMENT '搜索过滤条件，JSON格式',
  `result_count` int NULL DEFAULT 0 COMMENT '搜索结果数量',
  `search_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_search_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_search_time`(`search_time` ASC) USING BTREE,
  INDEX `idx_user_search_keywords`(`search_keywords`(100) ASC) USING BTREE,
  CONSTRAINT `user_search_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `profile_completed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '资料是否完善：0-未完善，1-已完善',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for video_analysis_config
-- ----------------------------
DROP TABLE IF EXISTS `video_analysis_config`;
CREATE TABLE `video_analysis_config`  (
  `config_id` int NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置描述',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`config_id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频分析配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for video_analysis_tasks
-- ----------------------------
DROP TABLE IF EXISTS `video_analysis_tasks`;
CREATE TABLE `video_analysis_tasks`  (
  `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务ID',
  `interview_id` bigint NOT NULL COMMENT '面试ID',
  `video_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频文件路径',
  `transcript` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '转录文本',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'processing' COMMENT '任务状态：processing, completed, failed',
  `result_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '分析结果JSON',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`task_id`) USING BTREE,
  INDEX `idx_interview_id`(`interview_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频分析任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for written_test_answers
-- ----------------------------
DROP TABLE IF EXISTS `written_test_answers`;
CREATE TABLE `written_test_answers`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` int NOT NULL COMMENT '申请ID，关联applications表',
  `written_test_result_id` bigint NOT NULL COMMENT '笔试题目ID，关联written_test_results表',
  `answers_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户答案JSON数据',
  `score` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '总得分',
  `total_questions` int NULL DEFAULT 0 COMMENT '总题目数',
  `correct_answers` int NULL DEFAULT 0 COMMENT '正确答案数',
  `wrong_answers` int NULL DEFAULT 0 COMMENT '错误答案数',
  `completion_time` int NULL DEFAULT 0 COMMENT '完成时间（秒）',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始答题时间',
  `submit_time` timestamp NULL DEFAULT NULL COMMENT '提交答题时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'not_started' COMMENT '答题状态：not_started, in_progress, completed',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_application_test`(`application_id` ASC, `written_test_result_id` ASC) USING BTREE,
  INDEX `idx_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_written_test_result_id`(`written_test_result_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_score`(`score` ASC) USING BTREE,
  INDEX `idx_test_answers_composite`(`application_id` ASC, `status` ASC, `score` ASC) USING BTREE,
  CONSTRAINT `fk_test_answers_application` FOREIGN KEY (`application_id`) REFERENCES `applications` (`application_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_test_answers_result` FOREIGN KEY (`written_test_result_id`) REFERENCES `written_test_results` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔试答案结果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for written_test_results
-- ----------------------------
DROP TABLE IF EXISTS `written_test_results`;
CREATE TABLE `written_test_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_id` int NOT NULL COMMENT '申请ID，关联applications表',
  `resume_id` int NOT NULL COMMENT '简历ID，关联resumes表',
  `job_position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职位名称',
  `candidate_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '候选人所属领域',
  `questions_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '生成的题目JSON数据',
  `total_questions` int NULL DEFAULT 20 COMMENT '总题目数',
  `choice_questions` int NULL DEFAULT 10 COMMENT '选择题数量',
  `true_false_questions` int NULL DEFAULT 10 COMMENT '判断题数量',
  `total_score` int NULL DEFAULT 100 COMMENT '总分',
  `workflow_run_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Dify工作流运行ID',
  `generation_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '生成状态：pending, success, failed',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_resume_id`(`resume_id` ASC) USING BTREE,
  INDEX `idx_workflow_run_id`(`workflow_run_id` ASC) USING BTREE,
  INDEX `idx_generation_status`(`generation_status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_written_test_composite`(`application_id` ASC, `generation_status` ASC, `created_at` ASC) USING BTREE,
  CONSTRAINT `fk_written_test_application` FOREIGN KEY (`application_id`) REFERENCES `applications` (`application_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_written_test_resume` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`resume_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔试结果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 清洗后job
-- ----------------------------
DROP TABLE IF EXISTS `清洗后job`;
CREATE TABLE `清洗后job`  (
  `职位名称` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `公司名称` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `工作地点` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `发布时间` time NULL DEFAULT NULL,
  `职位描述或任职要求` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `公司福利` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `公司规模` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `公司类型` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `公司标签` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `城市` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `地区` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `工作经验要求` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `学历要求` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL,
  `十三薪等` int NULL DEFAULT NULL,
  `最低年薪` int NULL DEFAULT NULL,
  `最高年薪` int NULL DEFAULT NULL,
  `月薪最小值` int NULL DEFAULT NULL,
  `月薪最大值` int NULL DEFAULT NULL,
  `发布时间月` int NULL DEFAULT NULL,
  `发布时间日` int NULL DEFAULT NULL,
  `发布时间时` int NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- View structure for v_interview_evaluation_summary
-- ----------------------------
DROP VIEW IF EXISTS `v_interview_evaluation_summary`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_interview_evaluation_summary` AS select `ie`.`id` AS `evaluation_id`,`ie`.`application_id` AS `application_id`,`ie`.`job_position` AS `job_position`,`ie`.`candidate_id` AS `candidate_id`,`ie`.`interview_score` AS `interview_score`,`ie`.`written_test_score` AS `written_test_score`,`ie`.`weighted_score` AS `weighted_score`,`ie`.`evaluation_status` AS `evaluation_status`,`ie`.`created_at` AS `evaluated_at`,count(`rr`.`id`) AS `resource_count`,group_concat(distinct `rr`.`resource_type` separator ',') AS `resource_types` from (`interview_evaluations` `ie` left join `resource_recommendations` `rr` on((`ie`.`id` = `rr`.`interview_evaluation_id`))) group by `ie`.`id`,`ie`.`application_id`,`ie`.`job_position`,`ie`.`candidate_id`,`ie`.`interview_score`,`ie`.`written_test_score`,`ie`.`weighted_score`,`ie`.`evaluation_status`,`ie`.`created_at`;

-- ----------------------------
-- Procedure structure for GetEvaluationStatistics
-- ----------------------------
DROP PROCEDURE IF EXISTS `GetEvaluationStatistics`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetEvaluationStatistics`()
BEGIN
    SELECT 
        COUNT(*) as total_evaluations,
        SUM(CASE WHEN evaluation_status = 'completed' THEN 1 ELSE 0 END) as completed_evaluations,
        SUM(CASE WHEN evaluation_status = 'pending' THEN 1 ELSE 0 END) as pending_evaluations,
        SUM(CASE WHEN evaluation_status = 'failed' THEN 1 ELSE 0 END) as failed_evaluations,
        AVG(interview_score) as avg_interview_score,
        AVG(written_test_score) as avg_written_test_score,
        AVG(weighted_score) as avg_weighted_score
    FROM interview_evaluations
    WHERE evaluation_status = 'completed';
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table interview_evaluations
-- ----------------------------
DROP TRIGGER IF EXISTS `tr_interview_evaluations_after_insert`;
delimiter ;;
CREATE TRIGGER `tr_interview_evaluations_after_insert` AFTER INSERT ON `interview_evaluations` FOR EACH ROW BEGIN
    -- 记录评估创建日志
    INSERT INTO system_logs (table_name, operation, record_id, created_at)
    VALUES ('interview_evaluations', 'INSERT', NEW.id, NOW());
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
