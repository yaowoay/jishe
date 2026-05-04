package com.aiinterview.model.dto.student;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生端活动展示DTO
 */
@Data
public class StudentActivityDTO {

    private Long activityId;
    private String title;
    private String type;
    private String mode;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String status;
    private String posterUrl;

    /** 是否已报名 */
    private Boolean registered;
    /** 报名记录ID */
    private Long registrationId;
    /** 报名状态：registered/signed_in/absent/cancelled */
    private String registrationStatus;
    /** 报名时间 */
    private LocalDateTime registerTime;
    /** 签到时间 */
    private LocalDateTime signInTime;
}
