package com.aiinterview.service.resume;

import cn.hutool.json.JSONUtil;
import com.aiinterview.model.dto.resume.ResumeMatchRequest;
import com.aiinterview.service.ai.dify.DifyFileUploadService;
import com.aiinterview.service.ai.dify.vo.FileInfo;
import com.aiinterview.service.ai.dify.vo.FileItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;

import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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

    public ResumeMatchServiceImpl(DifyFileUploadService difyFileUploadService, WebClient.Builder webClientBuilder) {
        this.difyFileUploadService = difyFileUploadService;
        this.webClient = webClientBuilder
                .baseUrl("https://api.dify.ai/v1")
                .defaultHeader("Authorization", "Bearer " + RESUME_MATCH_API_KEY)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)  // 连接超时30秒
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
            // 上传文件，使用简历匹配服务专用的API Key
            FileInfo fileInfo = difyFileUploadService.uploadFile(resumeFile, "user-" + System.currentTimeMillis(), RESUME_MATCH_API_KEY);
            if (fileInfo == null) {
                throw new RuntimeException("文件上传失败");
            }
            log.info("文件上传成功: fileInfo={}", JSONUtil.toJsonStr(fileInfo));
            FileItem fileItem = FileItem.buildByLocal(fileInfo.getId());

            // 将文件内容转为 Base64 字符串
            byte[] fileBytes = resumeFile.getBytes();
            String base64Content = Base64.getEncoder().encodeToString(fileBytes);

            // 构建请求体
            Map<String, Object> inputs = new HashMap<>();
            inputs.put("jobPosition", jobPosition);
            inputs.put("jobDescription", jobDescription);

            Map<String, Object> fileObject = new HashMap<>();
            fileObject.put("name", resumeFile.getOriginalFilename());
            fileObject.put("content", base64Content);
            String contentType = resumeFile.getContentType();
            if (contentType == null || contentType.equals("None")) {
                contentType = detectContentType(resumeFile.getOriginalFilename());
            }
            fileObject.put("type", contentType);
            inputs.put("updownCV", fileObject);
            // 文件类型是个文件列表
            inputs.put("updownCV", Arrays.asList(fileItem));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", "blocking"); // 使用blocking模式
            requestBody.put("user", "user-" + System.currentTimeMillis());

            log.info("调用 Dify API 进行简历匹配分析: requestBody={}", requestBody);

            // 发送 POST 请求到 Dify API
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
                    .doOnNext(response -> log.info("Dify API 调用成功: response={}", JSONUtil.toJsonStr(response)))
                    .doOnError(e -> log.error("Dify API 调用失败: {}", e.getMessage(), e));
        } catch (Exception e) {
            log.error("处理简历文件失败: {}", e.getMessage(), e);
            return Mono.error(new RuntimeException("处理简历文件失败: " + e.getMessage()));
        }
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
