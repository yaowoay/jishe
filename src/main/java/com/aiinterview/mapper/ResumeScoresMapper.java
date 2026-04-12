package com.aiinterview.mapper;

import com.aiinterview.model.entity.resume.ResumeScores;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 简历评分Mapper接口
 */
@Mapper
public interface ResumeScoresMapper extends BaseMapper<ResumeScores> {
    
    /**
     * 根据报告ID查询评分
     */
    ResumeScores selectByReportId(@Param("reportId") Long reportId);
}
