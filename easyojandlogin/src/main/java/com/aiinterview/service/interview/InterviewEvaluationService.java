package com.aiinterview.service.interview;

import com.aiinterview.model.dto.interview.InterviewEvaluationRequest;
import com.aiinterview.model.dto.interview.InterviewEvaluationResponse;

import java.util.List;

/**
 * 面试评估服务接口
 */
public interface InterviewEvaluationService {
    
    /**
     * 执行面试评估与资源推荐
     * 
     * @param request 评估请求
     * @return 评估响应
     */
    InterviewEvaluationResponse evaluateInterview(InterviewEvaluationRequest request);
    
    /**
     * 根据申请ID获取评估结果
     * 
     * @param applicationId 申请ID
     * @return 评估响应
     */
    InterviewEvaluationResponse getEvaluationByApplicationId(Integer applicationId);
    
    /**
     * 根据评估ID获取评估结果
     * 
     * @param evaluationId 评估ID
     * @return 评估响应
     */
    InterviewEvaluationResponse getEvaluationById(Long evaluationId);
    
    /**
     * 获取候选人的所有评估记录
     * 
     * @param candidateId 候选人ID
     * @return 评估响应列表
     */
    List<InterviewEvaluationResponse> getEvaluationsByCandidateId(String candidateId);
    
    /**
     * 重新评估面试结果
     * 
     * @param applicationId 申请ID
     * @return 评估响应
     */
    InterviewEvaluationResponse reEvaluateInterview(Integer applicationId);
    
    /**
     * 删除评估记录
     * 
     * @param applicationId 申请ID
     * @return 是否删除成功
     */
    boolean deleteEvaluation(Integer applicationId);
    
    /**
     * 获取最近的评估记录
     * 
     * @param limit 限制数量
     * @return 评估记录列表
     */
    List<InterviewEvaluationResponse> getRecentEvaluations(int limit);
    
    /**
     * 验证资源链接的可用性
     * 
     * @param evaluationId 评估ID
     * @return 验证结果
     */
    boolean verifyResourceLinks(Long evaluationId);
    
    /**
     * 获取评估统计信息
     * 
     * @return 统计信息
     */
    EvaluationStatistics getEvaluationStatistics();
    
    /**
     * 评估统计信息
     */
    class EvaluationStatistics {
        private Long totalEvaluations;
        private Long completedEvaluations;
        private Long pendingEvaluations;
        private Long failedEvaluations;
        private Double averageInterviewScore;
        private Double averageWrittenTestScore;
        private Double averageWeightedScore;
        
        // Getters and Setters
        public Long getTotalEvaluations() { return totalEvaluations; }
        public void setTotalEvaluations(Long totalEvaluations) { this.totalEvaluations = totalEvaluations; }
        
        public Long getCompletedEvaluations() { return completedEvaluations; }
        public void setCompletedEvaluations(Long completedEvaluations) { this.completedEvaluations = completedEvaluations; }
        
        public Long getPendingEvaluations() { return pendingEvaluations; }
        public void setPendingEvaluations(Long pendingEvaluations) { this.pendingEvaluations = pendingEvaluations; }
        
        public Long getFailedEvaluations() { return failedEvaluations; }
        public void setFailedEvaluations(Long failedEvaluations) { this.failedEvaluations = failedEvaluations; }
        
        public Double getAverageInterviewScore() { return averageInterviewScore; }
        public void setAverageInterviewScore(Double averageInterviewScore) { this.averageInterviewScore = averageInterviewScore; }
        
        public Double getAverageWrittenTestScore() { return averageWrittenTestScore; }
        public void setAverageWrittenTestScore(Double averageWrittenTestScore) { this.averageWrittenTestScore = averageWrittenTestScore; }
        
        public Double getAverageWeightedScore() { return averageWeightedScore; }
        public void setAverageWeightedScore(Double averageWeightedScore) { this.averageWeightedScore = averageWeightedScore; }
    }
}
