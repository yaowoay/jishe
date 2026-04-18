package com.aiinterview.service;

import com.aiinterview.model.dto.exam.ExamGenerateRequest ;
import com.aiinterview.model.entity.exam.ExamTask ;
import com.aiinterview.repository.exam.ExamTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class ExamService {
    
    @Autowired
    private ExamTaskRepository examTaskRepository;
    
    public ExamTask generateExamQuestions(Long userId, ExamGenerateRequest request) {
        log.info("开始生成笔试题目，用户ID: {}, 职位: {}", userId, request.getJobPosition());
        
        // 验证请求参数
        validateExamSettings(request);
        
        // 创建笔试任务
        ExamTask task = new ExamTask();
        task.setTaskId("task_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8));
        task.setUserId(userId);
        task.setJobPosition(request.getJobPosition());
        task.setSkills(request.getSkills());
        task.setExperience(request.getExperience());
        task.setQuestionCount(request.getQuestionCount());
        task.setDifficultyLevel(request.getDifficultyLevel());
        task.setFocusArea(request.getFocusArea());
        task.setStatus("PENDING");
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        examTaskRepository.insert(task);
        log.info("笔试任务创建成功，taskId: {}", task.getTaskId());
        
        return task;
    }
    
    public ExamTask getExamStatus(String taskId) {
        return examTaskRepository.findByTaskId(taskId);
    }
    
    public void updateExamStatus(String taskId, String status) {
        ExamTask task = examTaskRepository.findByTaskId(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setUpdatedAt(LocalDateTime.now());
            examTaskRepository.updateById(task);
        }
    }
    
    private void validateExamSettings(ExamGenerateRequest request) {
        if (request.getJobPosition() == null || request.getJobPosition().trim().isEmpty()) {
            throw new IllegalArgumentException("职位名称不能为空");
        }
        if (request.getSkills() == null || request.getSkills().trim().isEmpty()) {
            throw new IllegalArgumentException("技能要求不能为空");
        }
        if (request.getExperience() == null || request.getExperience().trim().isEmpty()) {
            throw new IllegalArgumentException("工作经验不能为空");
        }
        if (request.getQuestionCount() == null || request.getQuestionCount() < 3 || request.getQuestionCount() > 20) {
            throw new IllegalArgumentException("题目数量必须在3-20之间");
        }
        if (request.getDifficultyLevel() == null || request.getDifficultyLevel().trim().isEmpty()) {
            throw new IllegalArgumentException("难度等级不能为空");
        }
    }
}
