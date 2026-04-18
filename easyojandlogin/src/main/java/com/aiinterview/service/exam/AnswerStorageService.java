package com.aiinterview.service.exam;

public interface AnswerStorageService {
    void storeAnswer(String taskId, String questionId, String userAnswer);
    
    String retrieveAnswer(String taskId, String questionId);
}
