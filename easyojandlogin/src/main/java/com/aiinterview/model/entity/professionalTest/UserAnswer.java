package com.aiinterview.model.entity.professionalTest;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户答题记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_answers")
public class UserAnswer {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("test_result_id")
    private Long testResultId;
    
    @TableField("question_id")
    private Integer questionId;
    
    @TableField("user_answer")
    private String userAnswer; // JSON字符串
    
    @TableField("is_correct")
    private Boolean isCorrect;
    
    @TableField("answer_time")
    private Integer answerTime;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
