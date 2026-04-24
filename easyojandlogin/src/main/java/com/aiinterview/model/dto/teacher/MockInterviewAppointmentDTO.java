package com.aiinterview.model.dto.teacher;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 模拟面试预约DTO
 */
@Data
public class MockInterviewAppointmentDTO {

    private Long appointmentId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "预约时间不能为空")
    private LocalDateTime appointmentTime;

    private Integer duration;

    private String location;

    private String interviewType;

    private String targetPosition;

    private String interviewMode;

    private String meetingLink;

    private String studentResumeUrl;

    private String studentNotes;

    private String interviewQuestions;

    private String interviewFeedback;

    private Integer performanceScore;

    private String strengths;

    private String weaknesses;

    private String improvementSuggestions;

    private String status;

    private Integer rating;
}
