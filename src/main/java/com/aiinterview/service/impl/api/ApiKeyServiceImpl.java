package com.aiinterview.service.impl.api;

import com.aiinterview.config.ApiSecurityConfig;
import com.aiinterview.service.api.ApiKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * API密钥服务实现类
 */
@Slf4j
@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    @Autowired
    private ApiSecurityConfig apiSecurityConfig;

    @Override
    public boolean validateApiKey(String authorizationHeader) {
        if (!apiSecurityConfig.isEnabled()) {
            log.debug("API密钥验证已禁用");
            return true;
        }

        if (authorizationHeader == null) {
            log.warn("缺少Authorization头");
            return false;
        }

        String apiKey = extractApiKey(authorizationHeader);
        if (apiKey == null) {
            log.warn("无法从Authorization头中提取API密钥");
            return false;
        }

        boolean isValid = apiSecurityConfig.isValidApiKey(apiKey);
        if (!isValid) {
            log.warn("无效的API密钥: {}", apiKey);
        } else {
            log.debug("API密钥验证成功: {}", apiKey);
        }

        return isValid;
    }

    @Override
    public String extractApiKey(String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }

        // 支持 "Bearer token" 格式
        if (authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        // 支持直接传递token
        return authorizationHeader;
    }

    @Override
    public String getDefaultApiKey() {
        return apiSecurityConfig.getDefaultApiKey();
    }
}
