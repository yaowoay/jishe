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
 * 模拟面试预约实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mock_interview_appointments")
public class MockInterviewAppointment {

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

    @TableField("interview_type")
    private String interviewType;

    @TableField("target_position")
    private String targetPosition;

    @TableField("interview_mode")
    private String interviewMode;

    @TableField("meeting_link")
    private String meetingLink;

    @TableField("student_resume_url")
    private String studentResumeUrl;

    @TableField("student_notes")
    private String studentNotes;

    @TableField("interview_questions")
    private String interviewQuestions;

    @TableField("interview_feedback")
    private String interviewFeedback;

    @TableField("performance_score")
    private Integer performanceScore;

    @TableField("strengths")
    private String strengths;

    @TableField("weaknesses")
    private String weaknesses;

    @TableField("improvement_suggestions")
    private String improvementSuggestions;

    @TableField("status")
    private String status;

    @TableField("rating")
    private Integer rating;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
