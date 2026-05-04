package com.aiinterview.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 技能专长实体类
 */
@Data
@TableName("skill")
public class Skill {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 简历ID
     */
    private Long resumeId;
    
    /**
     * 技能名称
     */
    private String skillName;
    
    /**
     * 熟练度(1-5星)
     */
    private Integer proficiency;
    
    /**
     * 技能描述
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