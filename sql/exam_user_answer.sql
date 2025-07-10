/*
 Navicat Premium Data Transfer

 Source Server         : libolin4
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : 127.0.0.1:3306
 Source Schema         : my_db

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 08/07/2025 15:00:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for exam_user_answer
-- ----------------------------
DROP TABLE IF EXISTS `exam_user_answer`;
CREATE TABLE `exam_user_answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `question_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_user_answer
-- ----------------------------
INSERT INTO `exam_user_answer` VALUES (1, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', 'A', '2025-07-03 18:35:07');
INSERT INTO `exam_user_answer` VALUES (2, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', 'B', '2025-07-03 18:35:07');
INSERT INTO `exam_user_answer` VALUES (3, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', 'D', '2025-07-03 18:35:07');
INSERT INTO `exam_user_answer` VALUES (4, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', 'A', '2025-07-03 18:35:07');
INSERT INTO `exam_user_answer` VALUES (5, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', 'true', '2025-07-03 18:35:07');
INSERT INTO `exam_user_answer` VALUES (16, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, '2025-07-05 15:06:40');
INSERT INTO `exam_user_answer` VALUES (17, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, '2025-07-05 15:06:40');
INSERT INTO `exam_user_answer` VALUES (18, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, '2025-07-05 15:06:40');
INSERT INTO `exam_user_answer` VALUES (19, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, '2025-07-05 15:06:40');
INSERT INTO `exam_user_answer` VALUES (20, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, '2025-07-05 15:06:40');
INSERT INTO `exam_user_answer` VALUES (21, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, '2025-07-05 15:40:40');
INSERT INTO `exam_user_answer` VALUES (22, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, '2025-07-05 15:40:40');
INSERT INTO `exam_user_answer` VALUES (23, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, '2025-07-05 15:40:40');
INSERT INTO `exam_user_answer` VALUES (24, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, '2025-07-05 15:40:40');
INSERT INTO `exam_user_answer` VALUES (25, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, '2025-07-05 15:40:40');
INSERT INTO `exam_user_answer` VALUES (26, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, '2025-07-05 15:41:15');
INSERT INTO `exam_user_answer` VALUES (27, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, '2025-07-05 15:41:15');
INSERT INTO `exam_user_answer` VALUES (28, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, '2025-07-05 15:41:15');
INSERT INTO `exam_user_answer` VALUES (29, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, '2025-07-05 15:41:15');
INSERT INTO `exam_user_answer` VALUES (30, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, '2025-07-05 15:41:15');
INSERT INTO `exam_user_answer` VALUES (31, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, '2025-07-05 15:42:00');
INSERT INTO `exam_user_answer` VALUES (32, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, '2025-07-05 15:42:00');
INSERT INTO `exam_user_answer` VALUES (33, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, '2025-07-05 15:42:00');
INSERT INTO `exam_user_answer` VALUES (34, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, '2025-07-05 15:42:00');
INSERT INTO `exam_user_answer` VALUES (35, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, '2025-07-05 15:42:00');
INSERT INTO `exam_user_answer` VALUES (36, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', 'A', '2025-07-05 15:43:30');
INSERT INTO `exam_user_answer` VALUES (37, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', 'B', '2025-07-05 15:43:30');
INSERT INTO `exam_user_answer` VALUES (38, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', 'D', '2025-07-05 15:43:30');
INSERT INTO `exam_user_answer` VALUES (39, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', 'A', '2025-07-05 15:43:30');
INSERT INTO `exam_user_answer` VALUES (40, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', 'true', '2025-07-05 15:43:30');
INSERT INTO `exam_user_answer` VALUES (41, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, '2025-07-05 16:43:25');
INSERT INTO `exam_user_answer` VALUES (42, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, '2025-07-05 16:43:25');
INSERT INTO `exam_user_answer` VALUES (43, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, '2025-07-05 16:43:25');
INSERT INTO `exam_user_answer` VALUES (44, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, '2025-07-05 16:43:25');
INSERT INTO `exam_user_answer` VALUES (45, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, '2025-07-05 16:43:25');
INSERT INTO `exam_user_answer` VALUES (46, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, '2025-07-05 17:15:04');
INSERT INTO `exam_user_answer` VALUES (47, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, '2025-07-05 17:15:04');
INSERT INTO `exam_user_answer` VALUES (48, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, '2025-07-05 17:15:04');
INSERT INTO `exam_user_answer` VALUES (49, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, '2025-07-05 17:15:04');
INSERT INTO `exam_user_answer` VALUES (50, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, '2025-07-05 17:15:04');
INSERT INTO `exam_user_answer` VALUES (51, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, '2025-07-05 17:16:03');
INSERT INTO `exam_user_answer` VALUES (52, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, '2025-07-05 17:16:03');
INSERT INTO `exam_user_answer` VALUES (53, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, '2025-07-05 17:16:04');
INSERT INTO `exam_user_answer` VALUES (54, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, '2025-07-05 17:16:04');
INSERT INTO `exam_user_answer` VALUES (55, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, '2025-07-05 17:16:04');
INSERT INTO `exam_user_answer` VALUES (56, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, '2025-07-05 17:33:45');
INSERT INTO `exam_user_answer` VALUES (57, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, '2025-07-05 17:33:45');
INSERT INTO `exam_user_answer` VALUES (58, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, '2025-07-05 17:33:45');
INSERT INTO `exam_user_answer` VALUES (59, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, '2025-07-05 17:33:45');
INSERT INTO `exam_user_answer` VALUES (60, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, '2025-07-05 17:33:45');
INSERT INTO `exam_user_answer` VALUES (61, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, '2025-07-05 17:35:24');
INSERT INTO `exam_user_answer` VALUES (62, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, '2025-07-05 17:35:24');
INSERT INTO `exam_user_answer` VALUES (63, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, '2025-07-05 17:35:24');
INSERT INTO `exam_user_answer` VALUES (64, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, '2025-07-05 17:35:24');
INSERT INTO `exam_user_answer` VALUES (65, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, '2025-07-05 17:35:24');
INSERT INTO `exam_user_answer` VALUES (66, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, '2025-07-05 17:40:05');
INSERT INTO `exam_user_answer` VALUES (67, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, '2025-07-05 17:40:05');
INSERT INTO `exam_user_answer` VALUES (68, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, '2025-07-05 17:40:05');
INSERT INTO `exam_user_answer` VALUES (69, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, '2025-07-05 17:40:05');
INSERT INTO `exam_user_answer` VALUES (70, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, '2025-07-05 17:40:05');

SET FOREIGN_KEY_CHECKS = 1;
