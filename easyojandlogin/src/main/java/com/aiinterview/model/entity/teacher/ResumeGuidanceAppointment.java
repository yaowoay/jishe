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
 * 简历指导预约实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("resume_guidance_appointments")
public class ResumeGuidanceAppointment {

    @TableId(value = "appointment_id", type = IdType.AUTO)
    private Long appointmentId;

    @TableField("student_id")
    private Long studentId;

    @TableField("teacher_id")
    private Long teacherId;

    @TableField("appointment_time")
    private LocalDateTime appointmentTime;

    @TableField("duration")
    private Integer duration;

    @TableField("location")
    private String location;

    @TableField("guidance_type")
    private String guidanceType;

    @TableField("student_resume_url")
    private String studentResumeUrl;

    @TableField("student_notes")
    private String studentNotes;

    @TableField("teacher_feedback")
    private String teacherFeedback;

    @TableField("status")
    private String status;

    @TableField("rating")
    private Integer rating;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
