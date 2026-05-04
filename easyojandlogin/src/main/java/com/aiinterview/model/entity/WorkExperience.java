package com.aiinterview.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工作经历实体类
 */
@Data
@TableName("work_experience")
public class WorkExperience {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 简历ID
     */
    private Long resumeId;
    
    /**
     * 公司名称
     */
    private String company;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 开始时间
     */
    private LocalDate startDate;
    
    /**
     * 结束时间
     */
    private LocalDate endDate;
    
    /**
     * 工作职责
     */
    private String responsibility;
    
    /**
     * 工作成果
     */
    private String achievement;
    
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