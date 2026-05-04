package com.aiinterview.model.entity.writtenTest;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 笔试结果实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("written_test_results")
public class WrittenTestResult {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("application_id")
    private Integer applicationId;
    
    @TableField("resume_id")
    private Long resumeId;
    
    @TableField("job_position")
    private String jobPosition;
    
    @TableField("candidate_field")
    private String candidateField; // 候选人所属领域（人工智能、大数据等）
    
    @TableField("questions_json")
    private String questionsJson; // 生成的题目JSON
    
    @TableField("total_questions")
    private Integer totalQuestions; // 总题目数
    
    @TableField("choice_questions")
    private Integer choiceQuestions; // 选择题数量
    
    @TableField("true_false_questions")
    private Integer trueFalseQuestions; // 判断题数量
    
    @TableField("total_score")
    private Integer totalScore; // 总分
    
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
