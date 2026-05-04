package com.aiinterview.model.dto.exam;

import lombok.Data;

@Data
public class ExamSubmitRequest {
    private String taskId;
    private java.util.List<AnswerItem> answers;
    
    @Data
    public static class AnswerItem {
        private String questionId;
        private String userAnswer;
    }
}
