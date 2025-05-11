package org.iflyproject.springdemos.service;

import org.iflyproject.springdemos.model.dto.*;

public interface InterviewFlowService {
    boolean validateAllStepsCompleted(InterviewStartRequest request);
    InterviewSession initializeInterviewSession(InterviewStartRequest request);
    InterviewProgress getProgress(String sessionId);
}