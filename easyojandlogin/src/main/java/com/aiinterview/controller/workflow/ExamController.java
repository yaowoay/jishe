package com.aiinterview.controller.workflow;

import com.aiinterview.model.dto.exam.UserAnswersRequest;
import com.aiinterview.model.dto.exam.ExamRequest;
import com.aiinterview.model.dto.exam.JudgmentResultDTO;
import com.aiinterview.service.ai.dify.DifyExamService;
import com.aiinterview.service.exam.ExamJudgmentService;
import com.aiinterview.service.exam.ExamQuestionDetailService;
import com.aiinterview.service.exam.AnswerStorageService;
import com.aiinterview.service.exam.ExamUserAnswerService;
import com.aiinterview.service.exam.ExamJudgmentDetailService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
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
        log.info("收到笔试题目生成请求: {}", request);
        
        String experience = request.getExperience() != null ?
                request.getExperience().getDisplayName() : null;

        String difficultyLevel = request.getDifficultyLevel() != null ?
                request.getDifficultyLevel().getDisplayName() : null;

        return difyExamService.generateExamQuestions(
                request.getJobPosition(),
                request.getSkills(),
                experience,
                request.getQuestionCount(),
                difficultyLevel,
                request.getFocusArea()
        ).doOnNext(result -> {
            this.processAndSaveQuestions(result);
        });
    }

    private void processAndSaveQuestions(Map<String, Object> result) {
        String taskId = (String) result.get("task_id");
        if (taskId == null) {
            log.warn("任务ID为空，无法保存题目");
            return;
        }

        Map<String, Object> data = (Map<String, Object>) result.get("data");
        if (data == null) {
            log.warn("任务{}的data节点为空", taskId);
            return;
        }

        Map<String, Object> outputs = (Map<String, Object>) data.get("outputs");
        if (outputs == null) {
            log.warn("任务{}的outputs节点为空", taskId);
            return;
        }

        Map<String, Object> questionsContainer = (Map<String, Object>) outputs.get("questions");
        if (questionsContainer == null) {
            log.warn("任务{}的questions容器节点为空", taskId);
            return;
        }

        List<Map<String, Object>> questions = (List<Map<String, Object>>) questionsContainer.get("questions");
        if (questions == null || questions.isEmpty()) {
            log.warn("任务{}没有找到题目数据", taskId);
            return;
        }

        try {
            examQuestionDetailService.saveQuestions(taskId, questions);
            log.info("任务{}的{}道题目已成功保存到数据库", taskId, questions.size());
        } catch (Exception e) {
            log.error("保存任务{}的题目到数据库失败", taskId, e);
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
            var questions = examQuestionDetailService.getQuestionsByTaskId(taskId);
            
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
