package com.aiinterview.model.dto.teacher;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动报名DTO
 */
@Data
public class ActivityRegistrationDTO {
    
    private Long registrationId;
    
    private Long activityId;
    
    private Long studentId;
    
    private String studentName;
    
    private String studentNo;
    
    private String major;
    
    private LocalDateTime registerTime;
    
    private LocalDateTime signInTime;
    
    private String signInMethod; // 'qrcode', 'manual', 'face'
    
    private String status; // 'registered', 'signed_in', 'absent', 'cancelled'
    
    private String feedback;
    
    private Integer rating;
}
