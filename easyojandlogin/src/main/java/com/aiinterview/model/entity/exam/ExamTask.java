package com.aiinterview.model.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_task")
public class ExamTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String taskId;
    private Long userId;
    private String jobPosition;
    private String skills;
    private String experience;
    private Integer questionCount;
    private String difficultyLevel;
    private String focusArea;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
