package com.aiinterview.model.dto.interview;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试考试DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewExamDTO {
    
    private Long interviewResultId;
    private Integer applicationId;
    private String jobPosition;
    private String candidateName;
    private Integer totalQuestions;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    private Integer timeLimit; // 面试时间限制（分钟）
    
    private List<InterviewQuestionDTO> questions;
    
    /**
     * 面试题目DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterviewQuestionDTO {
        private Integer questionNumber;
        private String question;
        private String category; // 题目分类：技术、行为、情景等
        private String difficulty; // 难度：easy, medium, hard
        private Integer timeLimit; // 单题时间限制（分钟）
        private String tips; // 答题提示
    }
}
