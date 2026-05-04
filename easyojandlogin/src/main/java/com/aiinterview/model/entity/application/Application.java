package com.aiinterview.model.entity.application;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 申请实体类 - 根据数据库表 applications 重新设计
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("applications")
public class Application {

    @TableId(value = "application_id", type = IdType.AUTO)
    private Long applicationId;

    @TableField("job_id")
    private Long jobId;

    @TableField("user_id")
    private Long userId;

    @TableField("apply_time")
    private LocalDateTime applyTime;

    @TableField("status")
    private String status; // 'pending', 'reviewed', 'interview', 'accepted', 'rejected'

    @TableField("ai_evaluation_score")
    private Float aiEvaluationScore;

    @TableField("feedback")
    private String feedback;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField("resume_id")
    private Long resumeId;

    @TableField("interview_time")
    private LocalDateTime interviewTime;

    @TableField("rejection_reason")
    private String rejectionReason;
}
