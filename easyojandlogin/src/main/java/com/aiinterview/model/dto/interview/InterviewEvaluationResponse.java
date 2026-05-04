package com.aiinterview.model.dto.interview;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试评估响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewEvaluationResponse {
    
    /**
     * 评估ID
     */
    private Long evaluationId;
    
    /**
     * 申请ID
     */
    private Integer applicationId;
    
    /**
     * 候选人ID
     */
    private String candidateId;
    
    /**
     * 应聘岗位
     */
    private String jobPosition;
    
    /**
     * 面试得分
     */
    private BigDecimal interviewScore;
    
    /**
     * 笔试得分
     */
    private BigDecimal writtenTestScore;
    
    /**
     * 权重后的综合得分
     */
    private BigDecimal weightedScore;
    
    /**
     * 面试评估详情
     */
    private InterviewScoreDetail interviewEvaluation;
    
    /**
     * 笔试评估详情
     */
    private WrittenTestScoreDetail writtenTestEvaluation;
    
    /**
     * 需要改进的领域
     */
    private List<String> improvementAreas;
    
    /**
     * 学习建议
     */
    private List<String> recommendations;
    
    /**
     * 推荐资源
     */
    private List<ResourceDetail> resources;
    
    /**
     * 评估状态
     */
    private String evaluationStatus;
    
    /**
     * 评估时间
     */
    private LocalDateTime evaluatedAt;
    
    /**
     * 面试评分详情
     */
    @Data
    @Builder
    public static class InterviewScoreDetail {
        private String jobTitle;
        private String question;
        private String candidateResponse;
        private Integer score;
    }
    
    /**
     * 笔试评分详情
     */
    @Data
    @Builder
    public static class WrittenTestScoreDetail {
        private String jobTitle;
        private String question;
        private String candidateResponse;
        private Integer score;
    }
    
    /**
     * 资源详情
     */
    @Data
    @Builder
    public static class ResourceDetail {
        private String name;
        private String type; // book, course, article, video, practice
        private String url;
        private String description;
        private String improvementArea;
        private Integer priority;
    }
}
