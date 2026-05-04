package com.aiinterview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.WorkExperience;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 工作经历Mapper接口
 */
@Mapper
public interface WorkExperienceMapper extends BaseMapper<WorkExperience> {
    
    /**
     * 根据简历ID获取工作经历列表
     */
    List<WorkExperience> selectByResumeId(@Param("resumeId") Long resumeId);
    
    /**
     * 根据简历ID删除工作经历
     */
    int deleteByResumeId(@Param("resumeId") Long resumeId);
} 