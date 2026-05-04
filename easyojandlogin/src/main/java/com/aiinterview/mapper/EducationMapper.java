package com.aiinterview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.Education;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 教育经历Mapper接口
 */
@Mapper
public interface EducationMapper extends BaseMapper<Education> {
    
    /**
     * 根据简历ID获取教育经历列表
     */
    List<Education> selectByResumeId(@Param("resumeId") Long resumeId);
    
    /**
     * 根据简历ID删除教育经历
     */
    int deleteByResumeId(@Param("resumeId") Long resumeId);
} 