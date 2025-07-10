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

 Date: 08/07/2025 15:01:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of problem
-- ----------------------------
INSERT INTO `problem` VALUES (1, '两数之和', '给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那两个整数，并返回他们的数组下标。', 1, '通过');
INSERT INTO `problem` VALUES (2, '无重复字符的最长子串', '给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。', 1, '通过');
INSERT INTO `problem` VALUES (3, '最长回文子串', '给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。', 1, '通过');
INSERT INTO `problem` VALUES (4, '盛最多水的容器', '给定 n 个非负整数 a1，a2，...，an，每个数代表坐标中的一个点 (i, ai) 。在坐标内画 n 条垂直线，垂直线 i 的两个端点分别为 (i, ai) 和 (i, 0)。找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。', 1, '未尝试');
INSERT INTO `problem` VALUES (5, '三数之和', '给定一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？找出所有满足条件且不重复的三元组。', 1, '未尝试');
INSERT INTO `problem` VALUES (6, '电话号码的字母组合', '给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。', 1, '未尝试');
INSERT INTO `problem` VALUES (7, '删除链表的倒数第N个节点', '给定一个链表，删除链表的倒数第 n 个节点，并且返回链表的头结点。', 0, '未尝试');
INSERT INTO `problem` VALUES (8, '有效的括号', '给定一个只包括 \'(\', \')\', \'{\', \'}\', \'[\', \']\' 的字符串，判断字符串是否有效。有效字符串需满足：左括号必须用相同类型的右括号闭合。左括号必须以正确的顺序闭合。注意空字符串可被认为是有效字符串。', 0, '未尝试');
INSERT INTO `problem` VALUES (9, '合并两个有序链表', '将两个升序链表合并为一个新的升序链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。', 0, '未尝试');
INSERT INTO `problem` VALUES (10, '括号生成', '给出 n 代表生成括号的对数，请你写出一个函数，使其能够生成所有可能的并且有效的括号组合。', 0, '未尝试');
INSERT INTO `problem` VALUES (11, '合并K个排序链表', '合并 k 个排序链表，返回合并后的排序链表。请分析和描述算法的复杂度。', 0, '未尝试');
INSERT INTO `problem` VALUES (12, '下一个排列', '实现获取下一个排列的函数，算法需要将给定数字序列重新排列成字典序中下一个更大的排列。如果不存在下一个更大的排列，则将数字重新排列成最小的排列（即升序排列）。必须原地修改，只允许使用额外常数空间。', 0, '未尝试');
INSERT INTO `problem` VALUES (13, '最长有效括号', '给定一个只包含 \'(\' 和 \')\' 的字符串，找出最长的包含有效括号的子串的长度。', 0, '未尝试');
INSERT INTO `problem` VALUES (14, '搜索旋转排序数组', '假设按照升序排序的数组在预先未知的某个点上进行了旋转。( 例如，数组 [0,1,2,4,5,6,7] 可能变为 [4,5,6,7,0,1,2] )。搜索一个给定的目标值，如果数组中存在这个目标值，则返回它的索引，否则返回 -1 。你可以假设数组中不存在重复的元素。你的算法时间复杂度必须是 O(log n) 级别。', 0, '未尝试');
INSERT INTO `problem` VALUES (15, '组合总和', '给定一个无重复元素的数组 candidates 和一个目标数 target ，找出 candidates 中所有可以使数字和为 target 的组合。candidates 中的数字可以无限制重复被选取。说明：所有数字（包括 target）都是正整数。解集不能包含重复的组合。', 0, '未尝试');
INSERT INTO `problem` VALUES (16, '旋转图像', '给定一个 n × n 的二维矩阵表示一个图像。将图像顺时针旋转 90 度。说明：你必须在原地旋转图像，这意味着你需要直接修改输入的二维矩阵。请不要使用另一个二维矩阵来旋转图像。', 0, '未尝试');
INSERT INTO `problem` VALUES (17, '字母异位词分组', '给定一个字符串数组，将字母异位词组合在一起。字母异位词指字母相同，但排列不同的字符串。', 0, '未尝试');
INSERT INTO `problem` VALUES (18, '最大子序和', '给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。', 0, '未尝试');
INSERT INTO `problem` VALUES (19, '跳跃游戏', '给定一个非负整数数组，你最初位于数组的第一个位置。数组中的每个元素代表你在该位置可以跳跃的最大长度。判断你是否能够到达最后一个位置。', 0, '未尝试');
INSERT INTO `problem` VALUES (20, '合并区间', '给出一个区间的集合，请合并所有重叠的区间。', 0, '未尝试');
INSERT INTO `problem` VALUES (21, '不同路径', '一个机器人位于一个 m x n 网格的左上角 （起始点在下图中标记为“Start” ）。机器人每次只能向下或者向右移动一步。机器人试图达到网格的右下角（在下图中标记为“Finish”）。问总共有多少条不同的路径？', 0, '未尝试');
INSERT INTO `problem` VALUES (22, '最小路径和', '给定一个包含非负整数的 m x n 网格，请找出一条从左上角到右下角的路径，使得路径上的数字总和为最小。说明：每次只能向下或者向右移动一步。', 0, '未尝试');
INSERT INTO `problem` VALUES (23, '合并K个升序链表', '给你一个链表数组，每个链表都已经按升序排列。请你将所有链表合并到一个升序链表中，返回合并后的链表。', 1, '未尝试');
INSERT INTO `problem` VALUES (24, '下一个排列', '实现获取下一个排列的函数，算法需要将给定数字序列重新排列成字典序中下一个更大的排列。如果不存在下一个更大的排列，则将数字重新排列成最小的排列（即升序排列）。必须原地修改，只允许使用额外常数空间。', 0, '未尝试');
INSERT INTO `problem` VALUES (25, '搜索旋转排序数组', '整数数组 nums 按升序排列，数组中的值 互不相同 。在传递给函数之前，nums 在预先未知的某个下标 k（0 <= k < nums.length）上进行了旋转，使数组变为 [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]（下标 从 0 开始 计数）。例如， [0,1,2,4,5,6,7] 在下标 3 处经旋转后可能变为 [4,5,6,7,0,1,2]。给你旋转后的数组 nums 和一个整数 target，如果 nums 中存在这个目标值 target，则返回它的下标，否则返回 -1。', 0, '未尝试');
INSERT INTO `problem` VALUES (26, '删除有序数组中的重复项', '给你一个有序数组 nums ，请你 原地 删除重复出现的元素，使每个元素 只出现一次 ，返回删除后数组的新长度。不要使用额外的数组空间，你必须在 原地 修改输入数组 并在使用 O(1) 额外空间的条件下完成。', 0, '未尝试');
INSERT INTO `problem` VALUES (27, '买卖股票的最佳时机', '给定一个数组 prices ，它的第 i 个元素 prices[i] 表示一支给定股票第 i 天的价格。你只能选择 某一天 买入这只股票，并选择在 未来的某一个不同的日子 卖出该股票。设计一个算法来计算你所能获取的最大利润。返回你可以从这笔交易中获取的最大利润。如果你不能获取任何利润，返回 0 。', 0, '未尝试');
INSERT INTO `problem` VALUES (28, '实现 strStr()', '实现 strStr() 函数。给你两个字符串 haystack 和 needle ，请你在 haystack 字符串中找出 needle 字符串出现的第一个位置（下标从 0 开始）。如果不存在，则返回  -1 。当 needle 是空字符串时，我们应当返回什么值呢？这是一个在面试中很好的问题。对于本题而言，当 needle 是空字符串时我们应当返回 0 。这与 C 语言的 strstr() 以及 Java 的 indexOf() 定义相符。', 0, '未尝试');
INSERT INTO `problem` VALUES (29, '两数相除', '给定两个整数，被除数 dividend 和除数 divisor。将两数相除，要求不使用乘法、除法和 mod 运算符。返回被除数 dividend 除以除数 divisor 得到的商。整数除法的结果应当截去（truncate）其小数部分，例如：truncate(8.345) = 8 以及 truncate(-2.7335) = -2', 0, '未尝试');
INSERT INTO `problem` VALUES (30, '串联所有单词的子串', '给定一个字符串 s 和一些 长度相同 的单词 words 。找出 s 中恰好可以由 words 中所有单词串联形成的子串的起始位置。注意子串要与 words 中的单词完全匹配，中间不能有其他字符 ，但不需要考虑 words 中单词串联的顺序。', 0, '未尝试');
INSERT INTO `problem` VALUES (31, '下一个排列', '实现获取下一个排列的函数，算法需要将给定数字序列重新排列成字典序中下一个更大的排列。如果不存在下一个更大的排列，则将数字重新排列成最小的排列（即升序排列）。必须原地修改，只允许使用额外常数空间。', 0, '未尝试');
INSERT INTO `problem` VALUES (32, 'A+B Problem', '输入两个整数 a 和 b，输出它们的和。', 0, '未尝试');
INSERT INTO `problem` VALUES (33, '过河卒', '在一个 m 行 n 列的棋盘上，从左上角走到右下角的路径数。卒只能向右或向下走，且不能经过某个特殊点（如马的位置及其攻击点）。求所有合法路径数。', 0, '未尝试');
INSERT INTO `problem` VALUES (34, '铺地毯', '在一个二维平面上铺设 n 块矩形地毯，地毯按顺序编号。每块地毯可用左下角坐标和宽、高表示。最后询问某个点被哪块地毯覆盖，若多块覆盖取编号最大的那块，若没有则输出 -1。', 0, '未尝试');
INSERT INTO `problem` VALUES (35, '陶陶摘苹果', '10 个苹果分别挂在不同高度，陶陶能用手触碰到高度为 H 以下的苹果。给出每个苹果的高度，问他最多能摘到几个苹果。', 0, '未尝试');
INSERT INTO `problem` VALUES (36, '能量项链', '将一个首尾相连的能量珠子项链打断成一条直线，打断后可以进行合并，合并两个珠子能获得的能量为它们能量值的乘积。求最大能量值。', 0, '未尝试');
INSERT INTO `problem` VALUES (37, '数字三角形', '给定一个数字三角形，从顶至底每次只能向左下或右下走一格，求路径数字之和最大值。', 0, '未尝试');
INSERT INTO `problem` VALUES (38, '纪念品分组', '有 n 个纪念品，每个纪念品的重量不超过 100，每组不能超过重量上限 W，问最少可以分成多少组。', 0, '未尝试');
INSERT INTO `problem` VALUES (39, '走楼梯', '有 N 级楼梯，每次可走 1 级或 2 级，求有多少种不同走法。', 0, '未尝试');
INSERT INTO `problem` VALUES (40, '高精度加法', '输入两个大整数，输出它们的和。', 0, '未尝试');
INSERT INTO `problem` VALUES (41, '数字三角形', '给定一个数字三角形，从顶到底移动，求路径和最大。', 0, '未尝试');
INSERT INTO `problem` VALUES (42, '约数个数', '给定一个正整数 n，求其约数的个数。', 0, '未尝试');
INSERT INTO `problem` VALUES (43, '最大子段和', '给定一个整数序列，求最大子段和。', 0, '未尝试');
INSERT INTO `problem` VALUES (44, '数列的和', '给定一个长度为 n 的整数序列，求该序列的所有元素之和。', 0, '未尝试');
INSERT INTO `problem` VALUES (45, '素数判定', '判断一个数是否为素数。', 0, '未尝试');
INSERT INTO `problem` VALUES (46, '最大公约数', '计算两个正整数的最大公约数。', 0, '未尝试');
INSERT INTO `problem` VALUES (47, '阶乘', '计算 n 的阶乘。', 0, '未尝试');
INSERT INTO `problem` VALUES (48, '斐波那契数列', '计算斐波那契数列的第 n 项。', 0, '未尝试');
INSERT INTO `problem` VALUES (49, '水仙花数', '判断一个三位数是否为水仙花数，即各位数字的立方和等于该数本身。', 0, '未尝试');
INSERT INTO `problem` VALUES (50, '字符统计', '统计字符串中各字符出现次数。', 0, '未尝试');

SET FOREIGN_KEY_CHECKS = 1;
