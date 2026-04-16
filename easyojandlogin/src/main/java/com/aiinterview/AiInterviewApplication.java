package com.aiinterview;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI面试系统启动类
 */
@SpringBootApplication
@MapperScan({"com.aiinterview.mapper", "com.aiinterview.repository"})
public class AiInterviewApplication {


    public static void main(String[] args) {
        SpringApplication.run(AiInterviewApplication.class, args);

    }
}
