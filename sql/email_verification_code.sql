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

 Date: 08/07/2025 15:00:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for email_verification_code
-- ----------------------------
DROP TABLE IF EXISTS `email_verification_code`;
CREATE TABLE `email_verification_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱地址',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-未使用，1-已使用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_email_status`(`email` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮箱验证码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_verification_code
-- ----------------------------
INSERT INTO `email_verification_code` VALUES (1, '2415808269@qq.com', '905135', '2025-07-03 22:56:11', '2025-07-03 23:06:11', 0);
INSERT INTO `email_verification_code` VALUES (2, '3167967427@qq.com', '125423', '2025-07-03 22:56:22', '2025-07-03 23:06:22', 0);
INSERT INTO `email_verification_code` VALUES (3, '2415808269@qq.com', '337444', '2025-07-03 22:59:28', '2025-07-03 23:09:28', 0);
INSERT INTO `email_verification_code` VALUES (4, '3167967427@qq.com', '681093', '2025-07-03 23:00:07', '2025-07-03 23:10:07', 0);
INSERT INTO `email_verification_code` VALUES (5, '2415808269@qq.com', '329320', '2025-07-03 23:09:20', '2025-07-03 23:19:20', 1);
INSERT INTO `email_verification_code` VALUES (6, '2415808269@qq.com', '347454', '2025-07-07 16:38:54', '2025-07-07 16:48:54', 0);

SET FOREIGN_KEY_CHECKS = 1;
