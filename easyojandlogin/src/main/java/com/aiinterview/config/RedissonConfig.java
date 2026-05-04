package com.aiinterview.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 * 优化内存使用和连接管理
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        // 单机模式配置
        String address = "redis://" + redisHost + ":" + redisPort;
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(database)
                .setConnectionMinimumIdleSize(1)  // 最小空闲连接数
                .setConnectionPoolSize(4)        // 减少连接池大小
                .setIdleConnectionTimeout(10000) // 空闲连接超时
                .setConnectTimeout(10000)        // 连接超时
                .setTimeout(5000)                // 增加命令超时
                .setRetryAttempts(2)             // 减少重试次数
                .setRetryInterval(2000);         // 增加重试间隔

        // 设置密码（如果有）
        if (redisPassword != null && !redisPassword.trim().isEmpty()) {
            config.useSingleServer().setPassword(redisPassword);
        }

        // 优化内存使用
        config.setNettyThreads(2);              // 减少 Netty 线程数
        config.setThreads(2);                   // 减少业务线程数

        // 编解码器配置
        config.setCodec(new org.redisson.codec.JsonJacksonCodec());

        return Redisson.create(config);
    }
}
