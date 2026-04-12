package com.aiinterview.model.dto.workflow;

import lombok.Data;
import java.util.Map;

/**
 * 工作流执行请求DTO
 */
@Data
public class WorkflowRunRequest {
    private Map<String, Object> inputs;
    private String responseMode;
    private String user;
}
