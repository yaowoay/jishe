-- OJ判题系统数据库初始化脚本

-- 创建 problem 表
CREATE TABLE IF NOT EXISTS problem (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '题目标题',
    description TEXT NOT NULL COMMENT '题目描述',
    is_favorite BOOLEAN DEFAULT FALSE COMMENT '是否收藏',
    status ENUM('未尝试', '尝试中', '通过') DEFAULT '未尝试' COMMENT '题目状态'
);

-- 创建 example 表
CREATE TABLE IF NOT EXISTS example (
    id INT AUTO_INCREMENT PRIMARY KEY,
    problem_id INT NOT NULL COMMENT '题目ID',
    input TEXT NOT NULL COMMENT '输入示例',
    output TEXT NOT NULL COMMENT '输出示例',
    FOREIGN KEY (problem_id) REFERENCES problem(id) ON DELETE CASCADE
);

-- 创建 language 表
CREATE TABLE IF NOT EXISTS language (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL COMMENT '语言名称'
);

-- 创建 solution 表
CREATE TABLE IF NOT EXISTS solution (
    id INT AUTO_INCREMENT PRIMARY KEY,
    problem_id INT NOT NULL COMMENT '题目ID',
    language_id INT NOT NULL COMMENT '语言ID',
    code TEXT NOT NULL COMMENT '代码内容',
    FOREIGN KEY (problem_id) REFERENCES problem(id) ON DELETE CASCADE,
    FOREIGN KEY (language_id) REFERENCES language(id)
);

-- 插入编程语言支持
INSERT INTO language (id, name) VALUES 
(1, 'Python'),
(2, 'Java'),
(3, 'C')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 插入题目
INSERT INTO problem (id, title, description, is_favorite, status) VALUES
(1, '两数之和', '给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那两个整数，并返回他们的数组下标。', FALSE, '通过'),
(2, '无重复字符的最长子串', '给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。', TRUE, '未尝试')
ON DUPLICATE KEY UPDATE 
    title = VALUES(title),
    description = VALUES(description),
    is_favorite = VALUES(is_favorite),
    status = VALUES(status);

-- 插入示例
INSERT INTO example (problem_id, input, output) VALUES
(1, 'nums = [2,7,11,15], target = 9', '[0,1]'),
(1, 'nums = [3,2,4], target = 6', '[1,2]'),
(2, 's = "abcabcbb"', '3'),
(2, 's = "bbbbb"', '1')
ON DUPLICATE KEY UPDATE 
    input = VALUES(input),
    output = VALUES(output);

-- 插入题目代码
INSERT INTO solution (problem_id, language_id, code) VALUES
(1, 1, 'def two_sum(nums, target):\n    for i in range(len(nums)):\n        for j in range(i+1, len(nums)):\n            if nums[i] + nums[j] == target:\n                return [i, j]\n    return []\n\n# 测试代码\nif __name__ == "__main__":\n    nums = [2, 7, 11, 15]\n    target = 9\n    result = two_sum(nums, target)\n    print(result)'),
(1, 2, 'public class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        for (int i = 0; i < nums.length; i++) {\n            for (int j = i + 1; j < nums.length; j++) {\n                if (nums[i] + nums[j] == target) {\n                    return new int[]{i, j};\n                }\n            }\n        }\n        return null;\n    }\n}'),
(1, 3, '#include <stdio.h>\n#include <stdlib.h>\n\nint* twoSum(int* nums, int numsSize, int target, int* returnSize) {\n    for (int i = 0; i < numsSize; i++) {\n        for (int j = i + 1; j < numsSize; j++) {\n            if (nums[i] + nums[j] == target) {\n                int* result = malloc(2 * sizeof(int));\n                result[0] = i; result[1] = j;\n                *returnSize = 2;\n                return result;\n            }\n        }\n    }\n    return NULL;\n}'),
(2, 1, 'def lengthOfLongestSubstring(s):\n    start = maxLength = 0\n    usedChar = {}\n    for i in range(len(s)):\n        if s[i] in usedChar and start <= usedChar[s[i]]:\n            start = usedChar[s[i]] + 1\n        else:\n            maxLength = max(maxLength, i - start + 1)\n        usedChar[s[i]] = i\n    return maxLength\n\n# 测试代码\nif __name__ == "__main__":\n    s = "abcabcbb"\n    result = lengthOfLongestSubstring(s)\n    print(result)'),
(2, 2, 'public class Solution {\n    public int lengthOfLongestSubstring(String s) {\n        int maxLength = 0;\n        Map<Character, Integer> map = new HashMap<>();\n        for (int i = 0, j = 0; j < s.length(); j++) {\n            if (map.containsKey(s.charAt(j))) {\n                i = Math.max(map.get(s.charAt(j)) + 1, i);\n            }\n            map.put(s.charAt(j), j);\n            maxLength = Math.max(maxLength, j - i + 1);\n        }\n        return maxLength;\n    }\n}'),
(2, 3, '#include <string.h>\n\nint lengthOfLongestSubstring(char * s) {\n    int lastIndex[128] = {0}, maxLen = 0, start = 0;\n    for (int i = 0; s[i]; i++) {\n        start = lastIndex[s[i]] > start ? lastIndex[s[i]] : start;\n        int len = i - start + 1;\n        maxLen = maxLen > len ? maxLen : len;\n        lastIndex[s[i]] = i + 1;\n    }\n    return maxLen;\n}')
ON DUPLICATE KEY UPDATE 
    code = VALUES(code); 