package com.aiinterview.model.entity.resume;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 简历模板实体类
 */
@Data
@TableName("resume_templates")
public class ResumeTemplate {
    
    @TableId(type = IdType.AUTO)
    private Integer templateId;
    
    private String templateName;
    
    private String templateType;
    
    private String templatePath;
    
    private String previewImage;
    
    private String description;
    
    private String category;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}