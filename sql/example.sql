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

 Date: 05/07/2025 19:58:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for example
-- ----------------------------
DROP TABLE IF EXISTS `example`;
CREATE TABLE `example`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `problem_id` int NULL DEFAULT NULL,
  `input` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `problem_id`(`problem_id` ASC) USING BTREE,
  CONSTRAINT `example_ibfk_1` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of example
-- ----------------------------
INSERT INTO `example` VALUES (1, 1, 'nums = [2,7,11,15], target = 9', '[0,1]');
INSERT INTO `example` VALUES (2, 2, 's = \"abcabcbb\"', '3');
INSERT INTO `example` VALUES (3, 3, 's = \"babad\"', '\"bab\"');
INSERT INTO `example` VALUES (4, 4, 'height = [1,8,6,2,5,4,8,3,7]', '49');
INSERT INTO `example` VALUES (5, 5, 'nums = [-1, 0, 1, 2, -1, -4]', '[[-1, 0, 1],[-1, -1, 2]]');
INSERT INTO `example` VALUES (6, 6, 'digits = \"23\"', '[\"ad\", \"ae\", \"af\", \"bd\", \"be\", \"bf\", \"cd\", \"ce\", \"cf\"]');
INSERT INTO `example` VALUES (7, 7, 'head = [1,2,3,4,5], n = 2', '[1,2,3,5]');
INSERT INTO `example` VALUES (8, 8, 's = \"()[]{}\"', 'true');
INSERT INTO `example` VALUES (9, 9, 'l1 = [1,2,4], l2 = [1,3,4]', '[1,1,2,3,4,4]');
INSERT INTO `example` VALUES (10, 10, 'n = 3', '[\"((()))\",\"(()())\",\"(())()\",\"()(())\",\"()()()\"]');
INSERT INTO `example` VALUES (11, 11, 'lists = [[1,4,5],[1,3,4],[2,6]]', '[1,1,2,3,4,4,5,6]');
INSERT INTO `example` VALUES (12, 12, 'nums = [1,2,3]', '[1,3,2]');
INSERT INTO `example` VALUES (13, 13, 's = \"(()\"', '2');
INSERT INTO `example` VALUES (14, 14, 'nums = [4,5,6,7,0,1,2], target = 0', '4');
INSERT INTO `example` VALUES (15, 15, 'candidates = [2,3,6,7], target = 7', '[[7],[2,2,3]]');
INSERT INTO `example` VALUES (16, 16, 'matrix = [[1,2,3],[4,5,6],[7,8,9]]', '[[7,4,1],[8,5,2],[9,6,3]]');
INSERT INTO `example` VALUES (17, 17, 'strs = [\"eat\", \"tea\", \"tan\", \"ate\", \"nat\", \"bat\"]', '[[\"ate\",\"eat\",\"tea\"],[\"nat\",\"tan\"],[\"bat\"]]');
INSERT INTO `example` VALUES (18, 18, 'nums = [-2,1,-3,4,-1,2,1,-5,4]', '6');
INSERT INTO `example` VALUES (19, 19, 'nums = [2,3,1,1,4]', 'true');
INSERT INTO `example` VALUES (20, 20, 'intervals = [[1,3],[2,6],[8,10],[15,18]]', '[[1,6],[8,10],[15,18]]');
INSERT INTO `example` VALUES (21, 21, 'm = 3, n = 2', '3');
INSERT INTO `example` VALUES (22, 22, 'grid = [[1,3,1],[1,5,1],[4,2,1]]', '7');
INSERT INTO `example` VALUES (23, 23, 'lists = [[1,4,5],[1,3,4],[2,6]]', '[1,1,2,3,4,4,5,6]');
INSERT INTO `example` VALUES (24, 23, 'lists = []', '[]');
INSERT INTO `example` VALUES (25, 24, 'nums = [1,2,3]', '[1,3,2]');
INSERT INTO `example` VALUES (26, 24, 'nums = [3,2,1]', '[1,2,3]');
INSERT INTO `example` VALUES (27, 25, 'nums = [4,5,6,7,0,1,2], target = 0', '4');
INSERT INTO `example` VALUES (28, 25, 'nums = [4,5,6,7,0,1,2], target = 3', '-1');
INSERT INTO `example` VALUES (29, 26, 'nums = [1,1,2]', '2, nums = [1,2]');
INSERT INTO `example` VALUES (30, 26, 'nums = [0,0,1,1,1,2,2,3,3,4]', '5, nums = [0,1,2,3,4]');
INSERT INTO `example` VALUES (31, 27, 'prices = [7,1,5,3,6,4]', '5');
INSERT INTO `example` VALUES (32, 27, 'prices = [7,6,4,3,1]', '0');
INSERT INTO `example` VALUES (33, 28, 'haystack = \"hello\", needle = \"ll\"', '2');
INSERT INTO `example` VALUES (34, 28, 'haystack = \"aaaaa\", needle = \"bba\"', '-1');
INSERT INTO `example` VALUES (35, 29, 'dividend = 10, divisor = 3', '3');
INSERT INTO `example` VALUES (36, 29, 'dividend = 7, divisor = -3', '-2');
INSERT INTO `example` VALUES (37, 30, 's = \"barfoothefoobarman\", words = [\"foo\",\"bar\"]', '[0,9]');
INSERT INTO `example` VALUES (38, 30, 's = \"wordgoodgoodgoodbestword\", words = [\"word\",\"good\",\"best\",\"word\"]', '[]');
INSERT INTO `example` VALUES (39, 31, 'nums = [1,2,3]', '[1,3,2]');
INSERT INTO `example` VALUES (40, 31, 'nums = [3,2,1]', '[1,2,3]');
INSERT INTO `example` VALUES (43, 32, '1 2', '3');
INSERT INTO `example` VALUES (44, 32, '10 20', '30');
INSERT INTO `example` VALUES (45, 33, '6 6\n3 3', '6');
INSERT INTO `example` VALUES (46, 34, '4\n1 1 4 4\n2 2 3 3\n3 3 2 2\n4 4 1 1\n2 2', '2');
INSERT INTO `example` VALUES (47, 35, '100 200 150 140 129 134 167 198 200 111\n150', '6');
INSERT INTO `example` VALUES (48, 36, '4\n3 5 6 1', '105');
INSERT INTO `example` VALUES (49, 37, '5\n7\n3 8\n8 1 0\n2 7 4 4\n4 5 2 6 5', '30');
INSERT INTO `example` VALUES (50, 38, '4 100\n90 20 20 80', '2');
INSERT INTO `example` VALUES (51, 39, '5', '8');
INSERT INTO `example` VALUES (52, 40, '99999999999999999999\n1', '100000000000000000000');
INSERT INTO `example` VALUES (53, 41, '5\n7\n3 8\n8 1 0\n2 7 4 4\n4 5 2 6 5', '30');
INSERT INTO `example` VALUES (54, 42, '6', '4');
INSERT INTO `example` VALUES (55, 43, '5\n-2 11 -4 13 -5', '20');
INSERT INTO `example` VALUES (56, 44, '5\n1 2 3 4 5', '15');
INSERT INTO `example` VALUES (57, 45, '17', 'YES');
INSERT INTO `example` VALUES (58, 46, '12 18', '6');
INSERT INTO `example` VALUES (59, 47, '5', '120');
INSERT INTO `example` VALUES (60, 48, '10', '55');
INSERT INTO `example` VALUES (61, 49, '153', 'YES');
INSERT INTO `example` VALUES (62, 50, 'abbccc', 'a:1\\nb:2\\nc:3');

SET FOREIGN_KEY_CHECKS = 1;
