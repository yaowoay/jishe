package com.aiinterview.model.entity.interview;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 面试评估结果实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("interview_evaluations")
public class InterviewEvaluation {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("application_id")
    private Integer applicationId;
    
    @TableField("interview_id")
    private Long interviewId;
    
    @TableField("written_test_answer_id")
    private Long writtenTestAnswerId;
    
    @TableField("job_position")
    private String jobPosition; // 应聘岗位
    
    @TableField("candidate_id")
    private String candidateId; // 候选人ID
    
    @TableField("interview_score")
    private BigDecimal interviewScore; // 面试得分
    
    @TableField("written_test_score")
    private BigDecimal writtenTestScore; // 笔试得分
    
    @TableField("weighted_score")
    private BigDecimal weightedScore; // 权重后的综合得分
    
    @TableField("interview_evaluation_json")
    private String interviewEvaluationJson; // 面试评估详情JSON
    
    @TableField("written_test_evaluation_json")
    private String writtenTestEvaluationJson; // 笔试评估详情JSON
    
    @TableField("improvement_areas")
    private String improvementAreas; // 需要改进的领域JSON数组
    
    @TableField("recommendations")
    private String recommendations; // 学习建议JSON数组
    
    @TableField("resources_json")
    private String resourcesJson; // 推荐资源JSON数组
    
    @TableField("evaluation_status")
    private String evaluationStatus; // 评估状态：pending, completed, failed
    
    @TableField("dify_workflow_run_id")
    private String difyWorkflowRunId; // Dify工作流运行ID
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
