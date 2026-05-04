package com.aiinterview.service.impl.interview;

import com.aiinterview.model.entity.interview.AIInterview;
import com.aiinterview.mapper.AIInterviewMapper;
import com.aiinterview.service.interview.AIInterviewService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * AI面试服务实现类
 */
@Slf4j
@Service
public class AIInterviewServiceImpl implements AIInterviewService {

    @Autowired
    private AIInterviewMapper aiInterviewMapper;

    @Override
    public Long saveInterview(AIInterview interview) {
        try {
            log.info("保存面试记录，面试ID: {}", interview.getInterviewId());
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            interview.setCreatedAt(now);
            interview.setUpdatedAt(now);

            // 验证application_id是否提供
            if (interview.getApplicationId() == null) {
                log.warn("AI面试记录缺少application_id，这可能导致数据关联问题");
                throw new RuntimeException("面试记录必须关联到具体的申请ID");
            }

            log.info("保存AI面试记录，关联申请ID: {}", interview.getApplicationId());
            
            // 插入记录
            int result = aiInterviewMapper.insert(interview);
            
            if (result > 0) {
                log.info("面试记录保存成功，面试ID: {}", interview.getInterviewId());
                return interview.getInterviewId();
            } else {
                throw new RuntimeException("插入面试记录失败");
            }
            
        } catch (Exception e) {
            log.error("保存面试记录失败，面试ID: {}", interview.getInterviewId(), e);
            throw new RuntimeException("保存面试记录失败: " + e.getMessage());
        }
    }

    @Override
    public AIInterview getByInterviewId(Long interviewId) {
        try {
            log.info("查询面试记录，面试ID: {}", interviewId);
            
            QueryWrapper<AIInterview> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("interview_id", interviewId);
            
            AIInterview interview = aiInterviewMapper.selectOne(queryWrapper);
            
            if (interview != null) {
                log.info("找到面试记录，面试ID: {}", interviewId);
            } else {
                log.info("未找到面试记录，面试ID: {}", interviewId);
            }
            
            return interview;
            
        } catch (Exception e) {
            log.error("查询面试记录失败，面试ID: {}", interviewId, e);
            return null;
        }
    }

    @Override
    public boolean updateInterview(AIInterview interview) {
        try {
            log.info("更新面试记录，面试ID: {}", interview.getInterviewId());
            
            // 设置更新时间
            interview.setUpdatedAt(LocalDateTime.now());
            
            int result = aiInterviewMapper.updateById(interview);
            
            if (result > 0) {
                log.info("面试记录更新成功，面试ID: {}", interview.getInterviewId());
                return true;
            } else {
                log.warn("面试记录更新失败，可能记录不存在，面试ID: {}", interview.getInterviewId());
                return false;
            }
            
        } catch (Exception e) {
            log.error("更新面试记录失败，面试ID: {}", interview.getInterviewId(), e);
            return false;
        }
    }

    @Override
    public boolean updateVideoPath(Long interviewId, String videoPath) {
        try {
            log.info("更新面试视频路径，面试ID: {}, 视频路径: {}", interviewId, videoPath);

            // 查找面试记录
            QueryWrapper<AIInterview> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("interview_id", interviewId);

            AIInterview interview = aiInterviewMapper.selectOne(queryWrapper);
            if (interview == null) {
                log.warn("未找到面试记录，面试ID: {}", interviewId);
                return false;
            }

            // 更新视频路径（由于数据库字段问题，暂时记录到evaluation字段中）
            try {
                String evaluation = interview.getEvaluation();
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

                java.util.Map<String, Object> evalData;
                if (evaluation != null && !evaluation.trim().isEmpty()) {
                    evalData = mapper.readValue(evaluation, java.util.Map.class);
                } else {
                    evalData = new java.util.HashMap<>();
                }

                evalData.put("videoPath", videoPath);
                evalData.put("videoUpdatedAt", java.time.LocalDateTime.now().toString());

                interview.setEvaluation(mapper.writeValueAsString(evalData));
                interview.setUpdatedAt(java.time.LocalDateTime.now());

                int result = aiInterviewMapper.updateById(interview);

                if (result > 0) {
                    log.info("面试视频路径更新成功，面试ID: {}", interviewId);
                    return true;
                } else {
                    log.warn("面试视频路径更新失败，可能记录不存在，面试ID: {}", interviewId);
                    return false;
                }

            } catch (Exception e) {
                log.error("处理视频路径数据失败，面试ID: {}", interviewId, e);
                return false;
            }

        } catch (Exception e) {
            log.error("更新面试视频路径失败，面试ID: {}", interviewId, e);
            return false;
        }
    }
}
