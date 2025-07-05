package com.coldwind.easyoj.service;

//package org.iflyproject.springdemos.service;




import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;



@Service
public class DifyExamService {
    private final WebClient webClient;
    private final Map<String, Map<String, Object>> questionCache;
    private final ScheduledExecutorService cacheCleaner;

    @Value("${answer.cache.expiration-minutes:30}")
    private int cacheExpirationMinutes;

    public DifyExamService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost/v1")
                .defaultHeader("Authorization", "Bearer app-iCdufyD7xNGAuQO209OverNh")
                .build();

        this.questionCache = new ConcurrentHashMap<>();
        this.cacheCleaner = Executors.newSingleThreadScheduledExecutor();

        this.cacheCleaner.scheduleAtFixedRate(() ->
                questionCache.entrySet().removeIf(entry ->
                        System.currentTimeMillis() - (Long) entry.getValue().get("timestamp") >
                                cacheExpirationMinutes * 60 * 1000
                ), 0, 1, TimeUnit.HOURS);
    }

    public Mono<Map<String, Object>> generateExamQuestions(
            String jobPosition,
            String skills,
            String experience,
            int questionCount,
            String difficultyLevel,
            String focusArea
    ) {
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("jobPosition", jobPosition);
        inputs.put("skills", skills);
        inputs.put("exp", experience);
        inputs.put("questionCount", questionCount);
        inputs.put("level", difficultyLevel);
        inputs.put("focusArea", focusArea);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", inputs);
        requestBody.put("response_mode", "blocking"); // 明确使用blocking模式
        requestBody.put("user", "user-" + System.currentTimeMillis());

        return webClient.post()
                .uri("/workflows/run")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException(
                                        "Dify API 错误: " + response.statusCode() + " - " + body
                                )))
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnNext(response -> {
                    // 直接缓存完整响应
                    String taskId = (String) response.get("task_id");
                    if (taskId != null) {
                        Map<String, Object> data = (Map<String, Object>) response.get("data");
                        if (data != null) {
                            data.put("timestamp", System.currentTimeMillis());
                            questionCache.put(taskId, data);
                        }
                    }
                });
//                .map(response -> {
//                    // 返回标准化响应结构
//                    Map<String, Object> data = (Map<String, Object>) response.get("data");
//                    if (data != null && "succeeded".equals(data.get("status"))) {
//                        return (Map<String, Object>) data.get("outputs"); //只返回outputs部分
//                    }
//                    throw new RuntimeException("Dify工作流执行失败: " + data.get("error"));
//                });
    }

    public Mono<Map<String, Object>> getGeneratedQuestions(String taskId) {
        return Mono.fromCallable(() -> {
            Map<String, Object> cachedQuestions = questionCache.get(taskId);
            if (cachedQuestions == null) {
                throw new RuntimeException("题目不存在或已过期: taskId=" + taskId);
            }
            return cachedQuestions;
        });
    }

    // public Map<String, Map<String, Object>> getQuestionCache() {
    //     return this.questionCache;
    // }

    @PreDestroy
    public void cleanup() {
        cacheCleaner.shutdown();
    }
}