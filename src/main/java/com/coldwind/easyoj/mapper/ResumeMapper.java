package com.coldwind.easyoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coldwind.easyoj.model.entity.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 简历Mapper接口
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {
    
    /**
     * 根据用户标识获取简历列表
     */
    List<Resume> selectByUserKey(@Param("userKey") String userKey);
    
    /**
     * 根据分享链接获取简历
     */
    Resume selectByShareUrl(@Param("shareUrl") String shareUrl);
    
    /**
     * 搜索简历
     */
    List<Resume> searchResumes(@Param("keyword") String keyword, @Param("userKey") String userKey);
    
    /**
     * 获取热门简历
     */
    List<Resume> selectPopularResumes(@Param("limit") int limit);
    
    /**
     * 更新查看次数
     */
    int updateViewCount(@Param("id") Long id);
} 