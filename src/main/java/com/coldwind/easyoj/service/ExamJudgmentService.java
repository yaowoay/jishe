package com.coldwind.easyoj.service;


import com.coldwind.easyoj.model.dto.request.UserAnswersRequest;
import com.coldwind.easyoj.model.dto.response.JudgmentResultDTO;
import reactor.core.publisher.Mono;


public interface ExamJudgmentService {
    /**
     * 判题逻辑主方法
     * @param userAnswers 用户提交的答案
     * @return 判题结果
     */
    Mono<JudgmentResultDTO> judgeAnswers(UserAnswersRequest userAnswers);
}