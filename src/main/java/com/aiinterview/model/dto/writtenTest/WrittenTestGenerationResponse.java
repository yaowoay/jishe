package com.aiinterview.model.dto.writtenTest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 笔试生成响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WrittenTestGenerationResponse {
    
    private Long id;
    private Integer applicationId;
    private Long resumeId;
    private String jobPosition;
    private String candidateField;
    private Integer totalQuestions;
    private Integer choiceQuestions;
    private Integer trueFalseQuestions;
    private Integer totalScore;
    private String generationStatus;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 解析后的题目列表
    private List<QuestionDTO> questions;
    
    /**
     * 题目DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionDTO {
        private String questionId;
        private String type; // choice 或 true_false
        private String questionContent; // 选择题的问题内容
        private String statement; // 判断题的陈述
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String answer; // 选择题答案（A/B/C/D）或判断题答案（true/false）
        private String explanation; // 解释
    }
}
