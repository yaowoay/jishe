package com.aiinterview.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 其他信息实体类
 */
@Data
@TableName("additional_info")
public class AdditionalInfo {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 简历ID
     */
    private Long resumeId;
    
    /**
     * 信息类型
     */
    private String type;
    
    /**
     * 名称
     */
    private String name;
    
    /**
     * 开始时间
     */
    private LocalDate startDate;
    
    /**
     * 结束时间
     */
    private LocalDate endDate;
    
    /**
     * 描述
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