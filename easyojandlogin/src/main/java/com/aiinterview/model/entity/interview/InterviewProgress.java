package com.aiinterview.model.entity.interview;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 面试进度实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("interview_progress")
public class InterviewProgress {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("application_id")
    private Integer applicationId;
    
    // 笔试相关信息
    @TableField("written_test_status")
    private String writtenTestStatus; // not_started, in_progress, completed, passed, failed
    
    @TableField("written_test_score")
    private BigDecimal writtenTestScore;
    
    @TableField("written_test_passed")
    private Boolean writtenTestPassed;
    
    @TableField("written_test_completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime writtenTestCompletedAt;
    
    @TableField("written_test_result_json")
    private String writtenTestResultJson; // 详细结果JSON
    
    // 面试相关信息
    @TableField("interview_status")
    private String interviewStatus; // not_started, scheduled, in_progress, completed, passed, failed
    
    @TableField("interview_score")
    private BigDecimal interviewScore;
    
    @TableField("interview_passed")
    private Boolean interviewPassed;
    
    @TableField("interview_scheduled_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime interviewScheduledAt;
    
    @TableField("interview_completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime interviewCompletedAt;
    
    @TableField("interview_result_json")
    private String interviewResultJson; // 详细结果JSON
    
    // 整体进度
    @TableField("overall_status")
    private String overallStatus; // pending, written_test, interview, completed, rejected
    
    @TableField("final_result")
    private String finalResult; // pending, passed, failed
    
    @TableField("notes")
    private String notes; // 备注信息
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
