package com.coldwind.easyoj.service;
//package org.iflyproject.springdemos.service;
//import org.ehcache.xml.model.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class DifyExamService {
    private static final Logger log = LoggerFactory.getLogger(DifyExamService.class);
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

    /**
     * 基于简历生成试题
     */
    public Mono<Map<String, Object>> generateExamQuestionsWithResume(
            MultipartFile resumeFile,
            String jobPosition,
            String skills,
            String experience,
            int questionCount,
            String difficultyLevel,
            String focusArea
    ) {
        try {
            // 将文件内容转换为base64编码
            byte[] fileBytes = resumeFile.getBytes();
            String base64Content = java.util.Base64.getEncoder().encodeToString(fileBytes);
            
            Map<String, Object> inputs = new HashMap<>();
            inputs.put("jobPosition", jobPosition);
            inputs.put("skills", skills);
            inputs.put("exp", experience);
            inputs.put("questionCount", questionCount);
            inputs.put("level", difficultyLevel);
            inputs.put("focusArea", focusArea);
            
            // 构造文件对象，包含文件名和base64内容
            Map<String, Object> fileObject = new HashMap<>();
            fileObject.put("name", resumeFile.getOriginalFilename());
            fileObject.put("content", base64Content);
            fileObject.put("type", resumeFile.getContentType());
            inputs.put("updownCV", fileObject);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", "blocking");
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
        } catch (Exception e) {
            return Mono.error(new RuntimeException("处理简历文件失败: " + e.getMessage()));
        }
    }

    /**
     * 基于简历文件路径生成试题
     */
    public Mono<Map<String, Object>> generateExamQuestionsWithResumePath(
            String resumeFilePath,
            String jobPosition,
            String skills,
            String experience,
            int questionCount,
            String difficultyLevel,
            String focusArea
    ) {
        try {
            log.info("开始处理简历文件路径生成试题: resumeFilePath={}", resumeFilePath);
            
            // 从URL中提取实际文件路径
            String actualFilePath = extractFilePathFromUrl(resumeFilePath);
            log.info("提取的实际文件路径: {}", actualFilePath);
            
            // 从文件路径读取文件内容
            java.nio.file.Path path = java.nio.file.Paths.get(actualFilePath);
            byte[] fileBytes = java.nio.file.Files.readAllBytes(path);
            String base64Content = java.util.Base64.getEncoder().encodeToString(fileBytes);
            log.info("文件转换为base64完成，长度: {}", base64Content.length());
            
            Map<String, Object> inputs = new HashMap<>();
            inputs.put("jobPosition", jobPosition);
            inputs.put("skills", skills);
            inputs.put("exp", experience);
            inputs.put("questionCount", questionCount);
            inputs.put("level", difficultyLevel);
            inputs.put("focusArea", focusArea);
            
            // 构造文件对象，包含文件名和base64内容
            Map<String, Object> fileObject = new HashMap<>();
            fileObject.put("name", path.getFileName().toString());
            fileObject.put("content", base64Content);
            fileObject.put("type", "application/pdf"); // 默认PDF类型
            inputs.put("updownCV", fileObject);
            
            log.info("构造请求参数完成: inputs={}", inputs);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", "user-" + System.currentTimeMillis());

            log.info("开始调用Dify API...");
            return webClient.post()
                    .uri("/workflows/run")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("Dify API调用失败: status={}, body={}", response.statusCode(), body);
                                        return Mono.error(new RuntimeException(
                                                "Dify API 错误: " + response.statusCode() + " - " + body
                                        ));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .doOnNext(response -> {
                        log.info("Dify API调用成功: response={}", response);
                        // 直接缓存完整响应
                        String taskId = (String) response.get("task_id");
                        if (taskId != null) {
                            Map<String, Object> data = (Map<String, Object>) response.get("data");
                            if (data != null) {
                                data.put("timestamp", System.currentTimeMillis());
                                questionCache.put(taskId, data);
                                log.info("试题缓存成功: taskId={}", taskId);
                            }
                        }
                    })
                    .doOnError(error -> log.error("Dify API调用异常: {}", error.getMessage(), error));
        } catch (Exception e) {
            log.error("处理简历文件路径失败: {}", e.getMessage(), e);
            return Mono.error(new RuntimeException("处理简历文件路径失败: " + e.getMessage()));
        }
    }

    /**
     * 从URL中提取实际文件路径
     */
    private String extractFilePathFromUrl(String fileUrl) {
        try {
            // 如果已经是文件路径，直接返回
            if (fileUrl.startsWith("/") || fileUrl.contains(":")) {
                return fileUrl;
            }
            
            // 从URL中提取路径部分
            // 例如：/api/file/download/resume/123/abc123.pdf -> resume/123/abc123.pdf
            String path = fileUrl;
            if (path.startsWith("http://") || path.startsWith("https://")) {
                // 移除协议和域名部分
                path = path.substring(path.indexOf("/", 8));
            }
            
            // 移除/api/file/download/前缀
            if (path.startsWith("/api/file/download/")) {
                path = path.substring("/api/file/download/".length());
            }
            
            // 构造实际的文件路径
            String basePath = "E:/java_code/rjb_oj/easyojandlogin/src/main/resources/txt"; // 从配置中获取
            return basePath + "/" + path;
        } catch (Exception e) {
            log.error("提取文件路径失败: {}", e.getMessage());
            throw new RuntimeException("无法解析文件路径: " + fileUrl);
        }
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