package com.aiinterview.config;

import com.aiinterview.mapper.ReportMapper;
import com.aiinterview.mapper.ResumeScoresMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Mapper验证配置类
 * 用于验证Mapper是否正确注入
 */
@Slf4j
@Configuration
public class MapperValidationConfig implements CommandLineRunner {

    @Autowired(required = false)
    private ReportMapper reportMapper;
    
    @Autowired(required = false)
    private ResumeScoresMapper resumeScoresMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Mapper验证开始 ===");
        
        if (reportMapper != null) {
            log.info("✅ ReportMapper 注入成功");
        } else {
            log.error("❌ ReportMapper 注入失败");
        }
        
        if (resumeScoresMapper != null) {
            log.info("✅ ResumeScoresMapper 注入成功");
        } else {
            log.error("❌ ResumeScoresMapper 注入失败");
        }
        
        log.info("=== Mapper验证结束 ===");
    }
}
