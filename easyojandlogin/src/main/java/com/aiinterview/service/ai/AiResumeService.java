package com.aiinterview.service.ai;

import com.aiinterview.model.dto.request.ResumeCreateRequest;

/**
 * AI简历生成服务接口
 */

public interface AiResumeService {
    
    /**
     * 根据用户信息生成简历
     * @param userInfo 用户基本信息（姓名、工作经验、技能等）
     * @return 生成的简历创建请求对象
     */
    ResumeCreateRequest generateResumeFromUserInfo(String userInfo);
    
    /**
     * 根据职位要求生成简历
     * @param jobTitle 职位名称
     * @param userInfo 用户基本信息
     * @return 生成的简历创建请求对象
     */
    ResumeCreateRequest generateResumeForJob(String jobTitle, String userInfo);
    
    /**
     * 优化现有简历
     * @param existingResume 现有简历内容
     * @return 优化后的简历创建请求对象
     */
    ResumeCreateRequest optimizeResume(String existingResume);
} 