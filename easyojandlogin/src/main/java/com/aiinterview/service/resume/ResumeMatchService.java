package com.aiinterview.service.resume;

import com.aiinterview.model.dto.resume.ResumeMatchRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 简历匹配分析服务接口
 */
public interface ResumeMatchService {
    
    /**
     * 分析简历与职位的匹配度
     * @param request 简历匹配请求
     * @return 匹配分析结果
     */
    Mono<Map<String, Object>> analyzeResumeMatch(ResumeMatchRequest request);
}
