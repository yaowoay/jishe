package com.aiinterview.model.entity.interview;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 面试答案记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("interview_answers")
public class InterviewAnswer {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("application_id")
    private Integer applicationId;
    
    @TableField("interview_result_id")
    private Long interviewResultId;
    
    @TableField("answers_json")
    private String answersJson; // 用户答案JSON
    
    @TableField("score")
    private BigDecimal score; // 总得分
    
    @TableField("total_questions")
    private Integer totalQuestions; // 总题目数
    
    @TableField("completion_time")
    private Integer completionTime; // 完成时间（分钟）
    
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime; // 开始面试时间
    
    @TableField("submit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime; // 提交面试时间
    
    @TableField("status")
    private String status; // 面试状态：not_started, in_progress, completed
    
    @TableField("interviewer_feedback")
    private String interviewerFeedback; // 面试官反馈
    
    @TableField("technical_score")
    private BigDecimal technicalScore; // 技术能力评分
    
    @TableField("communication_score")
    private BigDecimal communicationScore; // 沟通能力评分
    
    @TableField("problem_solving_score")
    private BigDecimal problemSolvingScore; // 问题解决能力评分
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
