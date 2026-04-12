package com.aiinterview.constants;

/**
 * Dify API 相关常量
 * 集中管理所有 Dify API 相关的配置常量
 */
public final class DifyApiConstants {

    private DifyApiConstants() {
        // 私有构造函数，防止实例化
    }

    // ========== API 基础配置 ==========

    // ========== Dify API 基础配置 ==========

    /**
     * Dify API 基础URL
     * 您可以修改为您的Dify实例地址，例如：
     * - 官方API: "https://api.dify.ai/v1"
     * - 自部署: "http://your-dify-server.com/v1"
     */
    public static final String BASE_URL = "http://81.70.20.30/v1";
    public static final String DIFY_BASE_URL = BASE_URL; // 保持向后兼容

    /**
     * 文件上传URL
     */
    public static final String DIFY_FILE_UPLOAD_URL = BASE_URL + "/files/upload";

    /**
     * 工作流运行URL
     */
    public static final String DIFY_WORKFLOW_RUN_URL = BASE_URL + "/workflows/run";

    // ========== 工作流配置 ==========

    /**
     * 简历分析工作流配置
     */
    public static final String RESUME_WORKFLOW_ID = "2e4bceb4-afd4-449e-ba8d-e4f2cd3efbee";
    public static final String RESUME_API_KEY = "app-iXQ9Euj9CEboby4yWxMBIYq8";

    /**
     * 职位描述生成工作流配置
     */
    public static final String JOB_DESCRIPTION_WORKFLOW_ID = "94119a23-b710-427b-9c5f-89e20b9190f1";
    public static final String JOB_DESCRIPTION_API_KEY = "app-USMtIt9GfjUMvGZGjQge3u7t";

    /**
     * 简历筛选和打分工作流配置
     */
    public static final String RESUME_SCORING_APP_ID = "your-resume-scoring-app-id";
    public static final String RESUME_SCORING_API_KEY = "app-OOVE1rL1PRHrzfPteGbKjgoB";

    /**
     * 笔试生成工作流配置
     * 例如：
     * WRITTEN_TEST_WORKFLOW_ID = "wf-12345678-abcd-efgh-ijkl-123456789012";
     * WRITTEN_TEST_API_KEY = "app-AbCdEfGhIjKlMnOpQrStUvWxYz123456";
     */
    public static final String WRITTEN_TEST_WORKFLOW_ID = "wf-temp-written-test-workflow";
    public static final String WRITTEN_TEST_API_KEY = "app-9FTcCqotNKuIKVjYhmU9pK0p";

    /**
     * 面试生成工作流配置
     * 例如：
     * INTERVIEW_WORKFLOW_ID = "wf-87654321-dcba-hgfe-lkji-210987654321";
     * INTERVIEW_API_KEY = "app-ZyXwVuTsRqPoNmLkJiHgFeDcBa987654";
     */
    public static final String INTERVIEW_WORKFLOW_ID = "wf-temp-interview-workflow";
    public static final String INTERVIEW_API_KEY = "app-VOCwpcLyOg6ctvSAl6UX6Is6";

    /**
     * 面试评估与资源推荐工作流配置
     * 对应Dify中的"评估与推荐.yml"工作流
     *
     * 重要提示：您需要在Dify平台上：
     * 1. 导入 src/main/resources/static/评估与推荐.yml 文件
     * 2. 获取生成的工作流ID（格式：wf-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx）
     * 3. 获取对应的API Key（格式：app-xxxxxxxxxxxxxxxxxxxxxxxx）
     * 4. 替换下面的配置
     */
    public static final String EVALUATION_RECOMMENDATION_WORKFLOW_ID = "wf-evaluation-recommendation-placeholder";
    public static final String EVALUATION_RECOMMENDATION_API_KEY = "app-jaRROtgNRfY8NElPmv6MbSrO";

    // ========== API 端点路径 ==========
    
    /**
     * 工作流运行端点
     */
    public static final String WORKFLOWS_RUN_ENDPOINT = "/workflows/run";

    /**
     * 聊天消息端点
     */
    public static final String CHAT_MESSAGES_ENDPOINT = "/chat-messages";

    /**
     * 文件上传端点
     */
    public static final String FILES_UPLOAD_ENDPOINT = "/files/upload";

    // ========== 请求配置 ==========
    
    /**
     * 请求超时时间（毫秒）
     */
    public static final int TIMEOUT = 60000;

    /**
     * 重试次数
     */
    public static final int RETRY_COUNT = 3;

    /**
     * 默认响应模式
     */
    public static final String DEFAULT_RESPONSE_MODE = "blocking";

    /**
     * 默认文档类型
     */
    public static final String DEFAULT_DOCUMENT_TYPE = "document";

    /**
     * 默认变量名称
     */
    public static final String DEFAULT_VARIABLE_NAME = "Resume_analysis";

    // ========== HTTP 头部 ==========
    
    /**
     * Authorization 头部前缀
     */
    public static final String BEARER_PREFIX = "Bearer ";

    // ========== 状态码 ==========
    
    /**
     * 工作流成功状态
     */
    public static final String STATUS_SUCCEEDED = "succeeded";

    /**
     * 工作流失败状态
     */
    public static final String STATUS_FAILED = "failed";

    /**
     * 工作流运行中状态
     */
    public static final String STATUS_RUNNING = "running";

    // ========== 错误消息 ==========
    
    /**
     * API 调用失败消息
     */
    public static final String ERROR_API_CALL_FAILED = "调用 Dify API 失败";

    /**
     * 工作流执行失败消息
     */
    public static final String ERROR_WORKFLOW_FAILED = "工作流执行失败";

    /**
     * 参数验证失败消息
     */
    public static final String ERROR_PARAMETER_VALIDATION = "参数验证失败";

    /**
     * 网络连接失败消息
     */
    public static final String ERROR_NETWORK_CONNECTION = "网络连接失败";

    // ========== 工具方法 ==========
    
    /**
     * 获取完整的工作流运行URL
     */
    public static String getWorkflowRunUrl() {
        return BASE_URL + WORKFLOWS_RUN_ENDPOINT;
    }

    /**
     * 获取完整的文件上传URL
     */
    public static String getFileUploadUrl() {
        return BASE_URL + FILES_UPLOAD_ENDPOINT;
    }

    /**
     * 获取完整的聊天消息URL
     */
    public static String getChatMessagesUrl() {
        return BASE_URL + CHAT_MESSAGES_ENDPOINT;
    }

    /**
     * 获取工作流状态查询URL
     */
    public static String getWorkflowStatusUrl(String workflowRunId) {
        return BASE_URL + WORKFLOWS_RUN_ENDPOINT + "/" + workflowRunId;
    }

    /**
     * 获取简历分析工作流的Authorization头部值
     */
    public static String getResumeAuthorizationHeader() {
        return BEARER_PREFIX + RESUME_API_KEY;
    }

    /**
     * 获取职位描述工作流的Authorization头部值
     */
    public static String getJobDescriptionAuthorizationHeader() {
        return BEARER_PREFIX + JOB_DESCRIPTION_API_KEY;
    }

    /**
     * 获取面试评估与资源推荐工作流的Authorization头部值
     */
    public static String getEvaluationRecommendationAuthorizationHeader() {
        return BEARER_PREFIX + EVALUATION_RECOMMENDATION_API_KEY;
    }

    /**
     * 根据工作流类型获取对应的API Key
     * @deprecated 建议使用 WorkflowType 枚举来管理工作流配置
     */
    @Deprecated
    public static String getApiKeyByWorkflowType(String workflowType) {
        switch (workflowType.toLowerCase()) {
            case "resume":
                return RESUME_API_KEY;
            case "job_description":
                return JOB_DESCRIPTION_API_KEY;
            default:
                throw new IllegalArgumentException("未知的工作流类型: " + workflowType);
        }
    }

    /**
     * 根据工作流类型获取对应的Authorization头部值
     * @deprecated 建议使用 WorkflowType 枚举来管理工作流配置
     */
    @Deprecated
    public static String getAuthorizationHeaderByWorkflowType(String workflowType) {
        return BEARER_PREFIX + getApiKeyByWorkflowType(workflowType);
    }

    /**
     * 检查工作流状态是否为成功
     */
    public static boolean isSuccessStatus(String status) {
        return STATUS_SUCCEEDED.equals(status);
    }

    /**
     * 检查工作流状态是否为失败
     */
    public static boolean isFailedStatus(String status) {
        return STATUS_FAILED.equals(status);
    }

    /**
     * 检查工作流状态是否为运行中
     */
    public static boolean isRunningStatus(String status) {
        return STATUS_RUNNING.equals(status);
    }
}
