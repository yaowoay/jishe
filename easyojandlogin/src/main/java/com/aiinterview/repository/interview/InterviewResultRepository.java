package com.aiinterview.repository.interview;

import com.aiinterview.model.entity.interview.InterviewResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面试结果数据访问层
 */
@Mapper
public interface InterviewResultRepository extends BaseMapper<InterviewResult> {
    
    /**
     * 根据申请ID查询面试结果
     */
    @Select("SELECT * FROM interview_results WHERE application_id = #{applicationId}")
    InterviewResult findByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 根据简历ID查询面试结果列表
     */
    @Select("SELECT * FROM interview_results WHERE resume_id = #{resumeId} ORDER BY created_at DESC")
    List<InterviewResult> findByResumeId(@Param("resumeId") Long resumeId);
    
    /**
     * 根据生成状态查询面试结果列表
     */
    @Select("SELECT * FROM interview_results WHERE generation_status = #{status} ORDER BY created_at DESC")
    List<InterviewResult> findByGenerationStatus(@Param("status") String status);
    
    /**
     * 统计某个申请的面试次数
     */
    @Select("SELECT COUNT(*) FROM interview_results WHERE application_id = #{applicationId}")
    int countByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 删除某个申请的所有面试结果
     */
    @Select("DELETE FROM interview_results WHERE application_id = #{applicationId}")
    int deleteByApplicationId(@Param("applicationId") Integer applicationId);
}
