package com.aiinterview.service.impl.exam;

import com.aiinterview.model.dto.exam.JudgmentResultDTO;
import com.aiinterview.model.dto.exam.UserAnswersRequest;
import com.aiinterview.service.ai.dify.DifyExamService;
import com.aiinterview.service.exam.ExamJudgmentService;
import com.aiinterview.service.exam.ExamQuestionDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamJudgmentServiceImpl implements ExamJudgmentService {

    private final DifyExamService difyExamService;
    private final ExamQuestionDetailService examQuestionDetailService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<JudgmentResultDTO> judgeAnswers(UserAnswersRequest userAnswers) {
        // 先尝试从 Dify 缓存获取题目（支持 Dify API 生成的数据）
        return difyExamService.getGeneratedQuestions(userAnswers.getTaskId())
                .onErrorResume(e -> {
                    // 如果缓存不存在，从数据库获取题目
                    log.info("Dify 缓存不存在，从数据库获取题目: {}", e.getMessage());
                    return Mono.fromCallable(() -> {
                        List<com.aiinterview.model.entity.exam.ExamQuestionDetail> dbQuestions =
                                examQuestionDetailService.getQuestionsByTaskId(userAnswers.getTaskId());

                        if (dbQuestions == null || dbQuestions.isEmpty()) {
                            throw new RuntimeException("题目不存在或已过期");
                        }

                        // 将数据库实体转换为缓存格式
                        List<Map<String, Object>> questionsList = dbQuestions.stream()
                                .map(q -> {
                                    Map<String, Object> qMap = new java.util.HashMap<>();
                                    qMap.put("questionId", q.getQuestionId());
                                    qMap.put("type", q.getType());
                                    qMap.put("questionContent", q.getContent());
                                    qMap.put("A", q.getOptionA());
                                    qMap.put("B", q.getOptionB());
                                    qMap.put("C", q.getOptionC());
                                    qMap.put("D", q.getOptionD());
                                    qMap.put("answer", q.getAnswer());
                                    qMap.put("trapDetection", q.getExplanation());
                                    return qMap;
                                })
                                .collect(java.util.stream.Collectors.toList());

                        Map<String, Object> mockQuestions = new java.util.HashMap<>();
                        mockQuestions.put("questions", questionsList);

                        Map<String, Object> outputs = new java.util.HashMap<>();
                        outputs.put("questions", mockQuestions);

                        Map<String, Object> data = new java.util.HashMap<>();
                        data.put("outputs", outputs);

                        Map<String, Object> result = new java.util.HashMap<>();
                        result.put("task_id", userAnswers.getTaskId());
                        result.put("data", data);

                        return result;
                    });
                })
                .switchIfEmpty(Mono.error(new RuntimeException("题目不存在或已过期")))
                .map(generatedQuestions -> {
                    log.info("开始判题，taskId: {}", userAnswers.getTaskId());

                    Map<String, Object> data = (Map<String, Object>) generatedQuestions.get("data");
                    Map<String, Object> outputs = (Map<String, Object>) data.get("outputs");
                    Map<String, Object> questionsContainer = (Map<String, Object>) outputs.get("questions");

                    // 支持两种格式：直接的 List 或包含 "questions" 字段的 Map
                    List<Map<String, Object>> questions;
                    if (questionsContainer instanceof java.util.List) {
                        questions = (List<Map<String, Object>>) questionsContainer;
                    } else {
                        questions = (List<Map<String, Object>>) questionsContainer.get("questions");
                    }

                    JudgmentResultDTO result = new JudgmentResultDTO();
                    result.setTaskId(userAnswers.getTaskId());
                    result.setTotalQuestions(questions.size());

                    List<JudgmentResultDTO.QuestionJudgment> judgments = questions.stream()
                            .map(q -> judgeQuestion(q, userAnswers))
                            .collect(java.util.stream.Collectors.toList());

                    int correctCount = (int) judgments.stream()
                            .filter(JudgmentResultDTO.QuestionJudgment::isCorrect)
                            .count();

                    result.setCorrectCount(correctCount);
                    result.setScore((double) (correctCount * 100) / questions.size());
                    result.setQuestionJudgments(judgments);

                    log.info("判题完成，正确数: {}/{}, 得分: {}", correctCount, questions.size(), result.getScore());

                    return result;
                });
    }

    private JudgmentResultDTO.QuestionJudgment judgeQuestion(
            Map<String, Object> question,
            UserAnswersRequest userAnswers) {

        String questionId = (String) question.get("questionId");
        String correctAnswer = (String) question.get("answer");

        String userAnswer = userAnswers.getAnswers().stream()
                .filter(a -> a.getQuestionId().equals(questionId))
                .map(UserAnswersRequest.UserAnswer::getUserAnswer)
                .findFirst()
                .orElse("");

        JudgmentResultDTO.QuestionJudgment judgment = new JudgmentResultDTO.QuestionJudgment();
        judgment.setQuestionId(questionId);
        judgment.setQuestionContent((String) question.get("questionContent"));
        judgment.setUserAnswer(userAnswer);
        judgment.setCorrectAnswer(correctAnswer);
        judgment.setCorrect(userAnswer.equals(correctAnswer));
        judgment.setExplanation((String) question.get("trapDetection"));

        return judgment;
    }
}
