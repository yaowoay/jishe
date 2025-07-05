package com.coldwind.easyoj.service.impl;

import com.coldwind.easyoj.mapper.ExamQuestionDetailMapper;
import com.coldwind.easyoj.model.entity.ExamQuestionDetail;
import com.coldwind.easyoj.service.ExamQuestionDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamQuestionDetailServiceImpl implements ExamQuestionDetailService {
    private final ExamQuestionDetailMapper mapper;

    @Override
    public void saveQuestions(String taskId, List<Map<String, Object>> questions) {
        for (Map<String, Object> q : questions) {
            ExamQuestionDetail detail = new ExamQuestionDetail();
            detail.setTaskId(taskId);
            detail.setQuestionId((String) q.get("questionId"));
            detail.setType((String) q.get("type"));
            detail.setContent((String) (q.get("questionContent") != null ? q.get("questionContent") : q.get("statement")));
            detail.setOptionA((String) q.get("A"));
            detail.setOptionB((String) q.get("B"));
            detail.setOptionC((String) q.get("C"));
            detail.setOptionD((String) q.get("D"));
            detail.setAnswer(q.get("answer") != null ? q.get("answer").toString() : null);
            detail.setExplanation(q.get("trapDetection") != null ? q.get("trapDetection").toString() : (q.get("explanation") != null ? q.get("explanation").toString() : null));
            mapper.insert(detail);
        }
    }

    @Override
    public List<ExamQuestionDetail> getQuestionsByTaskId(String taskId) {
        return mapper.getQuestionsByTaskId(taskId);
    }
}