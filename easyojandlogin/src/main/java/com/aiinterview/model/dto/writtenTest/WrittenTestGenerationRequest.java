package com.aiinterview.model.dto.writtenTest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * 笔试生成请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrittenTestGenerationRequest {
    
    @NotNull(message = "申请ID不能为空")
    private Integer applicationId;
    
    @NotNull(message = "简历ID不能为空")
    private Long resumeId;
    
    private String jobPosition; // 职位名称
    
    private String jobDescription; // 职位描述（bs参数）
    
    @Min(value = 1, message = "题目总数至少为1")
    @Max(value = 50, message = "题目总数最多为50")
    private Integer totalQuestions = 20; // 总题目数，默认20题
    
    @Min(value = 0, message = "选择题数量不能小于0")
    private Integer choiceQuestions = 10; // 选择题数量，默认10题
    
    @Min(value = 0, message = "判断题数量不能小于0")
    private Integer trueFalseQuestions = 10; // 判断题数量，默认10题
    
    @Min(value = 1, message = "总分至少为1")
    @Max(value = 1000, message = "总分最多为1000")
    private Integer totalScore = 100; // 总分，默认100分
    
    private String user = "system"; // 用户标识，默认为system
    
    private String responseMode = "blocking"; // 响应模式，默认为blocking
    
    private Boolean aiGenerated = true; // 是否AI生成，默认为true
}
