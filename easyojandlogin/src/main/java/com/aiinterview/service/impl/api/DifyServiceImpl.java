package com.aiinterview.service.impl.api;

import com.aiinterview.service.api.DifyService;
import com.aiinterview.service.api.ExternalApiService;
import com.aiinterview.config.DifyApiConfig;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Dify服务实现类
 */
@Slf4j
@Service
public class DifyServiceImpl implements DifyService {

    @Autowired
    private ExternalApiService externalApiService;

    @Autowired
    private DifyApiConfig difyApiConfig;

    @Override
    public String runWorkflow(String workflowName, Map<String, Object> inputs) {
        try {
            log.info("运行Dify工作流: {}, 输入参数: {}", workflowName, inputs);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", inputs);
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", "system");

            String requestJson = JSON.toJSONString(requestBody);
            log.info("Dify工作流请求: {}", requestJson);

            // 根据工作流名称获取对应的API Key
            String apiKey = getApiKeyByWorkflowName(workflowName);
            
            // 调用外部API服务
            String response = externalApiService.runWorkflowExternal(requestJson, apiKey);
            log.info("Dify工作流响应: {}", response);

            return response;
        } catch (Exception e) {
            log.error("运行Dify工作流失败: workflowName={}, error={}", workflowName, e.getMessage(), e);
            throw new RuntimeException("运行Dify工作流失败: " + e.getMessage());
        }
    }

    /**
     * 根据工作流名称获取对应的API Key
     */
    private String getApiKeyByWorkflowName(String workflowName) {
        try {
            log.info("获取工作流API Key，工作流名称: {}", workflowName);

            if ("面试".equals(workflowName)) {
                // 面试工作流使用专门的面试API Key
                return difyApiConfig.getApiKeyByCode("interview");
            } else if ("笔试".equals(workflowName)) {
                // 笔试工作流使用专门的笔试API Key
                return difyApiConfig.getApiKeyByCode("written_test");
            } else if ("evaluation_recommendation".equals(workflowName)) {
                // 面试评估与资源推荐工作流
                log.info("使用评估推荐工作流API Key");
                return difyApiConfig.getApiKeyByCode("evaluation_recommendation");
            } else {
                // 默认使用简历分析的API Key
                log.info("使用默认简历分析工作流API Key");
                return difyApiConfig.getApiKeyByCode("resume");
            }
        } catch (Exception e) {
            log.warn("获取API Key失败，使用默认值: {}", e.getMessage());
            // 如果获取失败，尝试使用简历分析的API Key作为备用
            try {
                return difyApiConfig.getApiKeyByCode("resume");
            } catch (Exception ex) {
                log.error("获取备用API Key也失败: {}", ex.getMessage());
                return "app-default-key"; // 最后的默认值
            }
        }
    }
}
