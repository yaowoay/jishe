package com.aiinterview.model.entity.candidataAnswer;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 应聘者笔试和面试答题统计信息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("candidate_answer_stats")
public class CandidateAnswerStats {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    @TableField("application_id")
    private Integer applicationId;
    
    @TableField("written_answer_count")
    private Integer writtenAnswerCount;
    
    @TableField("written_answer_range")
    private String writtenAnswerRange;

    @TableField("written_ai_generated")
    private Boolean writtenAiGenerated;

    @TableField("interview_answer_count")
    private Integer interviewAnswerCount;

    @TableField("interview_answer_range")
    private String interviewAnswerRange;

    @TableField("interview_ai_generated")
    private Boolean interviewAiGenerated;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
