package com.coldwind.easyoj.model.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class JudgmentResultDTO {
    private String taskId;
    private int totalQuestions;
    private int correctCount;
    private double score; // 得分，百分制
    private List<QuestionJudgment> questionJudgments;
    private String answerFilePath;

    public void setAnswerFilePath(String answerFilePath) {
        this.answerFilePath = answerFilePath;
    }

    // Optionally add getter if needed
    public String getAnswerFilePath() {
        return answerFilePath;
    }

    @Data
    public static class QuestionJudgment {
        private String questionId;
        private String questionContent;
        private String userAnswer;
        private String correctAnswer;
        private boolean isCorrect;
        private String explanation; // 解释或陷阱提示
    }
}