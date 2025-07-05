package com.coldwind.easyoj.controller;
import com.coldwind.easyoj.model.dto.request.ExamRequest;
import com.coldwind.easyoj.model.dto.request.UserAnswersRequest;
import com.coldwind.easyoj.model.dto.response.JudgmentResultDTO;
import com.coldwind.easyoj.model.entity.ExamQuestionDetail;
import com.coldwind.easyoj.service.AnswerStorageService;
import com.coldwind.easyoj.service.DifyExamService;
import com.coldwind.easyoj.service.ExamJudgmentService;
import com.coldwind.easyoj.service.ExamQuestionDetailService;
import com.coldwind.easyoj.service.ExamUserAnswerService;
import com.coldwind.easyoj.service.ExamJudgmentDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.stream.Collectors;


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

    public ExamController(DifyExamService difyExamService, ExamJudgmentService examJudgmentService, AnswerStorageService answerStorageService, ExamQuestionDetailService examQuestionDetailService, ExamUserAnswerService examUserAnswerService, ExamJudgmentDetailService examJudgmentDetailService) {
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
            // 保存到数据库
            String taskId = (String) result.get("task_id");
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            if (taskId != null && data != null) {
                Map<String, Object> outputs = (Map<String, Object>) data.get("outputs");
                if (outputs != null) {
                    String questionsJson = (String) outputs.get("questions");
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        Map<String, Object> questionsMap = objectMapper.readValue(questionsJson, Map.class);
                        List<Map<String, Object>> questions = (List<Map<String, Object>>) questionsMap.get("questions");
                        examQuestionDetailService.saveQuestions(taskId, questions);
                    } catch (Exception e) {
                        log.error("保存题目到数据库失败", e);
                    }
                }
            }
        });
    }
     @PostMapping("/submit-test")
    public Mono<JudgmentResultDTO> judgeAnswers(@RequestBody UserAnswersRequest userAnswers) {
        try {
            // 保存用户答案
            examUserAnswerService.saveUserAnswers(userAnswers.getTaskId(), userAnswers.getAnswers());
            // 1. 优先保存答案，确保即使后续判题失败也能保存
            String savedFilePath = answerStorageService.saveAnswersToTxt(
                userAnswers.getTaskId(), userAnswers);

            // 2. 执行判题逻辑
            return examJudgmentService.judgeAnswers(userAnswers)
                .doOnNext(result -> result.setAnswerFilePath(savedFilePath));
        } catch (Exception e) {
            // 3. 保存失败时记录错误但不中断请求
            log.error("保存用户答案失败: {}", e.getMessage());
            return examJudgmentService.judgeAnswers(userAnswers);
        }
    }

    /**
     * 根据taskId获取题目详情
     */
    @GetMapping("/questions/{taskId}")
    public Mono<Map<String, Object>> getQuestionsByTaskId(@PathVariable String taskId) {
        try {
            // 从数据库获取题目详情
            List<ExamQuestionDetail> questions = examQuestionDetailService.getQuestionsByTaskId(taskId);
            
            if (questions.isEmpty()) {
                return Mono.error(new RuntimeException("未找到题目"));
            }
            
            // 转换为前端需要的格式
            List<Map<String, Object>> questionList = questions.stream()
                .map(q -> {
                    Map<String, Object> question = new HashMap<>();
                    question.put("questionId", q.getQuestionId());
                    question.put("type", q.getType());
                    question.put("content", q.getContent());
                    question.put("optionA", q.getOptionA());
                    question.put("optionB", q.getOptionB());
                    question.put("optionC", q.getOptionC());
                    question.put("optionD", q.getOptionD());
                    question.put("correctAnswer", q.getAnswer());
                    question.put("explanation", q.getExplanation());
                    return question;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("questions", questionList);
            result.put("taskId", taskId);
            
            return Mono.just(result);
        } catch (Exception e) {
            log.error("获取题目失败，taskId: {}", taskId, e);
            return Mono.error(e);
        }
    }
}