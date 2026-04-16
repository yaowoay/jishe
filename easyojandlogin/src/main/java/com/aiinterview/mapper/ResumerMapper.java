package com.aiinterview.mapper;

import com.aiinterview.model.entity.resumer; // P0: 实体类修正
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 简历 Mapper 接口 (已修正版)
 */
@Mapper
public interface ResumerMapper extends BaseMapper<resumer> { // P0: resumer -> Resume

    /**
     * 根据用户ID获取简历列表
     * P0: userKey -> userId (Long)
     */
    List<resumer> selectByUserId(@Param("userId") Integer userId);

    /**
     * 根据分享码获取简历
     * P0: shareUrl -> shareCode
     */
    resumer selectByShareCode(@Param("shareCode") String shareCode);

    /**
     * 搜索简历
     * P0: 参数修正，与 Service 层保持一致
     */
    List<resumer> searchResumes(@Param("keyword") String keyword, @Param("userId")Integer userId);

    /**
     * 获取热门简历
     */
    List<resumer> selectPopularResumes(@Param("limit") int limit);

    /**
     * 更新查看次数
     */
    int updateViewCount(@Param("id") Long id);

    resumer selectDefaultByUserId(Long userId);
}