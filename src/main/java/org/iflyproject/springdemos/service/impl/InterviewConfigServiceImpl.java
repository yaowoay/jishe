package org.iflyproject.springdemos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.iflyproject.springdemos.mapper.InterviewerConfigDao;
import org.iflyproject.springdemos.mapper.QuestionConfigDao;
import org.iflyproject.springdemos.mapper.InterviewModeDao;
import org.iflyproject.springdemos.domain.InterviewerConfig;
import org.iflyproject.springdemos.domain.QuestionConfig;
import org.iflyproject.springdemos.domain.InterviewMode;
import org.iflyproject.springdemos.service.InterviewConfigService;

@Service
public class InterviewConfigServiceImpl implements InterviewConfigService {

    @Autowired
    private InterviewerConfigDao interviewerConfigDao;

    @Autowired
    private QuestionConfigDao questionConfigDao;

    @Autowired
    private InterviewModeDao interviewModeDao;

    @Override
    public InterviewerConfig saveInterviewerConfig(InterviewerConfig config) {
        return interviewerConfigDao.save(config);
    }

    @Override
    public QuestionConfig saveQuestionConfig(QuestionConfig config) {
        return questionConfigDao.save(config);
    }

    @Override
    public InterviewMode saveInterviewMode(InterviewMode mode) {
        return interviewModeDao.save(mode);
    }
}