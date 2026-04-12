package com.aiinterview.model.dto.resume;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 简历生成请求DTO
 */
@Data
public class ResumeGenerationRequest {
    
    /**
     * 简历生成要求/提示词
     */
    @NotBlank(message = "简历生成要求不能为空")
    private String prompt;
    
    /**
     * 个人基本信息
     */
    private String personalInfo;
    
    /**
     * 工作经验
     */
    private String workExperience;
    
    /**
     * 教育背景
     */
    private String education;
    
    /**
     * 技能特长
     */
    private String skills;
    
    /**
     * 项目经验
     */
    private String projects;
    
    /**
     * 期望职位
     */
    private String targetPosition;
    
    /**
     * 简历模板类型
     */
    private String templateType = "standard";
    
    /**
     * 简历风格
     */
    private String style = "professional";
}
