package com.coldwind.easyoj.service;

import com.coldwind.easyoj.model.dto.*;
//定义了面试流程相关的服务接口，包括验证步骤、初始化面试会话和获取面试进度
public interface InterviewFlowService {
    boolean validateAllStepsCompleted(InterviewStartRequest request);
    InterviewSession initializeInterviewSession(InterviewStartRequest request);
    InterviewProgress getProgress(String sessionId);
}