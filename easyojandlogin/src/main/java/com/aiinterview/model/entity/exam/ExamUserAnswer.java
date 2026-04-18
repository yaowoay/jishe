package com.aiinterview.model.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exam_user_answer")
public class ExamUserAnswer {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String taskId;
    private String questionId;
    private String userAnswer;
    private LocalDateTime createdAt;
}
