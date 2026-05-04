package com.aiinterview.model.dto.workflow;

import lombok.Data;

/**
 * 工作流执行响应DTO
 */
@Data
public class WorkflowRunResponse {
    private String workflowRunId;
    private String taskId;
    private String status;
    private Object data;
    private Boolean success;
    private String message;

    public static WorkflowRunResponse success(String workflowRunId, String taskId, String status, Object data) {
        WorkflowRunResponse response = new WorkflowRunResponse();
        response.setWorkflowRunId(workflowRunId);
        response.setTaskId(taskId);
        response.setStatus(status);
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    public static WorkflowRunResponse error(String message) {
        WorkflowRunResponse response = new WorkflowRunResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
