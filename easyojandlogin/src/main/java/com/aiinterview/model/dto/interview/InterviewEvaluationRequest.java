package com.aiinterview.model.dto.interview;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 面试评估请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewEvaluationRequest {
    
    /**
     * 申请ID
     */
    private Integer applicationId;
    
    /**
     * 面试ID（可选）
     */
    private Long interviewId;
    
    /**
     * 笔试答案ID（可选）
     */
    private Long writtenTestAnswerId;
    
    /**
     * 是否强制重新评估（即使已有评估结果）
     */
    @Builder.Default
    private Boolean forceReEvaluate = false;

    /**
     * 评估类型：full（全面评估）, interview_only（仅面试）, written_only（仅笔试）
     */
    @Builder.Default
    private String evaluationType = "full";
}
