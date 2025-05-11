package org.iflyproject.springdemos.service;

// 面试配置服务

import org.iflyproject.springdemos.domain.InterviewerConfig;
import org.iflyproject.springdemos.domain.QuestionConfig;
import org.iflyproject.springdemos.domain.InterviewMode;

public interface InterviewConfigService {
    InterviewerConfig saveInterviewerConfig(InterviewerConfig config);
    QuestionConfig saveQuestionConfig(QuestionConfig config);
    InterviewMode saveInterviewMode(InterviewMode mode);
}