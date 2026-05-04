package com.aiinterview.service.impl.exam;

import com.aiinterview.model.entity.exam.ExamJudgmentDetail;
import com.aiinterview.repository.exam.ExamJudgmentDetailRepository;
import com.aiinterview.service.exam.ExamJudgmentDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamJudgmentDetailServiceImpl implements ExamJudgmentDetailService {
    
    private final ExamJudgmentDetailRepository examJudgmentDetailRepository;

    @Override
    public void saveJudgmentDetail(String taskId, String questionId, String userAnswer, 
                                   String correctAnswer, boolean isCorrect, String explanation) {
        ExamJudgmentDetail detail = new ExamJudgmentDetail();
        detail.setTaskId(taskId);
        detail.setQuestionId(questionId);
        detail.setUserAnswer(userAnswer);
        detail.setCorrectAnswer(correctAnswer);
        detail.setIsCorrect(isCorrect ? 1 : 0);
        detail.setExplanation(explanation);
        
        examJudgmentDetailRepository.insert(detail);
        log.info("保存判题详情: taskId={}, questionId={}, isCorrect={}", taskId, questionId, isCorrect);
    }
}
