package com.aiinterview.model.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exam_judgment_detail")
public class ExamJudgmentDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String taskId;
    private String questionId;
    private String userAnswer;
    private String correctAnswer;
    private Integer isCorrect;
    private String explanation;
    private LocalDateTime createdAt;
}
