package com.aiinterview;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

public class ResumeCleanTest {
    public static void main(String[] args) {
        // 模拟 AI 返回的混搭内容（从你截图复制）
        String mockAIResponse = "# 个人简历\n" +
                "**PERSONAL**\n" +
                "## 强强强强\n" +
                "... (中间截取) ...\n" +
                "## 技能证书\n" +
                "证书：项目管理师 (PMP)\n" +
                "```json\n" +
                "{\"name\": \"前端工程师\"}\n" +
                "```\n" +
                "- **job title**: \"json\"\n" +
                "\"workExperience\": []";

        // 测试清洗
        String cleaned = cleanResumeContent(mockAIResponse);
        System.out.println("=== 原始长度: " + mockAIResponse.length());
        System.out.println("=== 清洗后长度: " + cleaned.length());
        System.out.println("=== 清洗结果:\n" + cleaned);
    }

    private static String cleanResumeContent(String rawContent) {
        if (rawContent == null || rawContent.isEmpty()) {
            return "";
        }

        // 查找 "```json" 标记
        int jsonMarker = rawContent.indexOf("```json");
        if (jsonMarker > 0) {
            return rawContent.substring(0, jsonMarker).trim();
        }

        // 查找 "- **job title**:" 特征
        int jobTitleIndex = rawContent.indexOf("- **job title**:");
        if (jobTitleIndex > 0) {
            return rawContent.substring(0, jobTitleIndex).trim();
        }

        return rawContent;
    }
}