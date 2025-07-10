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

 Date: 08/07/2025 15:00:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for exam_judgment_detail
-- ----------------------------
DROP TABLE IF EXISTS `exam_judgment_detail`;
CREATE TABLE `exam_judgment_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `question_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `correct_answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_correct` tinyint NULL DEFAULT NULL,
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_judgment_detail
-- ----------------------------
INSERT INTO `exam_judgment_detail` VALUES (1, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', 'A', 'A', 1, '回答正确，消息队列是微服务架构中实现异步通信的常用方式。', '2025-07-05 00:00:00');
INSERT INTO `exam_judgment_detail` VALUES (2, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', 'B', 'A', 0, '回答错误，在Spring Boot应用中通常使用Eureka实现服务发现和注册。', '2025-07-05 00:00:00');
INSERT INTO `exam_judgment_detail` VALUES (3, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', 'D', 'C', 0, '回答错误，断路器模式是微服务架构中提高系统可用性和容错能力的常用设计模式。', '2025-07-05 00:00:00');
INSERT INTO `exam_judgment_detail` VALUES (4, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', 'false', 'true', 0, '回答错误，在微服务架构中每个服务应该有自己的数据库，这是最佳实践之一。', '2025-07-05 00:00:00');
INSERT INTO `exam_judgment_detail` VALUES (5, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', 'true', 'false', 0, '回答错误，Hystrix主要用于实现断路器功能，API网关通常使用Zuul或Spring Cloud Gateway。', '2025-07-05 00:00:00');
INSERT INTO `exam_judgment_detail` VALUES (7, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, NULL, 0, '', '2025-07-05 17:33:45');
INSERT INTO `exam_judgment_detail` VALUES (8, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, NULL, 0, '', '2025-07-05 17:33:45');
INSERT INTO `exam_judgment_detail` VALUES (9, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, NULL, 0, '', '2025-07-05 17:33:45');
INSERT INTO `exam_judgment_detail` VALUES (10, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, NULL, 0, '', '2025-07-05 17:33:45');
INSERT INTO `exam_judgment_detail` VALUES (11, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, NULL, 0, '', '2025-07-05 17:33:45');
INSERT INTO `exam_judgment_detail` VALUES (12, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', NULL, 'A', 0, 'B选项是同步通信，而题干要求异步通信', '2025-07-05 17:40:05');
INSERT INTO `exam_judgment_detail` VALUES (13, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', NULL, 'A', 0, '虽然B、C、D也可以用于服务发现，但在Spring生态中最常用的是Eureka', '2025-07-05 17:40:05');
INSERT INTO `exam_judgment_detail` VALUES (14, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', NULL, 'C', 0, 'A和B选项主要用于对象创建和管理，D选项主要用于代理访问，而C选项专门用于容错', '2025-07-05 17:40:05');
INSERT INTO `exam_judgment_detail` VALUES (15, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', NULL, 'true', 0, '题干中没有绝对化的词语，是一个常见的最佳实践', '2025-07-05 17:40:05');
INSERT INTO `exam_judgment_detail` VALUES (16, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', NULL, 'false', 0, 'Hystrix主要功能是断路器，而不是API网关', '2025-07-05 17:40:05');

SET FOREIGN_KEY_CHECKS = 1;
