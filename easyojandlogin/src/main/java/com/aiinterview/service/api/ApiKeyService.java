package com.aiinterview.service.api;

/**
 * API密钥服务接口
 */
public interface ApiKeyService {
    
    /**
     * 验证API密钥
     * @param authorizationHeader Authorization头信息
     * @return 是否有效
     */
    boolean validateApiKey(String authorizationHeader);
    
    /**
     * 从Authorization头中提取API密钥
     * @param authorizationHeader Authorization头信息
     * @return API密钥
     */
    String extractApiKey(String authorizationHeader);
    
    /**
     * 获取默认API密钥
     * @return 默认API密钥
     */
    String getDefaultApiKey();
}
