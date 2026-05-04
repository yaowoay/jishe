package com.aiinterview.service.exam;

import com.aiinterview.model.entity.exam.ExamUserAnswer;
import java.util.List;

public interface ExamUserAnswerService {
    void saveAnswer(String taskId, String questionId, String userAnswer);
    
    List<ExamUserAnswer> getAnswersByTaskId(String taskId);
    
    ExamUserAnswer getAnswer(String taskId, String questionId);
}
