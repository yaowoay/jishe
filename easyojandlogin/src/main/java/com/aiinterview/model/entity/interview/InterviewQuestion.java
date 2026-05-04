package com.aiinterview.model.entity.interview;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("interview_question")
public class InterviewQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String questionId;
    private String sessionId;
    private String content;
    private String category;
    private String difficulty;
    private LocalDateTime createdAt;
}
