package com.aiinterview.model.entity.professionalTest;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 测试结果实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("test_results")
public class TestResult {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("category")
    private String category;
    
    @TableField("category_name")
    private String categoryName;
    
    @TableField("score")
    private Integer score;
    
    @TableField("total_questions")
    private Integer totalQuestions;
    
    @TableField("correct_answers")
    private Integer correctAnswers;
    
    @TableField("duration")
    private Integer duration;
    
    @TableField("completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;
    
    @TableField("analysis_report")
    private String analysisReport;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
