package com.coldwind.easyoj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目经验实体类
 */
@Data
@TableName("project_experience")
public class ProjectExperience {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 简历ID
     */
    private Long resumeId;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 担任角色
     */
    private String role;
    
    /**
     * 开始时间
     */
    private LocalDate startDate;
    
    /**
     * 结束时间
     */
    private LocalDate endDate;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 