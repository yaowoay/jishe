package com.aiinterview.model.entity.resume;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 简历评分实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("resume_scores")
public class ResumeScores {
    
    @TableId(value = "score_id", type = IdType.AUTO)
    private Long scoreId;
    
    @TableField("report_id")
    private Long reportId;
    
    @TableField("total_score")
    private Double totalScore;
    
    @TableField("tech_match")
    private Double techMatch;
    
    @TableField("experience_match")
    private Double experienceMatch;
    
    @TableField("education_match")
    private Double educationMatch;
    
    @TableField("potential_match")
    private Double potentialMatch;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
