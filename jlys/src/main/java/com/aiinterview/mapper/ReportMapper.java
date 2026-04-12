package com.aiinterview.mapper;

import com.aiinterview.model.entity.report.Report;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 报告Mapper接口
 */
@Mapper
public interface ReportMapper extends BaseMapper<Report> {
    
    /**
     * 根据简历ID和职位ID查询报告
     */
    Report selectByResumeIdAndJobId(@Param("resumeId") Long resumeId, @Param("jobId") Long jobId);
}
