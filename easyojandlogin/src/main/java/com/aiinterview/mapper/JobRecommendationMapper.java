package com.aiinterview.mapper;

import com.aiinterview.model.entity.JobRecommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 职位推荐Mapper接口
 */
@Mapper
public interface JobRecommendationMapper {

    /**
     * 添加推荐
     */
    int insert(JobRecommendation recommendation);

    /**
     * 查询用户推荐列表
     */
    List<JobRecommendation> selectByUserId(@Param("userId") Integer userId);

    /**
     * 分页查询用户推荐
     */
    List<JobRecommendation> selectPageByUserId(@Param("userId") Integer userId,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    /**
     * 获取用户推荐总数
     */
    long countByUserId(@Param("userId") Integer userId);

    /**
     * 根据匹配度排序查询
     */
    List<JobRecommendation> selectByUserIdOrderByScore(@Param("userId") Integer userId);

    /**
     * 删除推荐
     */
    int delete(@Param("recId") Integer recId);

    /**
     * 更新推荐
     */
    int update(JobRecommendation recommendation);
}
