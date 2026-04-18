package com.aiinterview.service.ai.dify;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.HashMap;
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
                                .responseTimeout(Duration.ofSeconds(120))
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

        // 临时使用本地模拟数据，避免 Dify API 调用失败
        log.info("使用本地模拟数据生成笔试题目");

        // 创建模拟题目列表 - 字段名必须与数据库表和 ExamQuestionDetailServiceImpl 中的字段名一致
        java.util.List<Map<String, Object>> questionsList = new java.util.ArrayList<>();

        Map<String, Object> q1 = new HashMap<>();
        q1.put("questionId", "q1");
        q1.put("type", "choice");
        q1.put("questionContent", "什么是 " + jobPosition + "？");
        q1.put("A", "选项A");
        q1.put("B", "选项B");
        q1.put("C", "选项C");
        q1.put("D", "选项D");
        q1.put("answer", "A");
        q1.put("difficulty", "初级");
        q1.put("knowledgePoint", jobPosition + "基础");
        q1.put("scenario", "职位定义");
        q1.put("trapDetection", "注意区分相似概念");
        questionsList.add(q1);

        Map<String, Object> q2 = new HashMap<>();
        q2.put("questionId", "q2");
        q2.put("type", "choice");
        q2.put("questionContent", "请解释 " + skills + " 的核心概念");
        q2.put("A", "概念1");
        q2.put("B", "概念2");
        q2.put("C", "概念3");
        q2.put("D", "概念4");
        q2.put("answer", "B");
        q2.put("difficulty", "中级");
        q2.put("knowledgePoint", skills + "核心概念");
        q2.put("scenario", "技能应用");
        q2.put("trapDetection", "容易混淆的概念");
        questionsList.add(q2);

        Map<String, Object> q3 = new HashMap<>();
        q3.put("questionId", "q3");
        q3.put("type", "true_false");
        q3.put("questionContent", "根据 " + experience + " 的经验，以下说法是否正确？");
        q3.put("A", "正确");
        q3.put("B", "错误");
        q3.put("answer", "A");
        q3.put("difficulty", "高级");
        q3.put("knowledgePoint", experience + "经验应用");
        q3.put("scenario", "实际问题处理");
        q3.put("trapDetection", "需要实际经验判断");
        questionsList.add(q3);

        Map<String, Object> mockQuestions = new HashMap<>();
        mockQuestions.put("questions", questionsList);

        Map<String, Object> outputs = new HashMap<>();
        outputs.put("questions", mockQuestions);

        Map<String, Object> data = new HashMap<>();
        data.put("outputs", outputs);

        Map<String, Object> result = new HashMap<>();
        result.put("task_id", taskId);
        result.put("data", data);

        // 缓存结果
        Map<String, Object> cacheData = new HashMap<>();
        cacheData.put("task_id", taskId);
        cacheData.put("data", data);
        cacheData.put("timestamp", System.currentTimeMillis());
        questionCache.put(taskId, cacheData);

        log.info("本地模拟题目已缓存，taskId: {}", taskId);

        return Mono.just(result);
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
