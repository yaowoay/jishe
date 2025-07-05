package com.coldwind.easyoj.service.impl;


import com.coldwind.easyoj.mapper.ExamUserAnswerMapper;
import com.coldwind.easyoj.model.dto.request.UserAnswersRequest;
import com.coldwind.easyoj.model.entity.ExamUserAnswer;
import com.coldwind.easyoj.service.ExamUserAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamUserAnswerServiceImpl implements ExamUserAnswerService {
    private final ExamUserAnswerMapper mapper;

    @Override
    public void saveUserAnswers(String taskId, List<UserAnswersRequest.UserAnswer> answers) {
        for (UserAnswersRequest.UserAnswer a : answers) {
            ExamUserAnswer answer = new ExamUserAnswer();
            answer.setTaskId(taskId);
            answer.setQuestionId(a.getQuestionId());
            answer.setUserAnswer(a.getUserAnswer());
            mapper.insert(answer);
        }
    }
}