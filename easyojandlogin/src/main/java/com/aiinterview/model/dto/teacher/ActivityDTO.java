package com.aiinterview.model.dto.teacher;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 就业活动DTO
 */
@Data
public class ActivityDTO {
    
    private Long activityId;
    
    private Long collegeId;
    
    private Long teacherId;
    
    @NotBlank(message = "活动标题不能为空")
    private String title;
    
    @NotBlank(message = "活动类型不能为空")
    private String type; // 'job_fair', 'lecture', 'training', 'visit'
    
    @NotBlank(message = "活动模式不能为空")
    private String mode; // 'online', 'offline', 'hybrid'
    
    private String location;
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    private Integer maxParticipants;
    
    private Integer currentParticipants;
    
    private String status; // 'draft', 'published', 'ongoing', 'completed', 'cancelled'
    
    private String posterUrl;
    
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
