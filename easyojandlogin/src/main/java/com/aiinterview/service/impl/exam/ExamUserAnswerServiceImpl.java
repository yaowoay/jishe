package com.aiinterview.service.impl.exam;

import com.aiinterview.model.entity.exam.ExamUserAnswer;
import com.aiinterview.repository.exam.ExamUserAnswerRepository;
import com.aiinterview.service.exam.ExamUserAnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamUserAnswerServiceImpl implements ExamUserAnswerService {
    
    private final ExamUserAnswerRepository examUserAnswerRepository;

    @Override
    public void saveAnswer(String taskId, String questionId, String userAnswer) {
        ExamUserAnswer answer = new ExamUserAnswer();
        answer.setTaskId(taskId);
        answer.setQuestionId(questionId);
        answer.setUserAnswer(userAnswer);
        examUserAnswerRepository.insert(answer);
        log.info("保存用户答案: taskId={}, questionId={}, userAnswer={}", taskId, questionId, userAnswer);
    }

    @Override
    public List<ExamUserAnswer> getAnswersByTaskId(String taskId) {
        return examUserAnswerRepository.findByTaskId(taskId);
    }

    @Override
    public ExamUserAnswer getAnswer(String taskId, String questionId) {
        return examUserAnswerRepository.findByTaskIdAndQuestionId(taskId, questionId);
    }
}
