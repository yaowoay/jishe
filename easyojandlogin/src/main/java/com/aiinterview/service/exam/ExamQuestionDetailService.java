package com.aiinterview.service.exam;

import com.aiinterview.model.entity.exam.ExamQuestionDetail ;

import java.util.List;
import java.util.Map;

public interface ExamQuestionDetailService {
    void saveQuestions(String taskId, List<Map<String, Object>> questions);
    
    List<ExamQuestionDetail> getQuestionsByTaskId(String taskId);
    
    ExamQuestionDetail getQuestionById(String questionId);
}
