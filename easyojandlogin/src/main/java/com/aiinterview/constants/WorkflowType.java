package com.aiinterview.constants;

/**
 * 工作流类型枚举
 * 定义不同的工作流类型及其对应的配置
 */
public enum WorkflowType {

    /**
     * 简历分析工作流
     */
    RESUME("resume", "简历分析", 
           DifyApiConstants.RESUME_WORKFLOW_ID, 
           DifyApiConstants.RESUME_API_KEY),

    /**
     * 职位描述生成工作流
     */
    JOB_DESCRIPTION("job_description", "职位描述生成",
                    DifyApiConstants.JOB_DESCRIPTION_WORKFLOW_ID,
                    DifyApiConstants.JOB_DESCRIPTION_API_KEY),

    /**
     * 简历筛选和打分工作流
     */
    RESUME_SCORING("resume_scoring", "简历筛选和打分",
                   DifyApiConstants.RESUME_SCORING_APP_ID,
                   DifyApiConstants.RESUME_SCORING_API_KEY),

    /**
     * 笔试生成工作流
     */
    WRITTEN_TEST("written_test", "笔试生成",
                 DifyApiConstants.WRITTEN_TEST_WORKFLOW_ID,
                 DifyApiConstants.WRITTEN_TEST_API_KEY),

    /**
     * 面试生成工作流
     */
    INTERVIEW("interview", "面试生成",
              DifyApiConstants.INTERVIEW_WORKFLOW_ID,
              DifyApiConstants.INTERVIEW_API_KEY),

    /**
     * 面试评估与资源推荐工作流
     */
    EVALUATION_RECOMMENDATION("evaluation_recommendation", "面试评估与资源推荐",
                             DifyApiConstants.EVALUATION_RECOMMENDATION_WORKFLOW_ID,
                             DifyApiConstants.EVALUATION_RECOMMENDATION_API_KEY);

    private final String code;
    private final String description;
    private final String workflowId;
    private final String apiKey;

    WorkflowType(String code, String description, String workflowId, String apiKey) {
        this.code = code;
        this.description = description;
        this.workflowId = workflowId;
        this.apiKey = apiKey;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getApiKey() {
        return apiKey;
    }

    /**
     * 获取Authorization头部值
     */
    public String getAuthorizationHeader() {
        return DifyApiConstants.BEARER_PREFIX + apiKey;
    }

    /**
     * 根据代码获取工作流类型
     */
    public static WorkflowType fromCode(String code) {
        for (WorkflowType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的工作流类型代码: " + code);
    }

    /**
     * 根据工作流ID获取工作流类型
     */
    public static WorkflowType fromWorkflowId(String workflowId) {
        for (WorkflowType type : values()) {
            if (type.workflowId.equals(workflowId)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的工作流ID: " + workflowId);
    }

    /**
     * 检查代码是否有效
     */
    public static boolean isValidCode(String code) {
        try {
            fromCode(code);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 检查工作流ID是否有效
     */
    public static boolean isValidWorkflowId(String workflowId) {
        try {
            fromWorkflowId(workflowId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("WorkflowType{code='%s', description='%s', workflowId='%s'}", 
                           code, description, workflowId);
    }
}
