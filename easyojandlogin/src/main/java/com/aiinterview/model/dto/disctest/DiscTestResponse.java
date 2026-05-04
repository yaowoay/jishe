package com.aiinterview.model.dto.disctest;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DISC测试响应DTO
 */
@Data
public class DiscTestResponse {
    
    /**
     * 测试会话ID
     */
    private String testSession;
    
    /**
     * 测试状态
     */
    private String testStatus;
    
    /**
     * 测试题目（用于开始测试）
     */
    private List<DiscQuestionGroup> questions;
    
    /**
     * 测试结果（用于完成测试）
     */
    private DiscResult result;
    
    /**
     * 题目组DTO
     */
    @Data
    public static class DiscQuestionGroup {
        /**
         * 题目组号
         */
        private Integer questionGroup;
        
        /**
         * 选项列表
         */
        private List<DiscOption> options;
    }
    
    /**
     * 选项DTO
     */
    @Data
    public static class DiscOption {
        /**
         * 选项字母
         */
        private String optionLetter;
        
        /**
         * 选项内容
         */
        private String optionText;
    }
    
    /**
     * DISC测试结果DTO
     */
    @Data
    public static class DiscResult {
        /**
         * 各类型得分
         */
        private Map<String, Integer> scores;
        
        /**
         * 主要类型
         */
        private String primaryType;
        
        /**
         * 次要类型
         */
        private String secondaryType;
        
        /**
         * 性格档案
         */
        private String personalityProfile;
        
        /**
         * 类型描述
         */
        private DiscTypeDescription typeDescription;
        
        /**
         * 详细分析
         */
        private String detailedAnalysis;
        
        /**
         * 优势特点
         */
        private String strengths;
        
        /**
         * 改进建议
         */
        private String weaknesses;
        
        /**
         * 职业建议
         */
        private String careerSuggestions;
        
        /**
         * 完成时间
         */
        private LocalDateTime completeTime;
    }
    
    /**
     * DISC类型描述DTO
     */
    @Data
    public static class DiscTypeDescription {
        /**
         * 类型名称
         */
        private String typeName;
        
        /**
         * 类型标题
         */
        private String typeTitle;
        
        /**
         * 类型描述
         */
        private String description;
        
        /**
         * 特征描述
         */
        private String characteristics;
        
        /**
         * 工作风格
         */
        private String workStyle;
        
        /**
         * 沟通风格
         */
        private String communicationStyle;
        
        /**
         * 管理建议
         */
        private String managementTips;
        
        /**
         * 适合职业领域
         */
        private String careerFields;
    }
}
