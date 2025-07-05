package com.coldwind.easyoj.service;

import com.coldwind.easyoj.model.dto.response.JudgmentResultDTO;

import java.util.List;

public interface ExamJudgmentDetailService {
    void saveJudgmentDetails(String taskId, List<JudgmentResultDTO.QuestionJudgment> judgments);
}