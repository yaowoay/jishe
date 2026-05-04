package com.aiinterview.model.entity.professionalTest;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 测试题目实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("test_questions")
public class TestQuestion {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("test_result_id")
    private Long testResultId;
    
    @TableField("question_id")
    private Integer questionId;
    
    @TableField("question")
    private String question;
    
    @TableField("english_question")
    private String englishQuestion;
    
    @TableField("question_type")
    private String questionType;
    
    @TableField("options")
    private String options; // JSON字符串
    
    @TableField("correct_answer")
    private String correctAnswer; // JSON字符串
    
    @TableField("chinese_answer")
    private String chineseAnswer;
    
    @TableField("english_answer")
    private String englishAnswer;
    
    @TableField("difficulty")
    private String difficulty;
    
    @TableField("category")
    private String category;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
