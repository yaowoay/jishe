package com.coldwind.easyoj.service.impl;

import com.coldwind.easyoj.model.dto.request.UserAnswersRequest;
import com.coldwind.easyoj.model.dto.response.JudgmentResultDTO;
import com.coldwind.easyoj.service.DifyExamService;
import com.coldwind.easyoj.service.ExamJudgmentService;
import com.coldwind.easyoj.service.AnswerStorageService;
import com.coldwind.easyoj.service.ExamJudgmentDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamJudgmentServiceImpl implements ExamJudgmentService {

    private final DifyExamService difyExamService;
    private final AnswerStorageService answerStorageService;
    private final ObjectMapper objectMapper;
    private final ExamJudgmentDetailService examJudgmentDetailService;

    @Override
    public Mono<JudgmentResultDTO> judgeAnswers(UserAnswersRequest userAnswers) {
        // 1. 获取之前生成的题目
        return difyExamService.getGeneratedQuestions(userAnswers.getTaskId())
                .switchIfEmpty(Mono.error(new RuntimeException("题目不存在或已过期"))) //验证缓存中是否存在有效题目

                .map(generatedQuestions -> {
                    log.info("Generated Questions: {}", generatedQuestions); //打印日志

                    // 2. 执行判题逻辑
                    Map<String, String> correctAnswersMap = extractCorrectAnswers(generatedQuestions);
                    Map<String, String> questionContentsMap = extractQuestionContents(generatedQuestions);
                    Map<String, String> explanationsMap = extractExplanations(generatedQuestions);

                    // 3. 比对用户答案与正确答案
                    JudgmentResultDTO result = new JudgmentResultDTO();
                    result.setTaskId(userAnswers.getTaskId());
                    result.setTotalQuestions(correctAnswersMap.size());

                    result.setQuestionJudgments(userAnswers.getAnswers().stream()
                            .map(userAnswer -> {
                                JudgmentResultDTO.QuestionJudgment qj = new JudgmentResultDTO.QuestionJudgment();
                                qj.setQuestionId(userAnswer.getQuestionId());
                                qj.setQuestionContent(questionContentsMap.get(userAnswer.getQuestionId()));
                                qj.setUserAnswer(userAnswer.getUserAnswer());

                                String correctAnswer = correctAnswersMap.get(userAnswer.getQuestionId());
                                qj.setCorrectAnswer(correctAnswer);

                                // 判断答案是否正确（需要考虑不同题型的情况）
                                boolean isCorrect = isAnswerCorrect(userAnswer.getUserAnswer(), correctAnswer);
                                qj.setCorrect(isCorrect);
                                qj.setExplanation(explanationsMap.getOrDefault(userAnswer.getQuestionId(), ""));

                                return qj;
                            })
                            .collect(Collectors.toList()));

                    // 4. 计算得分
                    long correctCount = result.getQuestionJudgments().stream()
                            .filter(JudgmentResultDTO.QuestionJudgment::isCorrect)
                            .count();
                    result.setCorrectCount((int) correctCount);
                    result.setScore(calculateScore(correctCount, result.getTotalQuestions()));

                    // 保存判题明细
                    examJudgmentDetailService.saveJudgmentDetails(userAnswers.getTaskId(), result.getQuestionJudgments());

                    return result;
                });
    }

    /**
     * 提取正确答案到Map
     */
    private Map<String, String> extractCorrectAnswers(Object generatedQuestions) {
        return extractFieldFromQuestions(generatedQuestions, "answer");
    }

    /**
     * 提取题目内容到Map
     */
    private Map<String, String> extractQuestionContents(Object generatedQuestions) {
        return extractFieldFromQuestions(generatedQuestions, "questionContent");
    }

    /**
     * 提取解释或陷阱提示到Map
     */
    private Map<String, String> extractExplanations(Object generatedQuestions) {
        Map<String, String> result = new HashMap<>();
        try {
            Map<String, Object> data = (Map<String, Object>) generatedQuestions;
            String questionsJson = (String) ((Map<String, Object>) data.get("outputs")).get("questions");
            Map<String, Object> questionsData = objectMapper.readValue(questionsJson, Map.class);
            List<Map<String, Object>> questions = (List<Map<String, Object>>) questionsData.get("questions");
            //选择题是trapDetection，判断题是explanation，优先获取trapDetection提取到结果的explanation，结果中选择题和判断题都是explanation
            questions.forEach(q -> {
                String questionId = (String) q.get("questionId");
                // 关键修改点：优先获取trapDetection，其次获取explanation
                String explanation = q.containsKey("trapDetection")
                        ? q.get("trapDetection").toString()
                        : q.getOrDefault("explanation", "").toString();
                result.put(questionId, explanation);
            });
        } catch (Exception e) {
            log.error("解析题目说明失败", e);
        }
        return result;
    }


    /**
     * 公共方法：从题目数据中提取指定字段
     */
    private Map<String, String> extractFieldFromQuestions(Object generatedQuestions, String fieldName) {
        Map<String, String> result = new HashMap<>();
        try {
            Map<String, Object> data = (Map<String, Object>) generatedQuestions;
            String questionsJson = (String) ((Map<String, Object>) data.get("outputs")).get("questions");
            Map<String, Object> questionsData = objectMapper.readValue(questionsJson, Map.class);
            List<Map<String, Object>> questions = (List<Map<String, Object>>) questionsData.get("questions");

            questions.forEach(q -> {
                String questionId = (String) q.get("questionId");
                Object fieldValue = q.get(fieldName);
                result.put(questionId, fieldValue != null ? fieldValue.toString() : "");
            });
        } catch (Exception e) {
            log.error("解析题目{}失败", fieldName, e);
        }
        return result;
    }

    /**
     * 判断答案是否正确
     */
    private boolean isAnswerCorrect(String userAnswer, String correctAnswer) {
        if (userAnswer == null || correctAnswer == null) {
            return false;
        }
        // 对于判断题，统一转为小写比较
        if (correctAnswer.equalsIgnoreCase("true") || correctAnswer.equalsIgnoreCase("false")) {
            return userAnswer.equalsIgnoreCase(correctAnswer);
        }
        // 对于选择题，直接比较
        return userAnswer.equals(correctAnswer);
    }

    /**
     * 计算得分（百分制）
     */
    private double calculateScore(long correctCount, int totalQuestions) {
        if (totalQuestions == 0) {
            return 0;//避免除零错误
        }
        return (double) correctCount / totalQuestions * 100;
    }
}