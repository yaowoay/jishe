package com.aiinterview.model.entity.college;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 学院实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("colleges")
public class College {
    
    @TableId(value = "college_id", type = IdType.AUTO)
    private Long collegeId;
    
    @TableField("college_code")
    private String collegeCode;
    
    @TableField("college_name")
    private String collegeName;
    
    @TableField("dean")
    private String dean;
    
    @TableField("student_count")
    private Integer studentCount;
    
    @TableField("description")
    private String description;
    
    @TableField("status")
    private String status; // 'active', 'inactive'
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}