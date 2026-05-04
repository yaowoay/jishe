package com.aiinterview.model.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ExamResultResponse {
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer score;
    private List<QuestionJudgment> questionJudgments;
    
    @Data
    public static class QuestionJudgment {
        private String questionId;
        private String userAnswer;
        private String correctAnswer;
        private Boolean correct;
    }
}
