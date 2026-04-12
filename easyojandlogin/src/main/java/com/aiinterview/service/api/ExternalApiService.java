package com.aiinterview.service.api;

import org.springframework.web.multipart.MultipartFile;

/**
 * 外部API服务接口
 */
public interface ExternalApiService {
    
    /**
     * 调用外部文件上传接口
     * @param file 文件
     * @param user 用户标识
     * @param apiKey API密钥
     * @return 上传响应
     */
    String uploadFileToExternal(MultipartFile file, String user, String apiKey);
    
    /**
     * 调用外部工作流接口
     * @param requestBody 请求体
     * @param apiKey API密钥
     * @return 工作流响应
     */
    String runWorkflowExternal(String requestBody, String apiKey);
}
