// com/aiinterview/config/EmploymentWarningConfig.java
package com.aiinterview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "employment.warning.python.service")
public class EmploymentWarningConfig {
    private String url = "http://localhost:5001";
    private int timeout = 30000;
    private int retryCount = 2;
    private int retryDelay = 3000;
}