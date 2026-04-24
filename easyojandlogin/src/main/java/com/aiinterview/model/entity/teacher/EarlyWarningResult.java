package com.aiinterview.model.entity.teacher;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 预警结果实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("early_warning_results")
public class EarlyWarningResult {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    @TableField("warning_type")
    private String warningType;

    @TableField("warning_level")
    private String warningLevel;

    @TableField("warning_score")
    private Integer warningScore;

    @TableField("trigger_reason")
    private String triggerReason;

    @TableField("detection_time")
    private LocalDateTime detectionTime;

    @TableField("assigned_to")
    private Long assignedTo;

    @TableField("handle_status")
    private String handleStatus;

    @TableField("handle_time")
    private LocalDateTime handleTime;

    @TableField("handle_remark")
    private String handleRemark;

    @TableField("is_notified")
    private Boolean isNotified;

    @TableField("notify_time")
    private LocalDateTime notifyTime;

    @TableField("student_viewed")
    private Boolean studentViewed;

    @TableField("student_view_time")
    private LocalDateTime studentViewTime;

    @TableField("student_response")
    private String studentResponse;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
