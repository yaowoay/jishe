package com.aiinterview.config;

import org.springframework.context.annotation.Configuration;

/**
 * API文档配置 - 暂时禁用以解决兼容性问题
 * 如需启用API文档，请确保SpringFox依赖完全清理后再配置SpringDoc
 */
@Configuration
public class SwaggerConfig {

    // 暂时注释掉OpenAPI配置，避免与SpringFox冲突
    // 等系统稳定运行后再启用

    /*
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI面试系统API文档")
                        .description("AI面试系统后端API接口文档，包含视频分析功能")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AI面试系统开发团队")
                                .email(""))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
    */
}
