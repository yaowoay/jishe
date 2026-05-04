package com.aiinterview.service;

import com.aiinterview.model.entity.exam.ExamQuestion;
import com.aiinterview.repository.exam.ExamQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExamQuestionService {
    
    @Autowired
    private ExamQuestionRepository examQuestionRepository;
    
    public List<ExamQuestion> getQuestionsByTaskId(String taskId) {
        log.info("获取笔试题目，taskId: {}", taskId);
        return examQuestionRepository.findByTaskId(taskId);
    }
    
    public ExamQuestion getQuestionById(String questionId) {
        return examQuestionRepository.findByQuestionId(questionId);
    }
    
    public void saveQuestion(ExamQuestion question) {
        examQuestionRepository.insert(question);
    }
    
    public int getQuestionCount(String taskId) {
        return examQuestionRepository.countByTaskId(taskId);
    }
}
