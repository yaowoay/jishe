package com.coldwind.easyoj.service.impl;

import com.coldwind.easyoj.mapper.ExamJudgmentDetailMapper;
import com.coldwind.easyoj.model.dto.response.JudgmentResultDTO;
import com.coldwind.easyoj.model.entity.ExamJudgmentDetail;
import com.coldwind.easyoj.service.ExamJudgmentDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamJudgmentDetailServiceImpl implements ExamJudgmentDetailService {
    private final ExamJudgmentDetailMapper mapper;

    @Override
    public void saveJudgmentDetails(String taskId, List<JudgmentResultDTO.QuestionJudgment> judgments) {
        for (JudgmentResultDTO.QuestionJudgment j : judgments) {
            ExamJudgmentDetail detail = new ExamJudgmentDetail();
            detail.setTaskId(taskId);
            detail.setQuestionId(j.getQuestionId());
            detail.setUserAnswer(j.getUserAnswer());
            detail.setCorrectAnswer(j.getCorrectAnswer());
            detail.setIsCorrect(j.isCorrect());
            detail.setExplanation(j.getExplanation());
            mapper.insert(detail);
        }
    }
}