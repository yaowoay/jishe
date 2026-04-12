package com.aiinterview.service.candidataAnswer;

import com.aiinterview.model.dto.candidataAnswer.CandidateAnswerStatsDTO;
import com.aiinterview.model.entity.candidataAnswer.CandidateAnswerStats;

import java.util.List;
import java.util.Map;

/**
 * 应聘者答题统计信息服务接口
 */
public interface CandidateAnswerStatsService {
    
    /**
     * 保存或更新答题统计信息
     * @param dto 答题统计信息DTO
     * @return 保存后的实体
     */
    CandidateAnswerStats saveOrUpdate(CandidateAnswerStatsDTO dto);
    
    /**
     * 根据申请ID获取答题统计信息
     * @param applicationId 申请ID
     * @return 答题统计信息
     */
    CandidateAnswerStats getByApplicationId(Integer applicationId);
    
    /**
     * 根据申请ID列表批量获取答题统计信息
     * @param applicationIds 申请ID列表
     * @return 答题统计信息列表
     */
    List<CandidateAnswerStats> getByApplicationIds(List<Integer> applicationIds);
    
    /**
     * 设置笔试参数
     * @param applicationId 申请ID
     * @param answerCount 答题数量
     * @param answerRange 答题范围
     * @param aiGenerated 是否AI生成
     * @return 更新后的实体
     */
    CandidateAnswerStats setWrittenTestSettings(Integer applicationId, Integer answerCount, String answerRange, Boolean aiGenerated);

    /**
     * 设置面试参数
     * @param applicationId 申请ID
     * @param answerCount 答题数量
     * @param answerRange 答题范围
     * @param aiGenerated 是否AI生成
     * @return 更新后的实体
     */
    CandidateAnswerStats setInterviewSettings(Integer applicationId, Integer answerCount, String answerRange, Boolean aiGenerated);
    
    /**
     * 删除答题统计信息
     * @param applicationId 申请ID
     * @return 是否删除成功
     */
    boolean deleteByApplicationId(Integer applicationId);
    
    /**
     * 获取统计信息
     * @return 统计数据
     */
    Map<String, Object> getStatistics();
    
    /**
     * 获取有笔试设置的记录
     * @return 记录列表
     */
    List<CandidateAnswerStats> getWithWrittenTestSettings();
    
    /**
     * 获取有面试设置的记录
     * @return 记录列表
     */
    List<CandidateAnswerStats> getWithInterviewSettings();
}
