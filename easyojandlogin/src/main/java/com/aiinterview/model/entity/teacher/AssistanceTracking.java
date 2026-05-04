package com.aiinterview.model.entity.teacher;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 帮扶效果跟踪实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("assistance_tracking")
public class AssistanceTracking {

    @TableId(value = "tracking_id", type = IdType.AUTO)
    private Long trackingId;

    @TableField("record_id")
    private Long recordId;

    @TableField("tracking_date")
    private LocalDate trackingDate;

    @TableField("tracking_content")
    private String trackingContent;

    @TableField("student_feedback")
    private String studentFeedback;

    @TableField("progress_status")
    private String progressStatus;

    @TableField("next_action")
    private String nextAction;

    @TableField("before_score")
    private Integer beforeScore;

    @TableField("after_score")
    private Integer afterScore;

    @TableField("improvement_rate")
    private BigDecimal improvementRate;

    @TableField("effectiveness")
    private String effectiveness;

    @TableField("created_by")
    private Long createdBy;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
