package com.aiinterview.config;

import com.aiinterview.constants.DifyApiConstants;
import com.aiinterview.constants.WorkflowType;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * Dify API 配置类
 * 所有配置直接在代码中定义，不依赖外部配置文件
 * 使用常量类来管理配置值
 */
@Data
@Configuration
public class DifyApiConfig {

    /**
     * Dify API 基础URL
     */
    private final String baseUrl = DifyApiConstants.DIFY_BASE_URL;

    /**
     * 文件上传URL
     */
    private final String fileUploadUrl = DifyApiConstants.DIFY_FILE_UPLOAD_URL;

    /**
     * 工作流运行URL
     */
    private final String workflowRunUrl = DifyApiConstants.DIFY_WORKFLOW_RUN_URL;

    /**
     * 简历分析工作流配置
     */
    private final String resumeWorkflowId = DifyApiConstants.RESUME_WORKFLOW_ID;
    private final String resumeApiKey = DifyApiConstants.RESUME_API_KEY;

    /**
     * 职位描述生成工作流配置
     */
    private final String jobDescriptionWorkflowId = DifyApiConstants.JOB_DESCRIPTION_WORKFLOW_ID;
    private final String jobDescriptionApiKey = DifyApiConstants.JOB_DESCRIPTION_API_KEY;

    /**
     * 请求超时时间（毫秒）
     */
    private final int timeout = DifyApiConstants.TIMEOUT;

    /**
     * 重试次数
     */
    private final int retryCount = DifyApiConstants.RETRY_COUNT;

    /**
     * 是否启用调试模式
     */
    private final boolean debug = false;

    /**
     * 获取完整的工作流运行URL
     */
    public String getWorkflowRunUrl() {
        return workflowRunUrl;
    }

    /**
     * 获取文件上传URL
     */
    public String getFileUploadUrl() {
        return fileUploadUrl;
    }

    /**
     * 获取工作流状态查询URL
     */
    public String getWorkflowStatusUrl(String workflowRunId) {
        return DifyApiConstants.getWorkflowStatusUrl(workflowRunId);
    }

    /**
     * 获取简历分析工作流的Authorization头部值
     */
    public String getResumeAuthorizationHeader() {
        return DifyApiConstants.getResumeAuthorizationHeader();
    }

    /**
     * 获取职位描述工作流的Authorization头部值
     */
    public String getJobDescriptionAuthorizationHeader() {
        return DifyApiConstants.getJobDescriptionAuthorizationHeader();
    }

    /**
     * 根据工作流类型获取对应的Authorization头部值
     * @deprecated 建议使用 WorkflowType 枚举来管理工作流配置
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public String getAuthorizationHeaderByWorkflowType(String workflowType) {
        return DifyApiConstants.getAuthorizationHeaderByWorkflowType(workflowType);
    }

    /**
     * 根据工作流类型获取对应的API Key
     * @deprecated 建议使用 WorkflowType 枚举来管理工作流配置
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public String getApiKeyByWorkflowType(String workflowType) {
        return DifyApiConstants.getApiKeyByWorkflowType(workflowType);
    }

    /**
     * 检查工作流状态是否为成功
     */
    public boolean isSuccessStatus(String status) {
        return DifyApiConstants.isSuccessStatus(status);
    }

    /**
     * 检查工作流状态是否为失败
     */
    public boolean isFailedStatus(String status) {
        return DifyApiConstants.isFailedStatus(status);
    }

    /**
     * 检查工作流状态是否为运行中
     */
    public boolean isRunningStatus(String status) {
        return DifyApiConstants.isRunningStatus(status);
    }

    // ========== 新的基于 WorkflowType 枚举的方法 ==========

    /**
     * 根据工作流类型枚举获取Authorization头部值
     */
    public String getAuthorizationHeader(WorkflowType workflowType) {
        return workflowType.getAuthorizationHeader();
    }

    /**
     * 根据工作流类型枚举获取API Key
     */
    public String getApiKey(WorkflowType workflowType) {
        return workflowType.getApiKey();
    }

    /**
     * 根据工作流类型枚举获取工作流ID
     */
    public String getWorkflowId(WorkflowType workflowType) {
        return workflowType.getWorkflowId();
    }

    /**
     * 根据工作流代码获取Authorization头部值
     */
    public String getAuthorizationHeaderByCode(String code) {
        WorkflowType workflowType = WorkflowType.fromCode(code);
        return workflowType.getAuthorizationHeader();
    }

    /**
     * 根据工作流代码获取API Key
     */
    public String getApiKeyByCode(String code) {
        WorkflowType workflowType = WorkflowType.fromCode(code);
        return workflowType.getApiKey();
    }

    /**
     * 验证工作流类型是否有效
     */
    public boolean isValidWorkflowType(String code) {
        return WorkflowType.isValidCode(code);
    }
}
