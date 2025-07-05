package com.coldwind.easyoj.service;


import com.coldwind.easyoj.model.dto.request.UserAnswersRequest;

import java.util.List;

public interface ExamUserAnswerService {
    void saveUserAnswers(String taskId, List<UserAnswersRequest.UserAnswer> answers);
}