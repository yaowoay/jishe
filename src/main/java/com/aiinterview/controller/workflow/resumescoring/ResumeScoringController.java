package com.aiinterview.controller.workflow.resumescoring;

import com.aiinterview.config.DifyApiConfig;
import com.aiinterview.constants.DifyApiConstants;
import com.aiinterview.model.dto.workflow.resumescoring.ResumeScoringRequest;
import com.aiinterview.model.dto.workflow.resumescoring.ResumeScoringResponse;
import com.aiinterview.constants.WorkflowType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 简历筛选和打分控制器
 * 调用 Dify Chat Messages API 进行简历分析和打分
 */
@Slf4j
@RestController
@RequestMapping("/resume-scoring")
@CrossOrigin(origins = "*")
@Validated
public class ResumeScoringController {

    @Autowired
    private DifyApiConfig difyApiConfig;

    private final RestTemplate restTemplate;

    public ResumeScoringController() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 简历筛选和打分接口
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeResume(@Valid @RequestBody ResumeScoringRequest request) {
        log.info("开始简历筛选和打分: uploadFileId={}, jd={}, user={}", 
                request.getUploadFileId(), request.getJd(), request.getUser());

        try {
            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(request);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", difyApiConfig.getAuthorizationHeader(WorkflowType.RESUME_SCORING));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 调用 Dify Chat Messages API
            String url = DifyApiConstants.getChatMessagesUrl();
            log.info("调用 Dify Chat Messages API: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 解析响应
            String responseBody = response.getBody();
            log.info("Dify API 响应: {}", responseBody);

            if (responseBody != null) {
                JSONObject responseJson = JSON.parseObject(responseBody);
                
                // 提取关键信息
                String messageId = responseJson.getString("message_id");
                String conversationId = responseJson.getString("conversation_id");
                String answer = responseJson.getString("answer");
                Long createdAt = responseJson.getLong("created_at");

                // 构建成功响应
                ResumeScoringResponse scoringResponse = ResumeScoringResponse.success(
                        messageId, conversationId, answer, createdAt);

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "简历筛选和打分成功");
                result.put("data", scoringResponse);

                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "Dify API 响应为空");
                return ResponseEntity.ok(result);
            }

        } catch (Exception e) {
            log.error("简历筛选和打分失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "简历筛选和打分失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(ResumeScoringRequest request) {
        Map<String, Object> requestBody = new HashMap<>();

        // 构建 inputs
        Map<String, Object> inputs = new HashMap<>();
        
        // 构建 cv 数组
        Map<String, Object> cvItem = new HashMap<>();
        cvItem.put("transfer_method", "local_file");
        cvItem.put("upload_file_id", request.getUploadFileId());
        cvItem.put("type", "document");
        
        inputs.put("cv", new Object[]{cvItem});
        inputs.put("jd", request.getJd());

        requestBody.put("inputs", inputs);
        requestBody.put("query", request.getQuery());
        requestBody.put("response_mode", "blocking");
        requestBody.put("conversation_id", request.getConversationId());
        requestBody.put("user", request.getUser());

        log.info("构建的请求体: {}", JSON.toJSONString(requestBody));
        return requestBody;
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        result.put("message", "简历筛选和打分服务正常运行");
        result.put("chat_messages_api", DifyApiConstants.getChatMessagesUrl());
        result.put("workflow_type", WorkflowType.RESUME_SCORING.getDescription());
        return ResponseEntity.ok(result);
    }

    /**
     * 获取配置信息
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取配置信息成功");
            
            Map<String, Object> config = new HashMap<>();
            config.put("chatMessagesUrl", DifyApiConstants.getChatMessagesUrl());
            config.put("workflowType", WorkflowType.RESUME_SCORING.getCode());
            config.put("description", WorkflowType.RESUME_SCORING.getDescription());
            config.put("timeout", DifyApiConstants.TIMEOUT);
            config.put("retryCount", DifyApiConstants.RETRY_COUNT);
            
            result.put("data", config);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("获取配置信息失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取配置信息失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}
