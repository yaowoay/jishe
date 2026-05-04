package com.aiinterview.model.entity.writtenTest;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 笔试答案结果实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("written_test_answers")
public class WrittenTestAnswer {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("application_id")
    private Integer applicationId;
    
    @TableField("written_test_result_id")
    private Long writtenTestResultId;
    
    @TableField("answers_json")
    private String answersJson; // 用户答案JSON
    
    @TableField("score")
    private BigDecimal score; // 总得分
    
    @TableField("total_questions")
    private Integer totalQuestions; // 总题目数
    
    @TableField("correct_answers")
    private Integer correctAnswers; // 正确答案数
    
    @TableField("wrong_answers")
    private Integer wrongAnswers; // 错误答案数
    
    @TableField("completion_time")
    private Integer completionTime; // 完成时间（秒）
    
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // 开始答题时间
    
    @TableField("submit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime; // 提交答题时间
    
    @TableField("status")
    private String status; // 答题状态：not_started, in_progress, completed
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
