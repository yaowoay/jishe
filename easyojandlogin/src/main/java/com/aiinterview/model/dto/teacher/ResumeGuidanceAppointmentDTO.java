package com.aiinterview.model.dto.teacher;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 简历指导预约DTO
 */
@Data
public class ResumeGuidanceAppointmentDTO {

    private Long appointmentId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "预约时间不能为空")
    private LocalDateTime appointmentTime;

    private Integer duration;

    private String location;

    private String guidanceType;

    private String studentResumeUrl;

    private String studentNotes;

    private String teacherFeedback;

    private String status;

    private Integer rating;
}
