package com.aiinterview.service.impl.candidataAnswer;

import com.aiinterview.model.dto.candidataAnswer.CandidateAnswerStatsDTO;
import com.aiinterview.model.entity.candidataAnswer.CandidateAnswerStats;
import com.aiinterview.repository.candidataAnswer.CandidateAnswerStatsRepository;
import com.aiinterview.service.candidataAnswer.CandidateAnswerStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应聘者答题统计信息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CandidateAnswerStatsServiceImpl implements CandidateAnswerStatsService {
    
    private final CandidateAnswerStatsRepository candidateAnswerStatsRepository;
    
    @Override
    public CandidateAnswerStats saveOrUpdate(CandidateAnswerStatsDTO dto) {
        log.info("保存或更新答题统计信息，申请ID: {}", dto.getApplicationId());
        
        try {
            // 查询是否已存在记录
            CandidateAnswerStats existing = candidateAnswerStatsRepository.findByApplicationId(dto.getApplicationId());
            
            if (existing != null) {
                // 更新现有记录
                existing.setWrittenAnswerCount(dto.getWrittenAnswerCount() != null ? dto.getWrittenAnswerCount() : existing.getWrittenAnswerCount());
                existing.setWrittenAnswerRange(dto.getWrittenAnswerRange() != null ? dto.getWrittenAnswerRange() : existing.getWrittenAnswerRange());
                existing.setWrittenAiGenerated(dto.getWrittenAiGenerated() != null ? dto.getWrittenAiGenerated() : existing.getWrittenAiGenerated());
                existing.setInterviewAnswerCount(dto.getInterviewAnswerCount() != null ? dto.getInterviewAnswerCount() : existing.getInterviewAnswerCount());
                existing.setInterviewAnswerRange(dto.getInterviewAnswerRange() != null ? dto.getInterviewAnswerRange() : existing.getInterviewAnswerRange());
                existing.setInterviewAiGenerated(dto.getInterviewAiGenerated() != null ? dto.getInterviewAiGenerated() : existing.getInterviewAiGenerated());
                
                candidateAnswerStatsRepository.updateById(existing);
                log.info("更新答题统计信息成功，ID: {}", existing.getId());
                return existing;
            } else {
                // 创建新记录
                CandidateAnswerStats newStats = new CandidateAnswerStats();
                newStats.setApplicationId(dto.getApplicationId());
                newStats.setWrittenAnswerCount(dto.getWrittenAnswerCount() != null ? dto.getWrittenAnswerCount() : 0);
                newStats.setWrittenAnswerRange(dto.getWrittenAnswerRange());
                newStats.setWrittenAiGenerated(dto.getWrittenAiGenerated() != null ? dto.getWrittenAiGenerated() : true);
                newStats.setInterviewAnswerCount(dto.getInterviewAnswerCount() != null ? dto.getInterviewAnswerCount() : 0);
                newStats.setInterviewAnswerRange(dto.getInterviewAnswerRange());
                newStats.setInterviewAiGenerated(dto.getInterviewAiGenerated() != null ? dto.getInterviewAiGenerated() : true);
                
                candidateAnswerStatsRepository.insert(newStats);
                log.info("创建答题统计信息成功，ID: {}", newStats.getId());
                return newStats;
            }
        } catch (Exception e) {
            log.error("保存或更新答题统计信息失败，申请ID: {}", dto.getApplicationId(), e);
            throw new RuntimeException("保存答题统计信息失败", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public CandidateAnswerStats getByApplicationId(Integer applicationId) {
        return candidateAnswerStatsRepository.findByApplicationId(applicationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CandidateAnswerStats> getByApplicationIds(List<Integer> applicationIds) {
        if (applicationIds == null || applicationIds.isEmpty()) {
            return new ArrayList<>();
        }
        return candidateAnswerStatsRepository.findByApplicationIds(applicationIds);
    }
    
    @Override
    public CandidateAnswerStats setWrittenTestSettings(Integer applicationId, Integer answerCount, String answerRange, Boolean aiGenerated) {
        log.info("设置笔试参数，申请ID: {}, 答题数量: {}, 答题范围: {}, AI生成: {}", applicationId, answerCount, answerRange, aiGenerated);

        CandidateAnswerStatsDTO dto = new CandidateAnswerStatsDTO();
        dto.setApplicationId(applicationId);
        dto.setWrittenAnswerCount(answerCount);
        dto.setWrittenAnswerRange(answerRange);
        dto.setWrittenAiGenerated(aiGenerated != null ? aiGenerated : true);

        return saveOrUpdate(dto);
    }
    
    @Override
    public CandidateAnswerStats setInterviewSettings(Integer applicationId, Integer answerCount, String answerRange, Boolean aiGenerated) {
        log.info("设置面试参数，申请ID: {}, 答题数量: {}, 答题范围: {}, AI生成: {}", applicationId, answerCount, answerRange, aiGenerated);

        CandidateAnswerStatsDTO dto = new CandidateAnswerStatsDTO();
        dto.setApplicationId(applicationId);
        dto.setInterviewAnswerCount(answerCount);
        dto.setInterviewAnswerRange(answerRange);
        dto.setInterviewAiGenerated(aiGenerated != null ? aiGenerated : true);

        return saveOrUpdate(dto);
    }
    
    @Override
    public boolean deleteByApplicationId(Integer applicationId) {
        try {
            int result = candidateAnswerStatsRepository.deleteByApplicationId(applicationId);
            log.info("删除答题统计信息，申请ID: {}, 影响行数: {}", applicationId, result);
            return result > 0;
        } catch (Exception e) {
            log.error("删除答题统计信息失败，申请ID: {}", applicationId, e);
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            long writtenTestCount = candidateAnswerStatsRepository.countWithWrittenTestSettings();
            long interviewCount = candidateAnswerStatsRepository.countWithInterviewSettings();
            long totalCount = candidateAnswerStatsRepository.selectCount(null);
            
            statistics.put("writtenTestSettingsCount", writtenTestCount);
            statistics.put("interviewSettingsCount", interviewCount);
            statistics.put("totalSettingsCount", totalCount);
            
            log.info("获取答题统计信息统计数据: {}", statistics);
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            statistics.put("writtenTestSettingsCount", 0L);
            statistics.put("interviewSettingsCount", 0L);
            statistics.put("totalSettingsCount", 0L);
        }
        
        return statistics;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CandidateAnswerStats> getWithWrittenTestSettings() {
        return candidateAnswerStatsRepository.findWithWrittenTestSettings();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CandidateAnswerStats> getWithInterviewSettings() {
        return candidateAnswerStatsRepository.findWithInterviewSettings();
    }
}
