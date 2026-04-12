package com.aiinterview.controller.workflow.jobdescription;

import com.aiinterview.config.DifyApiConfig;
import com.aiinterview.model.dto.workflow.JD.JobDescriptionRequest;
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
 * 职位描述生成控制器
 * 调用 Dify 工作流生成详细的职位描述
 */
@Slf4j
@RestController
@RequestMapping("/job-description")
@CrossOrigin(origins = "*")
@Validated
public class JobDescriptionController {

    @Autowired
    private DifyApiConfig difyApiConfig;

    private final RestTemplate restTemplate;

    public JobDescriptionController() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 生成职位描述接口（支持表单参数）
     * 调用 Dify 工作流生成详细的职位描述
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateJobDescription(
            @RequestParam("job") String job,
            @RequestParam("jobType") String jobType,
            @RequestParam("salary") String salary,
            @RequestParam("education") String education,
            @RequestParam("experience") String experience,
            @RequestParam("place") String place,
            @RequestParam("description") String description,
            @RequestParam("requirement") String requirement,
            @RequestParam("user") String user) {

        log.info("开始生成职位描述: 职位={}, 类型={}, 用户={}", job, jobType, user);

        return generateJobDescriptionInternal(job, jobType, salary, education, experience, place, description, requirement, user);
    }

    /**
     * 生成职位描述接口（支持JSON请求体）
     * 调用 Dify 工作流生成详细的职位描述
     */
    @PostMapping("/generate-json")
    public ResponseEntity<Map<String, Object>> generateJobDescriptionJson(
            @Valid @RequestBody JobDescriptionRequest request) {

        log.info("开始生成职位描述(JSON): 职位={}, 类型={}, 用户={}", request.getJob(), request.getJobType(), request.getUser());

        return generateJobDescriptionInternal(
                request.getJob(),
                request.getJobType(),
                request.getSalary(),
                request.getEducation(),
                request.getExperience(),
                request.getPlace(),
                request.getDescription(),
                request.getRequirement(),
                request.getUser()
        );
    }

    /**
     * 内部方法：生成职位描述的核心逻辑
     */
    private ResponseEntity<Map<String, Object>> generateJobDescriptionInternal(
            String job, String jobType, String salary, String education,
            String experience, String place, String description, String requirement, String user) {

        try {
            String workflowUrl = difyApiConfig.getWorkflowRunUrl();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", difyApiConfig.getAuthorizationHeader(WorkflowType.JOB_DESCRIPTION));

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();

            // 构建inputs - 根据工作流的输入参数设置
            Map<String, Object> inputs = new HashMap<>();
            inputs.put("job", job);
            inputs.put("jobType", jobType);
            inputs.put("salary", salary);
            inputs.put("education", education);
            inputs.put("experience", experience);
            inputs.put("place", place);
            inputs.put("description", description);
            inputs.put("requirement", requirement);

            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", "blocking"); // 使用阻塞模式获取完整响应
            requestBody.put("user", user);

            String requestBodyJson = JSON.toJSONString(requestBody);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, headers);

            log.info("调用职位描述工作流接口: {}", workflowUrl);
            log.info("请求体: {}", requestBodyJson);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(workflowUrl, requestEntity, String.class);

            log.info("职位描述工作流API响应状态: {}", response.getStatusCode());
            log.info("职位描述工作流API响应内容: {}", response.getBody());

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                if (responseBody != null && (responseBody.startsWith("{") || responseBody.startsWith("["))) {
                    JSONObject responseJson = JSON.parseObject(responseBody);

                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "职位描述生成成功");
                    result.put("data", responseJson);

                    return ResponseEntity.ok(result);
                } else {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "职位描述生成响应不是有效的JSON格式");
                    result.put("raw_response", responseBody);
                    return ResponseEntity.ok(result);
                }
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "职位描述生成失败，状态码: " + response.getStatusCode());
                result.put("raw_response", response.getBody());
                return ResponseEntity.ok(result);
            }

        } catch (Exception e) {
            log.error("调用职位描述工作流API失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "调用职位描述工作流API失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        result.put("message", "职位描述生成服务正常运行");
        result.put("external_api", difyApiConfig.getBaseUrl());
        result.put("workflow_id", difyApiConfig.getJobDescriptionWorkflowId());
        return ResponseEntity.ok(result);
    }

    /**
     * 获取工作流状态接口
     * 用于查询特定工作流运行的状态
     */
    @GetMapping("/status/{workflowRunId}")
    public ResponseEntity<Map<String, Object>> getWorkflowStatus(
            @PathVariable("workflowRunId") String workflowRunId,
            @RequestParam("user") String user) {

        log.info("查询工作流状态: 运行ID={}, 用户={}", workflowRunId, user);

        try {
            String statusUrl = difyApiConfig.getWorkflowStatusUrl(workflowRunId);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", difyApiConfig.getAuthorizationHeader(WorkflowType.JOB_DESCRIPTION));

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            log.info("调用工作流状态查询接口: {}", statusUrl);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(statusUrl, HttpMethod.GET, requestEntity, String.class);

            log.info("工作流状态查询API响应状态: {}", response.getStatusCode());
            log.info("工作流状态查询API响应内容: {}", response.getBody());

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                if (responseBody != null && (responseBody.startsWith("{") || responseBody.startsWith("["))) {
                    JSONObject responseJson = JSON.parseObject(responseBody);

                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "工作流状态查询成功");
                    result.put("data", responseJson);

                    return ResponseEntity.ok(result);
                } else {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "工作流状态查询响应不是有效的JSON格式");
                    result.put("raw_response", responseBody);
                    return ResponseEntity.ok(result);
                }
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "工作流状态查询失败，状态码: " + response.getStatusCode());
                result.put("raw_response", response.getBody());
                return ResponseEntity.ok(result);
            }

        } catch (Exception e) {
            log.error("查询工作流状态失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "查询工作流状态失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}
