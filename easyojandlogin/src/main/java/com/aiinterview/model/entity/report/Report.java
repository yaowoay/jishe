package com.aiinterview.model.entity.report;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 报告实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("reports")
public class Report {
    
    @TableId(value = "report_id", type = IdType.AUTO)
    private Long reportId;
    
    @TableField("job_id")
    private Long jobId;
    
    @TableField("resume_id")
    private Long resumeId;
    
    @TableField("report")
    private String report; // JSON格式的报告内容
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
