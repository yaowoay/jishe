//package com.aiinterview.utils;
//
//import com.aiinterview.constants.DifyApiConstants;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Dify API 配置管理工具类
// * 提供配置信息的查看和验证功能
// */
//@Slf4j
//@Component
//public class DifyApiConfigManager {
//
//    /**
//     * 获取所有配置信息
//     */
//    public Map<String, Object> getAllConfigurations() {
//        Map<String, Object> configs = new HashMap<>();
//
//        // API 基础配置
//        configs.put("baseUrl", DifyApiConstants.BASE_URL);
//
//        // 简历分析工作流配置
//        configs.put("resumeWorkflowId", DifyApiConstants.RESUME_WORKFLOW_ID);
//        configs.put("resumeApiKey", maskApiKey(DifyApiConstants.RESUME_API_KEY));
//
//        // 职位描述工作流配置
//        configs.put("jobDescriptionWorkflowId", DifyApiConstants.JOB_DESCRIPTION_WORKFLOW_ID);
//        configs.put("jobDescriptionApiKey", maskApiKey(DifyApiConstants.JOB_DESCRIPTION_API_KEY));
//
//        // 请求配置
//        configs.put("timeout", DifyApiConstants.TIMEOUT);
//        configs.put("retryCount", DifyApiConstants.RETRY_COUNT);
//        configs.put("defaultResponseMode", DifyApiConstants.DEFAULT_RESPONSE_MODE);
//        configs.put("defaultDocumentType", DifyApiConstants.DEFAULT_DOCUMENT_TYPE);
//        configs.put("defaultVariableName", DifyApiConstants.DEFAULT_VARIABLE_NAME);
//
//        return configs;
//    }
//
//    /**
//     * 验证配置是否完整
//     */
//    public boolean validateConfigurations() {
//        try {
//            // 检查必要的配置项
//            if (isBlank(DifyApiConstants.BASE_URL)) {
//                log.error("BASE_URL 配置为空");
//                return false;
//            }
//
//            if (isBlank(DifyApiConstants.RESUME_API_KEY)) {
//                log.error("RESUME_API_KEY 配置为空");
//                return false;
//            }
//
//            if (isBlank(DifyApiConstants.JOB_DESCRIPTION_API_KEY)) {
//                log.error("JOB_DESCRIPTION_API_KEY 配置为空");
//                return false;
//            }
//
//            if (isBlank(DifyApiConstants.RESUME_WORKFLOW_ID)) {
//                log.error("RESUME_WORKFLOW_ID 配置为空");
//                return false;
//            }
//
//            if (isBlank(DifyApiConstants.JOB_DESCRIPTION_WORKFLOW_ID)) {
//                log.error("JOB_DESCRIPTION_WORKFLOW_ID 配置为空");
//                return false;
//            }
//
//            // 检查数值配置
//            if (DifyApiConstants.TIMEOUT <= 0) {
//                log.error("TIMEOUT 配置无效: {}", DifyApiConstants.TIMEOUT);
//                return false;
//            }
//
//            if (DifyApiConstants.RETRY_COUNT < 0) {
//                log.error("RETRY_COUNT 配置无效: {}", DifyApiConstants.RETRY_COUNT);
//                return false;
//            }
//
//            // 检查URL格式
//            if (!DifyApiConstants.BASE_URL.startsWith("http")) {
//                log.error("BASE_URL 格式无效: {}", DifyApiConstants.BASE_URL);
//                return false;
//            }
//
//            log.info("配置验证通过");
//            return true;
//
//        } catch (Exception e) {
//            log.error("配置验证失败", e);
//            return false;
//        }
//    }
//
//    /**
//     * 获取配置摘要信息
//     */
//    public String getConfigurationSummary() {
//        StringBuilder summary = new StringBuilder();
//        summary.append("=== Dify API 配置摘要 ===\n");
//        summary.append("API 地址: ").append(DifyApiConstants.BASE_URL).append("\n");
//        summary.append("简历工作流:\n");
//        summary.append("  - ID: ").append(DifyApiConstants.RESUME_WORKFLOW_ID).append("\n");
//        summary.append("  - API Key: ").append(maskApiKey(DifyApiConstants.RESUME_API_KEY)).append("\n");
//        summary.append("职位描述工作流:\n");
//        summary.append("  - ID: ").append(DifyApiConstants.JOB_DESCRIPTION_WORKFLOW_ID).append("\n");
//        summary.append("  - API Key: ").append(maskApiKey(DifyApiConstants.JOB_DESCRIPTION_API_KEY)).append("\n");
//        summary.append("超时时间: ").append(DifyApiConstants.TIMEOUT).append("ms\n");
//        summary.append("重试次数: ").append(DifyApiConstants.RETRY_COUNT).append("\n");
//        summary.append("========================");
//        return summary.toString();
//    }
//
//    /**
//     * 测试 API 连接性（模拟）
//     */
//    public boolean testApiConnection() {
//        try {
//            // 这里可以添加实际的连接测试逻辑
//            // 比如发送一个简单的健康检查请求
//            log.info("测试 API 连接: {}", DifyApiConstants.BASE_URL);
//
//            // 模拟连接测试
//            if (DifyApiConstants.BASE_URL.startsWith("http")) {
//                log.info("API 连接测试通过");
//                return true;
//            } else {
//                log.error("API 连接测试失败: URL 格式无效");
//                return false;
//            }
//
//        } catch (Exception e) {
//            log.error("API 连接测试失败", e);
//            return false;
//        }
//    }
//
//    /**
//     * 获取运行时统计信息
//     */
//    public Map<String, Object> getRuntimeStats() {
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("configurationValid", validateConfigurations());
//        stats.put("apiConnectionOk", testApiConnection());
//        stats.put("lastCheckTime", System.currentTimeMillis());
//        return stats;
//    }
//
//    /**
//     * 打印配置信息到日志
//     */
//    public void logConfigurations() {
//        log.info("当前 Dify API 配置:");
//        log.info("  BASE_URL: {}", DifyApiConstants.BASE_URL);
//        log.info("  简历分析工作流:");
//        log.info("    RESUME_WORKFLOW_ID: {}", DifyApiConstants.RESUME_WORKFLOW_ID);
//        log.info("    RESUME_API_KEY: {}", maskApiKey(DifyApiConstants.RESUME_API_KEY));
//        log.info("  职位描述工作流:");
//        log.info("    JOB_DESCRIPTION_WORKFLOW_ID: {}", DifyApiConstants.JOB_DESCRIPTION_WORKFLOW_ID);
//        log.info("    JOB_DESCRIPTION_API_KEY: {}", maskApiKey(DifyApiConstants.JOB_DESCRIPTION_API_KEY));
//        log.info("  TIMEOUT: {}ms", DifyApiConstants.TIMEOUT);
//        log.info("  RETRY_COUNT: {}", DifyApiConstants.RETRY_COUNT);
//        log.info("  DEFAULT_RESPONSE_MODE: {}", DifyApiConstants.DEFAULT_RESPONSE_MODE);
//    }
//
//    /**
//     * 掩码 API 密钥，只显示前后几位
//     */
//    private String maskApiKey(String apiKey) {
//        if (isBlank(apiKey)) {
//            return "未配置";
//        }
//
//        if (apiKey.length() <= 8) {
//            return "***";
//        }
//
//        return apiKey.substring(0, 4) + "***" + apiKey.substring(apiKey.length() - 4);
//    }
//
//    /**
//     * 检查字符串是否为空或空白
//     */
//    private boolean isBlank(String str) {
//        return str == null || str.trim().isEmpty();
//    }
//}
