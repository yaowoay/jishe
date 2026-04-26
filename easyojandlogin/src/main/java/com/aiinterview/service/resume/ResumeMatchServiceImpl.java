package com.aiinterview.service.resume;

import cn.hutool.json.JSONUtil;
import com.aiinterview.model.dto.resume.ResumeMatchRequest;
import com.aiinterview.service.ai.dify.DifyFileUploadService;
import com.aiinterview.service.ai.dify.vo.FileInfo;
import com.aiinterview.service.ai.dify.vo.FileItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.PrematureCloseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 简历匹配分析服务实现
 */
@Service
@Slf4j
public class ResumeMatchServiceImpl implements ResumeMatchService {

    private final WebClient webClient;
    private final DifyFileUploadService difyFileUploadService;

    // 简历匹配服务专用的API Key
    private static final String RESUME_MATCH_API_KEY = "app-zycdQrBx17QVDhynWelO9H8Q";

    // streaming 模式下，通常能更早收到首包，降低网关超时概率
    private static final Duration DIFY_CALL_TIMEOUT = Duration.ofSeconds(180);
    private static final int MAX_RETRY_TIMES = 1;

    public ResumeMatchServiceImpl(DifyFileUploadService difyFileUploadService, WebClient.Builder webClientBuilder) {
        this.difyFileUploadService = difyFileUploadService;
        this.webClient = webClientBuilder
                .baseUrl("https://api.dify.ai/v1")
                .defaultHeader("Authorization", "Bearer " + RESUME_MATCH_API_KEY)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300000)  // 连接超时30秒
                        .responseTimeout(Duration.ofSeconds(300))  // 响应超时5分钟
                ))
                .build();
    }

    @Override
    public Mono<Map<String, Object>> analyzeResumeMatch(ResumeMatchRequest request) {
        MultipartFile resumeFile = request.getUpdownCV();
        String jobPosition = request.getJobPosition();
        String jobDescription = request.getJobDescription();

        try {
            String userId = "user-" + System.currentTimeMillis();
            FileInfo fileInfo = difyFileUploadService.uploadFile(resumeFile, userId, RESUME_MATCH_API_KEY);
            if (fileInfo == null) {
                throw new RuntimeException("文件上传失败");
            }
            FileItem fileItem = FileItem.buildByLocal(fileInfo.getId());

            Map<String, Object> inputs = new HashMap<>();
            inputs.put("jobPosition", jobPosition);
            inputs.put("jobDescription", jobDescription);
            inputs.put("updownCV", java.util.Collections.singletonList(fileItem));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", "streaming");
            requestBody.put("user", userId);

            log.info("调用 Dify API 简历匹配(streaming): userId={}, jobPosition={}, fileId={}",
                    userId, jobPosition, fileInfo.getId());

            ObjectMapper mapper = new ObjectMapper();

            Mono<Map<String, Object>> streamingCall = webClient.post()
                    .uri("/workflows/run")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                    .mapNotNull(ServerSentEvent::data)
                    .map(String::trim)
                    .filter(data -> !data.isEmpty() && !"[DONE]".equals(data))
                    .flatMap(data -> {
                        try {
                            Map<String, Object> event = mapper.readValue(data, Map.class);
                            return Mono.just(event);
                        } catch (Exception e) {
                            log.warn("解析简历匹配 SSE 事件失败: {}", data);
                            return Mono.empty();
                        }
                    })
                    .filter(event -> "workflow_finished".equals(event.get("event")))
                    .next()
                    .flatMap(event -> {
                        Object eventData = event.get("data");
                        if (eventData == null) {
                            return Mono.error(new RuntimeException("workflow_finished 事件缺少 data 字段"));
                        }
                        try {
                            String resultJson = mapper.writeValueAsString(eventData);
                            Map<String, Object> response = mapper.readValue(resultJson, Map.class);
                            return Mono.just(response);
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException("解析 Dify 响应失败: " + e.getMessage()));
                        }
                    })
                    .switchIfEmpty(Mono.error(new RuntimeException("未收到 workflow_finished 事件")));

            Mono<Map<String, Object>> blockingFallbackCall = webClient.post()
                    .uri("/workflows/run")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new HashMap<String, Object>() {{
                        put("inputs", inputs);
                        put("response_mode", "blocking");
                        put("user", userId + "-fallback");
                    }})
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(body -> Mono.error(new RuntimeException(
                                            "Dify API 错误: " + response.statusCode() + " - " + body
                                    )))
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

            return streamingCall
                    .onErrorResume(error -> {
                        if (shouldFallbackToBlocking(error)) {
                            log.warn("streaming 模式失败，回退 blocking 模式: {}", error.getMessage());
                            return blockingFallbackCall;
                        }
                        return Mono.error(error);
                    })
                    .timeout(DIFY_CALL_TIMEOUT)
                    .retryWhen(
                            Retry.backoff(MAX_RETRY_TIMES, Duration.ofSeconds(1))
                                    .filter(this::shouldRetry)
                                    .onRetryExhaustedThrow((spec, signal) -> signal.failure())
                    )
                    .doOnNext(response -> log.info("Dify API 调用成功: {}", JSONUtil.toJsonStr(response)))
                    .doOnError(e -> log.error("Dify API 调用失败: {}", e.getMessage(), e));
        } catch (Exception e) {
            log.error("处理简历文件失败: {}", e.getMessage(), e);
            return Mono.error(new RuntimeException("处理简历文件失败: " + e.getMessage()));
        }
    }

    private boolean shouldFallbackToBlocking(Throwable throwable) {
        String message = throwable.getMessage();
        return message != null && (
                message.contains("未收到 workflow_finished 事件")
                        || message.contains("workflow_finished 事件缺少 data 字段")
                        || message.contains("PrematureCloseException")
                        || message.contains("Connection prematurely closed")
        );
    }

    private boolean shouldRetry(Throwable throwable) {
        if (throwable instanceof TimeoutException || throwable instanceof PrematureCloseException) {
            return true;
        }
        if (throwable instanceof WebClientResponseException) {
            Throwable cause = throwable.getCause();
            if (cause instanceof PrematureCloseException) {
                return true;
            }
        }
        String message = throwable.getMessage();
        return message != null && (
                message.contains("504")
                        || message.contains("GATEWAY_TIMEOUT")
                        || message.contains("PrematureCloseException")
                        || message.contains("Connection prematurely closed")
        );
    }

    /**
     * 根据文件名检测内容类型
     */
    private String detectContentType(String fileName) {
        if (fileName == null) return "application/octet-stream";
        if (fileName.endsWith(".pdf")) return "application/pdf";
        if (fileName.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (fileName.endsWith(".doc")) return "application/msword";
        return "application/octet-stream"; // 默认类型
    }
}
