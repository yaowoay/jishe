package com.aiinterview.service.resume;

import com.aiinterview.model.entity.resume.ResumeTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 简历模板服务接口
 */
public interface ResumeTemplateService extends IService<ResumeTemplate> {
    
    /**
     * 获取所有可用的模板
     */
    List<ResumeTemplate> getAllTemplates();
    
    /**
     * 根据分类获取模板
     */
    List<ResumeTemplate> getTemplatesByCategory(String category);
    
    /**
     * 根据ID获取模板
     */
    ResumeTemplate getTemplateById(Integer templateId);
}