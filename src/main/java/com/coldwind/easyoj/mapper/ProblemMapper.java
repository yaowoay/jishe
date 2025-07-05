package com.coldwind.easyoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coldwind.easyoj.model.entity.Problem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 题目Mapper
 */
@Mapper
public interface ProblemMapper extends BaseMapper<Problem> {
    
    /**
     * 更新题目收藏状态
     */
    @Update("UPDATE problem SET is_favorite = #{isFavorite} WHERE id = #{problemId}")
    int updateFavoriteStatus(@Param("problemId") Integer problemId, @Param("isFavorite") Boolean isFavorite);
    
    /**
     * 更新题目状态
     */
    @Update("UPDATE problem SET status = #{status} WHERE id = #{problemId}")
    int updateStatus(@Param("problemId") Integer problemId, @Param("status") String status);
}