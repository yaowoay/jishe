package com.aiinterview.model.entity.interview;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 面试评分实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("interview_scores")
public class InterviewScores {
    
    @TableId(value = "score_id", type = IdType.AUTO)
    private Long scoreId;
    
    @TableField("interview_id")
    private Long interviewId;
    
    @TableField("technical_ability")
    private Double technicalAbility;
    
    @TableField("learning_ability")
    private Double learningAbility;
    
    @TableField("team_collaboration")
    private Double teamCollaboration;
    
    @TableField("problem_solving")
    private Double problemSolving;
    
    @TableField("communication_expression")
    private Double communicationExpression;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
