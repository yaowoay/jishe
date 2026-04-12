package com.aiinterview.service.api;

import java.util.Map;

/**
 * Dify服务接口
 */
public interface DifyService {
    
    /**
     * 运行工作流
     * @param workflowName 工作流名称
     * @param inputs 输入参数
     * @return 工作流响应
     */
    String runWorkflow(String workflowName, Map<String, Object> inputs);
}
