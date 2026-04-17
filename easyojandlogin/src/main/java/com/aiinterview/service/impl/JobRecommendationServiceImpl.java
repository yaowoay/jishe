package com.aiinterview.service.impl;

import com.aiinterview.entity.JobRecommendation;
import com.aiinterview.mapper.JobRecommendationMapper;
import com.aiinterview.service.JobRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 职位推荐Service实现类
 */
@Service
public class JobRecommendationServiceImpl implements JobRecommendationService {

    @Autowired
    private JobRecommendationMapper jobRecommendationMapper;

    @Override
    public boolean addRecommendation(JobRecommendation recommendation) {
        return jobRecommendationMapper.insert(recommendation) > 0;
    }

    @Override
    public List<JobRecommendation> getUserRecommendations(Integer userId) {
        return jobRecommendationMapper.selectByUserId(userId);
    }

    @Override
    public List<JobRecommendation> getUserRecommendationsByPage(Integer userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return jobRecommendationMapper.selectPageByUserId(userId, offset, pageSize);
    }

    @Override
    public long getRecommendationCount(Integer userId) {
        return jobRecommendationMapper.countByUserId(userId);
    }

    @Override
    public List<JobRecommendation> getUserRecommendationsSorted(Integer userId) {
        return jobRecommendationMapper.selectByUserIdOrderByScore(userId);
    }

    @Override
    public boolean deleteRecommendation(Integer recId) {
        return jobRecommendationMapper.delete(recId) > 0;
    }

    @Override
    public boolean updateRecommendation(JobRecommendation recommendation) {
        return jobRecommendationMapper.update(recommendation) > 0;
    }
}
