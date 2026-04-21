package com.aiinterview.controller.workflow;

import com.aiinterview.model.dto.exam.UserAnswersRequest;
import com.aiinterview.model.dto.exam.ExamRequest;
import com.aiinterview.model.dto.exam.JudgmentResultDTO;
import com.aiinterview.model.entity.exam.ExamQuestionDetail;
import com.aiinterview.service.ai.dify.DifyExamService;
import com.aiinterview.service.exam.ExamJudgmentService;
import com.aiinterview.service.exam.ExamQuestionDetailService;
import com.aiinterview.service.exam.AnswerStorageService;
import com.aiinterview.service.exam.ExamUserAnswerService;
import com.aiinterview.service.exam.ExamJudgmentDetailService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@PreAuthorize("hasRole('xxx')")
@RequestMapping("/exam")
public class ExamController {

    private final DifyExamService difyExamService;
    private final ExamJudgmentService examJudgmentService;
    private final AnswerStorageService answerStorageService;
    private final ExamQuestionDetailService examQuestionDetailService;
    private final ExamUserAnswerService examUserAnswerService;
    private final ExamJudgmentDetailService examJudgmentDetailService;

    public ExamController(DifyExamService difyExamService, ExamJudgmentService examJudgmentService, 
                         AnswerStorageService answerStorageService, ExamQuestionDetailService examQuestionDetailService, 
                         ExamUserAnswerService examUserAnswerService, ExamJudgmentDetailService examJudgmentDetailService) {
        this.difyExamService = difyExamService;
        this.examJudgmentService = examJudgmentService;
        this.answerStorageService = answerStorageService;
        this.examQuestionDetailService = examQuestionDetailService;
        this.examUserAnswerService = examUserAnswerService;
        this.examJudgmentDetailService = examJudgmentDetailService;
    }

    @PostMapping("/generate")
    public Mono<Map<String, Object>> generateExam(@RequestBody ExamRequest request) {
        // 将枚举转换为字符串表示
        // 正确处理ExamRequest中的枚举类型，将其转换为服务层需要的字符串格式，保持了API调用的数据一致。

        String experience = request.getExperience() != null ?
                request.getExperience().getDisplayName() : null;

        String difficultyLevel = request.getDifficultyLevel() != null ?
                request.getDifficultyLevel().getDisplayName() : null;
        //如果Dify API期望的是枚举名称而不是显示名称，可以将getDisplayName()改为name()方法。
        return difyExamService.generateExamQuestions(
                request.getJobPosition(),
                request.getSkills(),
                experience,
                request.getQuestionCount(),
                difficultyLevel,
                request.getFocusArea()
        ).doOnNext(result -> {
            // 解析题目结果并保存到数据库
            this.processAndSaveQuestions(result);
        });
    }

    // 解析JSON并保存到数据库
    private void processAndSaveQuestions(Map<String, Object> result) {
        try {
            log.info("开始处理并保存题目，完整响应结构: {}", result);

            String taskId = (String) result.get("task_id");
            if (taskId == null) {
                log.warn("任务ID为空，无法保存题目");
                return;
            }
            log.info("任务ID: {}", taskId);

            Map<String, Object> data = (Map<String, Object>) result.get("data");
            if (data == null) {
                log.warn("任务{}的data节点为空", taskId);
                return;
            }
            log.info("data节点内容: {}", data);

            Map<String, Object> outputs = (Map<String, Object>) data.get("outputs");
            if (outputs == null) {
                log.warn("任务{}的outputs节点为空", taskId);
                return;
            }
            log.info("outputs节点内容: {}", outputs);
            log.info("outputs节点的所有key: {}", outputs.keySet());

            // 尝试多种可能的字段名
            Object questionsObj = outputs.get("questions");
            if (questionsObj == null) {
                questionsObj = outputs.get("bs_question");
            }
            if (questionsObj == null) {
                questionsObj = outputs.get("result");
            }

            if (questionsObj == null) {
                log.warn("任务{}的questions节点为空，outputs中的所有key: {}", taskId, outputs.keySet());
                return;
            }
            log.info("questions对象类型: {}, 内容: {}", questionsObj.getClass().getName(), questionsObj);

            List<Map<String, Object>> questions = null;

            // 如果是字符串，需要解析JSON
            if (questionsObj instanceof String) {
                log.info("questions是字符串类型，需要解析JSON");
                String questionsJson = (String) questionsObj;
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Map<String, Object> questionsMap = objectMapper.readValue(questionsJson, Map.class);
                questions = (List<Map<String, Object>>) questionsMap.get("questions");
            }
            // 如果是Map，从中提取questions列表
            else if (questionsObj instanceof Map) {
                log.info("questions是Map类型");
                Map<String, Object> questionsContainer = (Map<String, Object>) questionsObj;
                questions = (List<Map<String, Object>>) questionsContainer.get("questions");
            }
            // 如果直接是List
            else if (questionsObj instanceof List) {
                log.info("questions是List类型");
                questions = (List<Map<String, Object>>) questionsObj;
            }

            if (questions == null || questions.isEmpty()) {
                log.warn("任务{}没有找到题目数据，questionsObj类型: {}", taskId, questionsObj.getClass().getName());
                return;
            }

            log.info("解析到{}道题目，准备保存到数据库", questions.size());
            examQuestionDetailService.saveQuestions(taskId, questions);
            log.info("任务{}的{}道题目已成功保存到数据库", taskId, questions.size());

        } catch (Exception e) {
            log.error("处理和保存题目失败", e);
        }
    }


    @PostMapping("/submit-test")
    public Mono<JudgmentResultDTO> submitExam(@RequestBody UserAnswersRequest request) {
        log.info("提交笔试答案，taskId: {}", request.getTaskId());
        
        return examJudgmentService.judgeAnswers(request)
                .doOnNext(result -> {
                    try {
                        for (UserAnswersRequest.UserAnswer answer : request.getAnswers()) {
                            examUserAnswerService.saveAnswer(request.getTaskId(), answer.getQuestionId(), answer.getUserAnswer());
                        }
                        for (JudgmentResultDTO.QuestionJudgment judgment : result.getQuestionJudgments()) {
                            examJudgmentDetailService.saveJudgmentDetail(
                                    request.getTaskId(),
                                    judgment.getQuestionId(),
                                    judgment.getUserAnswer(),
                                    judgment.getCorrectAnswer(),
                                    judgment.isCorrect(),
                                    judgment.getExplanation()
                            );
                        }
                    } catch (Exception e) {
                        log.error("保存答案和判题结果失败", e);
                    }
                });
    }

    @GetMapping("/questions/{taskId}")
    public Mono<Map<String, Object>> getExamQuestions(@PathVariable String taskId) {
        log.info("获取笔试题目，taskId: {}", taskId);
        
        return Mono.fromCallable(() -> {
            List<ExamQuestionDetail> questions = examQuestionDetailService.getQuestionsByTaskId(taskId);

            if (questions == null || questions.isEmpty()) {
                throw new RuntimeException("未找到题目");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("questions", questions);
            result.put("totalCount", questions.size());
            
            return result;
        });
    }
}
