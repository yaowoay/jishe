package com.coldwind.easyoj.service;

// 面试配置服务

import com.coldwind.easyoj.domain.InterviewerConfig;
import com.coldwind.easyoj.domain.QuestionConfig;
import com.coldwind.easyoj.domain.InterviewMode;

public interface InterviewConfigService {
    InterviewerConfig saveInterviewerConfig(InterviewerConfig config);
    QuestionConfig saveQuestionConfig(QuestionConfig config);
    InterviewMode saveInterviewMode(InterviewMode mode);
}