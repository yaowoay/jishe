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

        log.info("开始调用 Dify API 生成笔试题目");
        log.info("参数: jobPosition={}, skills={}, experience={}, questionCount={}, difficultyLevel={}, focusArea={}",
                jobPosition, skills, experience, questionCount, difficultyLevel, focusArea);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();

        // 构建 inputs 参数
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("jobPosition", jobPosition);
        inputs.put("skills", skills);
        inputs.put("exp", experience);
        inputs.put("questionCount", questionCount);
        inputs.put("level", difficultyLevel);
        inputs.put("focusArea", focusArea);


        requestBody.put("inputs", inputs);
        requestBody.put("response_mode", "sreaming"); //非阻塞式异步响应
        requestBody.put("user", "user-" + System.currentTimeMillis());

        log.info("Dify API 请求体: {}", JSONUtil.toJsonStr(requestBody));

        // 调用 Dify API
        return webClient.post()
                .uri(difyApiUrl + "/workflows/run")
                .header("Authorization", "Bearer " + difyApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnNext(response -> {
                    log.info("Dify API 响应: {}", JSONUtil.toJsonStr(response));
                })
                .map(response -> {
                    // 从 Dify 响应中提取 data 节点
                    Map<String, Object> difyData = (Map<String, Object>) response.get("data");

                    // 构建返回结果，保持与 Controller 期望的结构一致
                    Map<String, Object> result = new HashMap<>();
                    result.put("task_id", taskId);
                    result.put("data", difyData);  // 直接使用 Dify 返回的 data 节点

                    // 缓存结果
                    Map<String, Object> cacheData = new HashMap<>();
                    cacheData.put("task_id", taskId);
                    cacheData.put("data", difyData);
                    cacheData.put("timestamp", System.currentTimeMillis());
                    questionCache.put(taskId, cacheData);

                    log.info("题目已缓存，taskId: {}, data结构: {}", taskId, difyData != null ? difyData.keySet() : "null");

                    return result;
                })
                .doOnError(error -> {
                    log.error("调用 Dify API 失败", error);
                })
                .onErrorResume(error -> {
                    log.error("Dify API 调用失败，返回错误信息: {}", error.getMessage());
                    return Mono.error(new RuntimeException("生成笔试题目失败: " + error.getMessage()));
                });
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
