package com.aiinterview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.ProjectExperience;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 项目经验Mapper接口
 */
@Mapper
public interface ProjectExperienceMapper extends BaseMapper<ProjectExperience> {
    
    /**
     * 根据简历ID获取项目经验列表
     */
    List<ProjectExperience> selectByResumeId(@Param("resumeId") Long resumeId);
    
    /**
     * 根据简历ID删除项目经验
     */
    int deleteByResumeId(@Param("resumeId") Long resumeId);
} 