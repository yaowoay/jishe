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
 * 活动报名签到实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("activity_registrations")
public class ActivityRegistration {

    @TableId(value = "registration_id", type = IdType.AUTO)
    private Long registrationId;

    @TableField("activity_id")
    private Long activityId;

    @TableField("student_id")
    private Long studentId;

    @TableField("register_time")
    private LocalDateTime registerTime;

    @TableField("sign_in_time")
    private LocalDateTime signInTime;

    @TableField("sign_in_method")
    private String signInMethod;

    @TableField("status")
    private String status;

    @TableField("feedback")
    private String feedback;

    @TableField("rating")
    private Integer rating;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
