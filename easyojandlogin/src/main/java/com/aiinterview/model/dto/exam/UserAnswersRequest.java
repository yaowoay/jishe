package com.aiinterview.model.dto.exam;

import lombok.Data;
import java.util.List;

@Data
public class UserAnswersRequest {
    private String taskId;
    private List<UserAnswer> answers;

    @Data
    public static class UserAnswer {
        private String questionId;
        private String userAnswer;
    }
}
