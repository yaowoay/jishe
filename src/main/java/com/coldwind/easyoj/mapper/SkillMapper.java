package com.coldwind.easyoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coldwind.easyoj.model.entity.Skill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 技能专长Mapper接口
 */
@Mapper
public interface SkillMapper extends BaseMapper<Skill> {
    
    /**
     * 根据简历ID获取技能列表
     */
    List<Skill> selectByResumeId(@Param("resumeId") Long resumeId);
    
    /**
     * 根据简历ID删除技能
     */
    int deleteByResumeId(@Param("resumeId") Long resumeId);
} 