package com.aiinterview.model.dto.writtenTest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 笔试考试DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WrittenTestExamDTO {
    
    private Long testResultId;
    private Integer applicationId;
    private String jobPosition;
    private Integer totalQuestions;
    private Integer totalScore;
    private String status; // not_started, in_progress, completed
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private Integer timeLimit; // 考试时长（分钟）
    
    // 题目列表（不包含答案，用于考试）
    private List<ExamQuestionDTO> questions;
    
    /**
     * 考试题目DTO（不包含正确答案）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExamQuestionDTO {
        private String questionId;
        private String type; // choice 或 true_false
        private String questionContent; // 选择题的问题内容
        private String statement; // 判断题的陈述
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        // 注意：不包含answer和explanation，防止泄露答案
    }
    
    /**
     * 用户答案DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserAnswerDTO {
        private String questionId;
        private String userAnswer; // 用户选择的答案
        private Long answerTime; // 答题时间（毫秒）
    }
    
    /**
     * 答题提交DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubmitAnswersDTO {
        private Integer applicationId;
        private Long testResultId;
        private List<UserAnswerDTO> answers;
        private Integer completionTime; // 总完成时间（秒）
    }
    
    /**
     * 答题结果DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TestResultDTO {
        private Long id;
        private Integer applicationId;
        private Long testResultId;
        private BigDecimal score;
        private Integer totalQuestions;
        private Integer correctAnswers;
        private Integer wrongAnswers;
        private Integer completionTime;
        private LocalDateTime startTime;
        private LocalDateTime submitTime;
        private String status;
        
        // 详细结果
        private List<QuestionResultDTO> questionResults;
        
        /**
         * 题目结果DTO
         */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class QuestionResultDTO {
            private String questionId;
            private String type;
            private String questionContent;
            private String statement;
            private String optionA;
            private String optionB;
            private String optionC;
            private String optionD;
            private String correctAnswer;
            private String userAnswer;
            private Boolean isCorrect;
            private String explanation;
        }
    }
}
