/*
 Navicat Premium Dump SQL

 Source Server         : Mysql
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : ai_interview_v2

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 17/04/2026 13:49:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  `template` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `LOCATION` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `skill` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '技能标签/技能列表',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_default`(`is_default` ASC) USING BTREE,
  INDEX `idx_expected_city`(`expected_city` ASC) USING BTREE,
  INDEX `idx_expected_salary`(`expected_salary_min` ASC, `expected_salary_max` ASC) USING BTREE,
  INDEX `idx_position`(`position` ASC) USING BTREE,
  CONSTRAINT `resume_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '简历主表（核心摘要信息）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of resume
-- ----------------------------
INSERT INTO `resume` VALUES (3, 40, 'qqq', '强强强强', '', '', NULL, '', 0, NULL, NULL, NULL, NULL, '具备丰富的专业经验和扎实的技术基础，熟练掌握相关技术栈和业务流程。注重团队协作和持续学习，具备良好的沟通能力和问题解决能力，能够在快节奏的工作环境中高效完成任务。', NULL, 0, 1, 0, 0, NULL, '2026-04-14 00:06:29', '2026-04-14 00:08:33', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (4, 40, '钱钱钱', '钱钱钱', '', '', NULL, '', 0, NULL, NULL, NULL, NULL, '具备丰富的专业经验和扎实的技术基础，熟练掌握相关技术栈和业务流程。注重团队协作和持续学习，具备良好的沟通能力和问题解决能力，能够在快节奏的工作环境中高效完成任务。', NULL, 0, 0, 0, 0, NULL, '2026-04-14 00:08:58', '2026-04-14 00:09:07', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (5, 37, '123', '123', '231', '213', NULL, '231', 0, NULL, NULL, NULL, NULL, '1234456', NULL, 0, 0, 0, 0, NULL, '2026-04-14 09:48:40', '2026-04-14 09:48:40', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (6, 37, '123', '123', '', '', NULL, '231', 0, NULL, NULL, NULL, NULL, '', NULL, 0, 0, 0, 0, NULL, '2026-04-14 09:49:18', '2026-04-14 09:49:18', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (7, 37, '123 - 副本', '123', '231', '213', NULL, '231', 0, NULL, NULL, NULL, NULL, '', NULL, 0, 1, 0, 0, 'a645501ac3054bf0b00382f655807662', '2026-04-14 09:48:40', '2026-04-14 09:50:59', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (8, 31, '后端开发简历', '张三', '13800138000', 'zhangsan@example.com', 'avatar1.jpg', '后端开发工程师', 8, '北京', 14000, 21000, '互联网', '拥有8年后端开发工程师相关经验，熟悉主流技术栈，具备良好的团队协作能力和解决问题的能力。', NULL, 1, 0, 51, 25, 'SHARE89124', '2026-03-20 05:34:19', '2026-04-17 13:38:02', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (9, 31, '后端开发简历', '张三', '13800138000', 'zhangsan@example.com', 'avatar1.jpg', '后端开发工程师', 8, '北京', 14000, 21000, '互联网', '拥有8年后端开发工程师相关经验，熟悉主流技术栈，具备良好的团队协作能力和解决问题的能力。', NULL, 1, 0, 51, 25, 'SHARE89124', '2026-03-20 05:34:19', '2026-04-17 13:38:50', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (10, 34, '前端开发简历', '李四', '13800138001', 'lisi@example.com', 'avatar2.jpg', '前端开发工程师', 1, '上海', 10000, 18000, '金融科技', '拥有1年前端开发工程师相关经验，熟悉主流技术栈，具备良好的团队协作能力和解决问题的能力。', NULL, 0, 0, 170, 44, 'SHARE11697', '2026-03-28 05:34:19', '2026-04-17 13:38:50', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (11, 35, '大数据开发简历', '王五', '13800138002', 'wangwu@example.com', 'avatar3.jpg', '大数据开发工程师', 6, '广州', 10000, 17000, '电商', '拥有6年大数据开发工程师相关经验，熟悉主流技术栈，具备良好的团队协作能力和解决问题的能力。', NULL, 0, 0, 127, 23, 'SHARE76006', '2026-04-16 05:34:19', '2026-04-17 13:38:50', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (12, 36, '算法工程师简历', '赵六', '13800138003', 'zhaoliu@example.com', 'avatar4.jpg', '算法工程师', 4, '深圳', 12000, 15000, '人工智能', '拥有4年算法工程师相关经验，熟悉主流技术栈，具备良好的团队协作能力和解决问题的能力。', NULL, 0, 0, 198, 26, 'SHARE49719', '2026-03-19 05:34:19', '2026-04-17 13:38:50', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (13, 37, '运维工程师简历', '钱七', '13800138004', 'qianqi@example.com', 'avatar5.jpg', '运维工程师', 7, '杭州', 14000, 21000, '企业服务', '拥有7年运维工程师相关经验，熟悉主流技术栈，具备良好的团队协作能力和解决问题的能力。', NULL, 0, 0, 109, 42, 'SHARE48525', '2026-04-14 05:34:19', '2026-04-17 13:38:50', 'draft', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `resume` VALUES (14, 30, '后端开发简历', '张三', '13800138000', 'zhangsan@example.com', 'avatar1.jpg', '后端开发工程师', 8, '北京', 14000, 21000, '互联网', '拥有8年后端开发工程师相关经验，熟悉主流技术栈，具备良好的团队协作能力和解决问题的能力。', NULL, 1, 0, 51, 25, 'SHARE89124', '2026-03-20 05:34:19', '2026-04-17 13:43:37', 'draft', NULL, NULL, NULL, NULL, NULL, 'Java, SpringBoot, MySQL, Redis, MyBatis, 微服务, 分布式');

SET FOREIGN_KEY_CHECKS = 1;
