package com.aiinterview.service.exam;

import com.aiinterview.model.dto.exam.JudgmentResultDTO;
import com.aiinterview.model.dto.exam.UserAnswersRequest;
import reactor.core.publisher.Mono;

public interface ExamJudgmentService {
    Mono<JudgmentResultDTO> judgeAnswers(UserAnswersRequest userAnswers);
}
