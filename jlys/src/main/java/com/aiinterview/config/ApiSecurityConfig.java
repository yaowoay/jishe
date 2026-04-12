package com.aiinterview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * API安全配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "api.security")
public class ApiSecurityConfig {
    
    /**
     * 是否启用API密钥验证
     */
    private boolean enabled = true;
    
    /**
     * API密钥列表
     */
    private List<ApiKey> keys;
    
    /**
     * 默认API密钥
     */
    private String defaultKey;
    
    /**
     * API密钥配置
     */
    @Data
    public static class ApiKey {
        private String key;
        private String description;
    }
    
    /**
     * 验证API密钥是否有效
     */
    public boolean isValidApiKey(String apiKey) {
        if (!enabled) {
            return true; // 如果未启用验证，则总是返回true
        }
        
        if (apiKey == null || keys == null) {
            return false;
        }
        
        return keys.stream()
                .anyMatch(k -> apiKey.equals(k.getKey()));
    }
    
    /**
     * 获取默认API密钥
     */
    public String getDefaultApiKey() {
        return defaultKey != null ? defaultKey : 
               (keys != null && !keys.isEmpty() ? keys.get(0).getKey() : null);
    }
}
