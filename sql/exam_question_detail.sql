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

 Date: 08/07/2025 15:00:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for exam_question_detail
-- ----------------------------
DROP TABLE IF EXISTS `exam_question_detail`;
CREATE TABLE `exam_question_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `question_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `option_a` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `option_b` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `option_c` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `option_d` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_question_detail
-- ----------------------------
INSERT INTO `exam_question_detail` VALUES (1, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q1', 'choice', '在一个微服务架构中，如果需要实现服务之间的异步通信，下面哪种方式最常被使用？', '消息队列（如RabbitMQ、Kafka）', 'HTTP请求', '数据库', '文件共享', 'A', '在微服务架构中，消息队列是实现异步通信的常用方式，它可以解耦服务之间的直接依赖，提高系统的可扩展性和容错性。B选项HTTP请求是同步通信方式，C和D选项不适合用于服务间通信。', '2025-07-05 00:00:00');
INSERT INTO `exam_question_detail` VALUES (2, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q2', 'choice', '在Spring Boot应用中，为了实现服务发现和注册，通常会使用哪个组件？', 'Eureka', 'Zookeeper', 'Consul', 'Nacos', 'A', '在Spring Cloud生态中，Eureka是官方推荐的服务发现与注册组件，虽然Zookeeper、Consul和Nacos也可以实现类似功能，但在Spring Boot应用中Eureka更为常用。', '2025-07-05 00:00:00');
INSERT INTO `exam_question_detail` VALUES (3, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q3', 'choice', '在微服务架构中，为了提高系统的可用性和容错能力，通常会采用哪种设计模式？', '单例模式', '工厂模式', '断路器模式', '代理模式', 'C', '断路器模式是微服务架构中提高系统可用性和容错能力的重要设计模式，它可以在服务出现故障时及时熔断，防止级联失败。A和B选项主要用于对象创建，D选项主要用于访问代理。', '2025-07-05 00:00:00');
INSERT INTO `exam_question_detail` VALUES (4, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q4', 'true_false', '在微服务架构中，每个服务都应该有自己的数据库，以确保数据的一致性和独立性。', NULL, NULL, NULL, NULL, 'true', '这是微服务设计的最佳实践之一，每个服务拥有自己的数据库可以避免数据耦合，确保数据的独立性和一致性，同时也能避免单点故障。', '2025-07-05 00:00:00');
INSERT INTO `exam_question_detail` VALUES (5, '59077f0e-24ec-4800-b09f-4c1978c447e6', 'q5', 'true_false', '在Spring Cloud中，Hystrix可以用来实现API网关的功能。', NULL, NULL, NULL, NULL, 'false', 'Hystrix主要用于实现断路器功能，以提高系统的容错能力，而API网关功能通常由Zuul或Spring Cloud Gateway实现。', '2025-07-05 00:00:00');

SET FOREIGN_KEY_CHECKS = 1;
