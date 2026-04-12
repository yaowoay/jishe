package com.aiinterview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.AdditionalInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 其他信息Mapper接口
 */
@Mapper
public interface AdditionalInfoMapper extends BaseMapper<AdditionalInfo> {
    
    /**
     * 根据简历ID获取其他信息列表
     */
    List<AdditionalInfo> selectByResumeId(@Param("resumeId") Long resumeId);
    
    /**
     * 根据简历ID删除其他信息
     */
    int deleteByResumeId(@Param("resumeId") Long resumeId);
} 