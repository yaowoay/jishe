package com.aiinterview.service.ai.dify;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DifyExamService {
    private final WebClient webClient;
    private final Map<String, Map<String, Object>> questionCache;
    private final ScheduledExecutorService cacheCleaner;

    @Value("${dify.exam.api.base-url:https://api.dify.ai/v1}")
    private String difyApiUrl;

    @Value("${dify.exam.api.auth-token:}")
    private String difyApiKey;

    @Value("${answer.cache.expiration-minutes:120}")
    private int cacheExpirationMinutes;

    public DifyExamService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                                .responseTimeout(Duration.ofSeconds(150))
                ))
                .build();

        this.questionCache = new ConcurrentHashMap<>();
        this.cacheCleaner = Executors.newSingleThreadScheduledExecutor();

        this.cacheCleaner.scheduleAtFixedRate(() ->
                questionCache.entrySet().removeIf(entry ->
                        System.currentTimeMillis() - (Long) entry.getValue().get("timestamp") >
                                cacheExpirationMinutes * 60 * 1000
                ), 0, 10, TimeUnit.MINUTES);
    }

    public Mono<Map<String, Object>> generateExamQuestions(
            String jobPosition,
            String skills,
            String experience,
            int questionCount,
            String difficultyLevel,
            String focusArea
    ) {
        String taskId = "task_" + System.currentTimeMillis() + "_" + System.nanoTime();

        log.info("开始生成笔试题目");
        log.info("参数: jobPosition={}, skills={}, experience={}, questionCount={}, difficultyLevel={}, focusArea={}",
                jobPosition, skills, experience, questionCount, difficultyLevel, focusArea);

        Map<String, Object> result = buildMockExamResult(taskId);

        return Mono.delay(Duration.ofSeconds(3))
                .map(ignore -> {
                    Map<String, Object> cacheData = new HashMap<>();
                    cacheData.put("task_id", taskId);
                    cacheData.put("data", result.get("data"));
                    cacheData.put("timestamp", System.currentTimeMillis());
                    questionCache.put(taskId, cacheData);

                    log.info("题目已缓存，taskId: {}, data结构: {}", taskId,
                            ((Map<String, Object>) result.get("data")).keySet());
                    return result;
                });
    }

    private Map<String, Object> buildMockExamResult(String taskId) {
        List<Map<String, Object>> questions = buildFrontendMockQuestions();

        Map<String, Object> questionsContainer = new HashMap<>();
        questionsContainer.put("questions", questions);

        Map<String, Object> outputs = new HashMap<>();
        outputs.put("questions", questionsContainer);
        outputs.put("bs_question", questionsContainer);

        Map<String, Object> data = new HashMap<>();
        data.put("workflow_run_id", "wf_" + System.currentTimeMillis());
        data.put("outputs", outputs);

        Map<String, Object> result = new HashMap<>();
        result.put("task_id", taskId);
        result.put("data", data);
        return result;
    }

    private List<Map<String, Object>> buildFrontendMockQuestions() {
        List<Map<String, Object>> questions = new ArrayList<>();

        questions.add(buildChoiceQuestion(
                "fe_q_001",
                "在 Vue3 中，哪个 API 用于在 setup 中创建响应式对象？",
                "computed",
                "reactive",
                "watch",
                "provide",
                "B",
                "reactive 用于创建响应式对象，computed 用于派生状态。"
        ));

        questions.add(buildChoiceQuestion(
                "fe_q_002",
                "在浏览器中，以下哪种方式最适合持久化保存用户登录 token 且可跨标签页共享？",
                "sessionStorage",
                "localStorage",
                "内存变量",
                "闭包变量",
                "B",
                "localStorage 可持久化并在同源标签页间共享。"
        ));

        questions.add(buildChoiceQuestion(
                "fe_q_003",
                "关于 CSS 盒模型，设置 box-sizing: border-box 后 width 的计算方式是？",
                "只包含 content",
                "content + padding",
                "content + padding + border",
                "content + margin",
                "C",
                "border-box 下 width 包含 content、padding、border。"
        ));

        questions.add(buildChoiceQuestion(
                "fe_q_004",
                "在 React 中，哪个 Hook 用于处理副作用（如请求数据、订阅事件）？",
                "useMemo",
                "useCallback",
                "useReducer",
                "useEffect",
                "D",
                "useEffect 用于处理副作用，依赖数组可控制触发时机。"
        ));

        questions.add(buildChoiceQuestion(
                "fe_q_005",
                "前端性能优化中，以下哪项最能直接减少首屏 JS 执行体积？",
                "图片懒加载",
                "路由级代码分割",
                "开启 SourceMap",
                "增加请求超时时间",
                "B",
                "路由级代码分割可按需加载，减少首屏需解析执行的 JS 体积。"
        ));

        return questions;
    }

    private Map<String, Object> buildChoiceQuestion(String questionId,
                                                    String questionContent,
                                                    String optionA,
                                                    String optionB,
                                                    String optionC,
                                                    String optionD,
                                                    String answer,
                                                    String explanation) {
        Map<String, Object> question = new LinkedHashMap<>();
        question.put("questionId", questionId);
        question.put("type", "choice");
        question.put("questionContent", questionContent);
        question.put("A", optionA);
        question.put("B", optionB);
        question.put("C", optionC);
        question.put("D", optionD);
        question.put("answer", answer);
        question.put("explanation", explanation);
        question.put("knowledgePoint", "前端基础");
        question.put("scenario", "笔试");
        question.put("difficulty", "中等");
        return question;
    }

    public Mono<Map<String, Object>> getGeneratedQuestions(String taskId) {
        return Mono.fromCallable(() -> {
            Map<String, Object> cachedQuestions = questionCache.get(taskId);
            if (cachedQuestions == null) {
                log.warn("题目缓存未找到或已过期: taskId={}, 当前缓存大小={}", taskId, questionCache.size());
                throw new RuntimeException("题目已过期，请重新生成题目。题目缓存时间为" + cacheExpirationMinutes + "分钟");
            }

            long timestamp = (Long) cachedQuestions.get("timestamp");
            long remainingTime = (cacheExpirationMinutes * 60 * 1000) - (System.currentTimeMillis() - timestamp);

            if (remainingTime < 5 * 60 * 1000) {
                log.warn("题目即将过期，剩余时间: {}分钟", remainingTime / 60 / 1000);
            }

            return cachedQuestions;
        });
    }

    @PreDestroy
    public void destroy() {
        if (cacheCleaner != null) {
            cacheCleaner.shutdown();
        }
    }
}
