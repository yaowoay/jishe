package com.aiinterview.service;

import com.aiinterview.model.dto.interview.InterviewSessionCreateRequest ;
import com.aiinterview.model.entity.interview.InterviewSession ;
import com.aiinterview.repository.interview.InterviewSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class InterviewService {
    
    @Autowired
    private InterviewSessionRepository interviewSessionRepository;
    
    public InterviewSession createInterviewSession(Long userId, InterviewSessionCreateRequest request) {
        log.info("开始创建面试会话，用户ID: {}, 职位: {}", userId, request.getJobPosition());
        
        // 创建面试会话
        InterviewSession session = new InterviewSession();
        session.setSessionId("session_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8));
        session.setUserId(userId);
        session.setResumePath(request.getResumePath());
        session.setJobPosition(request.getJobPosition());
        session.setStyle(request.getStyle());
        session.setStatus("ACTIVE");
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        
        interviewSessionRepository.insert(session);
        log.info("面试会话创建成功，sessionId: {}", session.getSessionId());
        
        return session;
    }
    
    public InterviewSession getInterviewSession(String sessionId) {
        return interviewSessionRepository.findBySessionId(sessionId);
    }
    
    public void updateSessionStatus(String sessionId, String status) {
        InterviewSession session = interviewSessionRepository.findBySessionId(sessionId);
        if (session != null) {
            session.setStatus(status);
            session.setUpdatedAt(LocalDateTime.now());
            interviewSessionRepository.updateById(session);
        }
    }
    
    public void generateInterviewQuestions(String sessionId) {
        log.info("开始生成面试题目，sessionId: {}", sessionId);
        InterviewSession session = getInterviewSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("面试会话不存在");
        }
        // 题目生成逻辑将在后续实现
        log.info("面试题目生成完成，sessionId: {}", sessionId);
    }
}
