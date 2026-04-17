package com.aiinterview.service;

import com.aiinterview.entity.JobRecommendation;
import java.util.List;

/**
 * 职位推荐Service接口
 */
public interface JobRecommendationService {

    /**
     * 添加推荐
     */
    boolean addRecommendation(JobRecommendation recommendation);

    /**
     * 查询用户推荐列表
     */
    List<JobRecommendation> getUserRecommendations(Integer userId);

    /**
     * 分页查询用户推荐
     */
    List<JobRecommendation> getUserRecommendationsByPage(Integer userId, int pageNum, int pageSize);

    /**
     * 获取用户推荐总数
     */
    long getRecommendationCount(Integer userId);

    /**
     * 按匹配度排序查询推荐
     */
    List<JobRecommendation> getUserRecommendationsSorted(Integer userId);

    /**
     * 删除推荐
     */
    boolean deleteRecommendation(Integer recId);

    /**
     * 更新推荐
     */
    boolean updateRecommendation(JobRecommendation recommendation);
}
