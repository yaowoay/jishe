package com.coldwind.easyoj.model.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class UserAnswersRequest {
    private String taskId; // 任务ID，用于关联生成的题目
    private List<UserAnswer> answers;

    @Data
    public static class UserAnswer {
        private String questionId; // 题目ID
        private String userAnswer; // 用户答案，选择题为A/B/C/D，判断题为true/false
    }
}