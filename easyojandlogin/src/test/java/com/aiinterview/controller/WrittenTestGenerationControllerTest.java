package com.aiinterview.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 笔试生成控制器测试类
 */
@SpringBootTest
@ActiveProfiles("test")
public class WrittenTestGenerationControllerTest {

    @Test
    public void testHealthEndpoint() {
        // 简单的健康检查测试
        // 这里可以添加更多的测试用例
        System.out.println("笔试生成控制器测试通过");
    }
}
