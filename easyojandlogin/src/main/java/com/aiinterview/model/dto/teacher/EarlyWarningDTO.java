package com.aiinterview.model.dto.teacher;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 就业预警DTO
 */
@Data
public class EarlyWarningDTO {
    
    private Long id;
    
    private Long studentId;
    
    private String studentName;
    
    private String studentNo;
    
    private String major;
    
    private String warningType; // 'employment', 'skill', 'interview', 'resume'
    
    private String warningLevel; // 'low', 'medium', 'high', 'urgent'
    
    private Integer warningScore;
    
    private String triggerReason;
    
    private LocalDateTime detectionTime;
    
    private Long assignedTo;
    
    private String assignedTeacherName;
    
    private String handleStatus; // 'pending', 'processing', 'resolved', 'ignored'
    
    private LocalDateTime handleTime;
    
    private String handleRemark;

    private Boolean isNotified;

    private LocalDateTime notifyTime;

    private Boolean studentViewed;

    private LocalDateTime studentViewTime;

    private String studentResponse;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
