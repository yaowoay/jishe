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
 * 就业活动实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("activities")
public class Activity {

    @TableId(value = "activity_id", type = IdType.AUTO)
    private Long activityId;

    @TableField("college_id")
    private Long collegeId;

    @TableField("teacher_id")
    private Long teacherId;

    @TableField("title")
    private String title;

    @TableField("type")
    private String type;

    @TableField("mode")
    private String mode;

    @TableField("location")
    private String location;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("max_participants")
    private Integer maxParticipants;

    @TableField("current_participants")
    private Integer currentParticipants;

    @TableField("status")
    private String status;

    @TableField("poster_url")
    private String posterUrl;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
