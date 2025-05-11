package org.iflyproject.springdemos.service.impl;

import org.springframework.stereotype.Service;
import org.iflyproject.springdemos.service.InterviewFlowService;
import org.iflyproject.springdemos.model.dto.*;

@Service
public class InterviewFlowServiceImpl implements InterviewFlowService {

    @Override
    public boolean validateAllStepsCompleted(InterviewStartRequest request) {
        return request.getResumeId() != null &&
                request.getInterviewerConfigId() != null &&
                request.getQuestionConfigId() != null &&
                request.getInterviewModeId() != null;
    }

    @Override
    public InterviewSession initializeInterviewSession(InterviewStartRequest request) {
        // 实现面试会话初始化逻辑
        InterviewSession session = new InterviewSession();
        session.setSessionId("session-" + System.currentTimeMillis());
        session.setStatus("READY");
        return session;
    }

    @Override
    public InterviewProgress getProgress(String sessionId) {
        // 实现获取面试进度逻辑
        InterviewProgress progress = new InterviewProgress();
        progress.setCurrentStep(3); // 假设已完成所有步骤
        progress.setReadyToStart(true);
        return progress;
    }
}