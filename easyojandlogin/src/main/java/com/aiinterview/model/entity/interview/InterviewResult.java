package com.aiinterview.model.entity.interview;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 面试结果实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("interview_results")
public class InterviewResult {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("application_id")
    private Integer applicationId;
    
    @TableField("resume_id")
    private Long resumeId;
    
    @TableField("job_position")
    private String jobPosition;
    
    @TableField("job_description")
    private String jobDescription; // 职位描述
    
    @TableField("candidate_name")
    private String candidateName; // 候选人姓名
    
    @TableField("questions_json")
    private String questionsJson; // 生成的面试题目JSON
    
    @TableField("total_questions")
    private Integer totalQuestions; // 总题目数
    
    @TableField("workflow_run_id")
    private String workflowRunId; // Dify工作流运行ID
    
    @TableField("generation_status")
    private String generationStatus; // 生成状态：pending, success, failed
    
    @TableField("error_message")
    private String errorMessage; // 错误信息
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
