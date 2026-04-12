package com.aiinterview.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * SpringFox禁用配置
 * 通过配置属性来控制是否启用SpringFox
 */
@Configuration
@ConditionalOnProperty(name = "springfox.documentation.enabled", havingValue = "false", matchIfMissing = true)
public class SpringFoxDisableConfig {
    
    // 这个配置类的存在就是为了通过条件注解来禁用SpringFox
    // 当springfox.documentation.enabled=false或者该属性不存在时，这个配置生效
    // 从而避免SpringFox相关的自动配置启动
    
}
