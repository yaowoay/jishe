package com.aiinterview.controller.resume;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.model.entity.resume.ResumeTemplate;
import com.aiinterview.service.resume.ResumeTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 简历模板控制器
 */
@RestController
@RequestMapping("/resume")
@Slf4j
@CrossOrigin(origins = "*")
public class ResumeTemplateController {
    
    @Resource
    private ResumeTemplateService resumeTemplateService;
    
    @GetMapping("/templates")
    public BaseResponse<List<ResumeTemplate>> getAllTemplates() {
        try {
            List<ResumeTemplate> templates = resumeTemplateService.getAllTemplates();
            return BaseResponse.success(templates, "获取模板列表成功");
        } catch (Exception e) {
            log.error("获取模板列表失败", e);
            return BaseResponse.error(500, "获取模板列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/templates/category/{category}")
    public BaseResponse<List<ResumeTemplate>> getTemplatesByCategory(@PathVariable String category) {
        try {
            List<ResumeTemplate> templates = resumeTemplateService.getTemplatesByCategory(category);
            return BaseResponse.success(templates, "获取分类模板成功");
        } catch (Exception e) {
            log.error("获取分类模板失败", e);
            return BaseResponse.error(500, "获取分类模板失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/templates/{templateId}")
    public BaseResponse<ResumeTemplate> getTemplateById(@PathVariable Integer templateId) {
        try {
            ResumeTemplate template = resumeTemplateService.getTemplateById(templateId);
            if (template == null) {
                return BaseResponse.error(404, "模板不存在");
            }
            return BaseResponse.success(template, "获取模板详情成功");
        } catch (Exception e) {
            log.error("获取模板详情失败", e);
            return BaseResponse.error(500, "获取模板详情失败: " + e.getMessage());
        }
    }
}