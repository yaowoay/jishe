package com.aiinterview.model.dto.candidataAnswer;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 应聘者答题统计信息数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAnswerStatsDTO {
    
    @NotNull(message = "申请ID不能为空")
    private Integer applicationId;
    
    @Min(value = 0, message = "笔试答题数量不能小于0")
    private Integer writtenAnswerCount;
    
    private String writtenAnswerRange;

    private Boolean writtenAiGenerated;

    @Min(value = 0, message = "面试答题数量不能小于0")
    private Integer interviewAnswerCount;

    private String interviewAnswerRange;

    private Boolean interviewAiGenerated;
}
