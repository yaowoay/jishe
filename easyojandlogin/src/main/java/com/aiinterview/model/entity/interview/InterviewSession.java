package com.aiinterview.model.entity.interview;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("interview_session")
public class InterviewSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String sessionId;
    private Long userId;
    private String resumePath;
    private String jobPosition;
    private String style;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
