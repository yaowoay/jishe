package com.aiinterview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 讯飞AI配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "xunfei.ai")
public class XunfeiAIConfig {

    /**
     * 服务URL
     */
    private String hostUrl;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * API密钥
     */
    private String apiSecret;
    
    /**
     * 连接超时时间(秒)
     */
    private Integer connectTimeout = 30;
    
    /**
     * 读取超时时间(秒)
     */
    private Integer readTimeout = 600;
    
    /**
     * 连接池最大连接数
     */
    private Integer maxConnections = 100;
    
    /**
     * 连接池保持时间(分钟)
     */
    private Integer keepAliveDuration = 5;
}
