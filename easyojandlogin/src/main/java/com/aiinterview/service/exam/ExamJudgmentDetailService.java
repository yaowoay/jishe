package com.aiinterview.service.exam;

public interface ExamJudgmentDetailService {
    void saveJudgmentDetail(String taskId, String questionId, String userAnswer, 
                           String correctAnswer, boolean isCorrect, String explanation);
}
