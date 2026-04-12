package com.aiinterview.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教育经历实体类
 */
@Data
@TableName("education")
public class Education {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 简历ID
     */
    private Long resumeId;
    
    /**
     * 学校名称
     */
    private String school;
    
    /**
     * 专业
     */
    private String major;
    
    /**
     * 学历
     */
    private String degree;
    
    /**
     * 开始时间
     */
    private LocalDate startDate;
    
    /**
     * 结束时间
     */
    private LocalDate endDate;
    
    /**
     * 在校经历
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