package com.aiinterview.repository.resourceRecomment;

import com.aiinterview.model.entity.resourceRecomment.ResourceRecommendation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源推荐数据访问层
 */
@Mapper
public interface ResourceRecommendationRepository extends BaseMapper<ResourceRecommendation> {
    
    /**
     * 根据评估ID查找推荐资源
     */
    default List<ResourceRecommendation> findByInterviewEvaluationId(Long interviewEvaluationId) {
        QueryWrapper<ResourceRecommendation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interview_evaluation_id", interviewEvaluationId);
        queryWrapper.orderByAsc("priority");
        return selectList(queryWrapper);
    }
    
    /**
     * 根据申请ID查找推荐资源
     */
    default List<ResourceRecommendation> findByApplicationId(Integer applicationId) {
        QueryWrapper<ResourceRecommendation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        queryWrapper.orderByAsc("priority");
        return selectList(queryWrapper);
    }
    
    /**
     * 根据资源类型查找推荐资源
     */
    default List<ResourceRecommendation> findByResourceType(String resourceType) {
        QueryWrapper<ResourceRecommendation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_type", resourceType);
        queryWrapper.orderByAsc("priority");
        return selectList(queryWrapper);
    }
    
    /**
     * 根据改进领域查找推荐资源
     */
    default List<ResourceRecommendation> findByImprovementArea(String improvementArea) {
        QueryWrapper<ResourceRecommendation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("improvement_area", improvementArea);
        queryWrapper.orderByAsc("priority");
        return selectList(queryWrapper);
    }
    
    /**
     * 查找已验证的推荐资源
     */
    default List<ResourceRecommendation> findVerifiedResources() {
        QueryWrapper<ResourceRecommendation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_verified", true);
        queryWrapper.orderByAsc("priority");
        return selectList(queryWrapper);
    }
    
    /**
     * 根据优先级范围查找推荐资源
     */
    default List<ResourceRecommendation> findByPriorityRange(Integer minPriority, Integer maxPriority) {
        QueryWrapper<ResourceRecommendation> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("priority", minPriority, maxPriority);
        queryWrapper.orderByAsc("priority");
        return selectList(queryWrapper);
    }
    
    /**
     * 删除指定评估ID的推荐资源
     */
    default int deleteByInterviewEvaluationId(Long interviewEvaluationId) {
        QueryWrapper<ResourceRecommendation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interview_evaluation_id", interviewEvaluationId);
        return delete(queryWrapper);
    }
    
    /**
     * 删除指定申请ID的推荐资源
     */
    default int deleteByApplicationId(Integer applicationId) {
        QueryWrapper<ResourceRecommendation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        return delete(queryWrapper);
    }
    
    /**
     * 统计指定资源类型的数量
     */
    @Select("SELECT COUNT(*) FROM resource_recommendations WHERE resource_type = #{resourceType}")
    Long countByResourceType(@Param("resourceType") String resourceType);
    
    /**
     * 获取热门推荐资源（按推荐次数排序）
     */
    @Select("SELECT resource_name, resource_type, resource_url, COUNT(*) as recommend_count " +
            "FROM resource_recommendations " +
            "WHERE is_verified = true " +
            "GROUP BY resource_name, resource_type, resource_url " +
            "ORDER BY recommend_count DESC " +
            "LIMIT #{limit}")
    List<ResourceRecommendation> findPopularResources(@Param("limit") int limit);
}
