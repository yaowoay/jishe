package com.aiinterview.service.resume;

import com.aiinterview.model.dto.resume.ResumeGenerationRequest;
import com.aiinterview.model.dto.resume.ResumeGenerationResponse;

/**
 * 简历生成服务接口
 */
public interface ResumeGenerationService {
    
    /**
     * 生成简历
     * @param request 简历生成请求
     * @return 生成的简历内容
     */
    ResumeGenerationResponse generateResume(ResumeGenerationRequest request);
    
    /**
     * 根据模板和个人信息生成简历
     * @param template 简历模板
     * @param personalInfo 个人信息
     * @return 生成的简历内容
     */
    ResumeGenerationResponse generateResumeWithTemplate(String template, String personalInfo);
    
    /**
     * 验证API配置是否正确
     * @return 验证结果
     */
    boolean validateApiConfig();
}
