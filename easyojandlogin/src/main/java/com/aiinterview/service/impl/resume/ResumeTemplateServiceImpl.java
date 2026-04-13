package com.aiinterview.service.impl.resume;

import com.aiinterview.mapper.ResumeTemplateMapper;
import com.aiinterview.model.entity.resume.ResumeTemplate;
import com.aiinterview.service.resume.ResumeTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 简历模板服务实现
 */
@Service
public class ResumeTemplateServiceImpl extends ServiceImpl<ResumeTemplateMapper, ResumeTemplate> 
        implements ResumeTemplateService {
    
    @Override
    public List<ResumeTemplate> getAllTemplates() {
        QueryWrapper<ResumeTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("template_id");
        return list(queryWrapper);
    }
    
    @Override
    public List<ResumeTemplate> getTemplatesByCategory(String category) {
        QueryWrapper<ResumeTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", category);
        queryWrapper.orderByAsc("template_id");
        return list(queryWrapper);
    }
    
    @Override
    public ResumeTemplate getTemplateById(Integer templateId) {
        return getById(templateId);
    }
}