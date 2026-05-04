package com.aiinterview.model.dto.exam;

import lombok.Data;
import java.util.List;

@Data
public class JudgmentResultDTO {
    private String taskId;
    private int totalQuestions;
    private int correctCount;
    private double score;
    private List<QuestionJudgment> questionJudgments;
    private String answerFilePath;

    @Data
    public static class QuestionJudgment {
        private String questionId;
        private String questionContent;
        private String userAnswer;
        private String correctAnswer;
        private boolean correct;
        private String explanation;

        public boolean isCorrect() {
            return correct;
        }

        public void setCorrect(boolean correct) {
            this.correct = correct;
        }
    }
}
