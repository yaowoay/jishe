package com.coldwind.easyoj.service;

import com.coldwind.easyoj.model.entity.ExamQuestionDetail;
import java.util.List;
import java.util.Map;

public interface ExamQuestionDetailService {
    void saveQuestions(String taskId, List<Map<String, Object>> questions);
    
    /**
     * 根据taskId获取题目详情
     */
    List<ExamQuestionDetail> getQuestionsByTaskId(String taskId);
}