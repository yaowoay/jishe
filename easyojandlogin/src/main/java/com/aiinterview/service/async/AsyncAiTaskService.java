package com.aiinterview.service.async;

import cn.hutool.json.JSONUtil;
import com.aiinterview.model.dto.writtenTest.WrittenTestGenerationRequest;
import com.aiinterview.model.dto.writtenTest.WrittenTestGenerationResponse;
import com.aiinterview.service.resume.ResumeMatchService;
import com.aiinterview.service.writtenTest.WrittenTestGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * AI任务异步编排服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncAiTaskService {

    private final WrittenTestGenerationService writtenTestGenerationService;
    private final ResumeMatchService resumeMatchService;

    @Qualifier("aiTaskExecutor")
    private final Executor aiTaskExecutor;

    private final Map<String, Map<String, Object>> taskStore = new ConcurrentHashMap<>();

    public String submitWrittenTestTask(WrittenTestGenerationRequest request) {
        String taskId = buildTaskId("written");
        Map<String, Object> task = initTask(taskId, "WRITTEN_TEST_GENERATION", "PENDING");
        taskStore.put(taskId, task);

        CompletableFuture.runAsync(() -> {
            task.put("status", "RUNNING");
            try {
                WrittenTestGenerationResponse result = writtenTestGenerationService.generateWrittenTest(request);
                task.put("status", "SUCCESS");
                task.put("result", result);
            } catch (Exception e) {
                log.error("异步笔试题生成失败, taskId={}", taskId, e);
                task.put("status", "FAILED");
                task.put("error", e.getMessage());
            } finally {
                task.put("finishedAt", System.currentTimeMillis());
            }
        }, aiTaskExecutor);

        return taskId;
    }

    public String submitResumeMatchTask(com.aiinterview.model.dto.resume.ResumeMatchRequest request) {
        String taskId = buildTaskId("match");
        Map<String, Object> task = initTask(taskId, "RESUME_MATCH_ANALYZE", "PENDING");
        taskStore.put(taskId, task);

        CompletableFuture.runAsync(() -> {
            task.put("status", "RUNNING");
            try {
                Map<String, Object> result = resumeMatchService.analyzeResumeMatch(request).block();
                task.put("status", "SUCCESS");
                task.put("result", result);
            } catch (Exception e) {
                log.error("异步简历匹配分析失败, taskId={}", taskId, e);
                task.put("status", "FAILED");
                task.put("error", e.getMessage());
            } finally {
                task.put("finishedAt", System.currentTimeMillis());
            }
        }, aiTaskExecutor);

        return taskId;
    }

    public Mono<Map<String, Object>> getTaskResult(String taskId) {
        return Mono.fromCallable(() -> {
            Map<String, Object> task = taskStore.get(taskId);
            if (task == null) {
                Map<String, Object> notFound = new ConcurrentHashMap<>();
                notFound.put("taskId", taskId);
                notFound.put("status", "NOT_FOUND");
                notFound.put("message", "任务不存在或已过期");
                return notFound;
            }
            return task;
        });
    }

    private Map<String, Object> initTask(String taskId, String taskType, String status) {
        Map<String, Object> task = new ConcurrentHashMap<>();
        task.put("taskId", taskId);
        task.put("taskType", taskType);
        task.put("status", status);
        task.put("createdAt", System.currentTimeMillis());
        log.info("创建异步AI任务: {}", JSONUtil.toJsonStr(task));
        return task;
    }

    private String buildTaskId(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + System.nanoTime();
    }
}
