package com.aiinterview.repository.interview;

import com.aiinterview.model.entity.interview.InterviewProgress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面试进度数据访问层
 */
@Mapper
public interface InterviewProgressRepository extends BaseMapper<InterviewProgress> {
    
    /**
     * 根据申请ID查询面试进度
     */
    @Select("SELECT * FROM interview_progress WHERE application_id = #{applicationId}")
    InterviewProgress findByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 根据整体状态查询进度列表
     */
    @Select("SELECT * FROM interview_progress WHERE overall_status = #{status} ORDER BY created_at DESC")
    List<InterviewProgress> findByOverallStatus(@Param("status") String status);
    
    /**
     * 根据笔试状态查询进度列表
     */
    @Select("SELECT * FROM interview_progress WHERE written_test_status = #{status} ORDER BY created_at DESC")
    List<InterviewProgress> findByWrittenTestStatus(@Param("status") String status);
    
    /**
     * 根据面试状态查询进度列表
     */
    @Select("SELECT * FROM interview_progress WHERE interview_status = #{status} ORDER BY created_at DESC")
    List<InterviewProgress> findByInterviewStatus(@Param("status") String status);
    
    /**
     * 根据最终结果查询进度列表
     */
    @Select("SELECT * FROM interview_progress WHERE final_result = #{result} ORDER BY created_at DESC")
    List<InterviewProgress> findByFinalResult(@Param("result") String result);
    
    /**
     * 统计各状态的数量
     */
    @Select("SELECT overall_status, COUNT(*) as count FROM interview_progress GROUP BY overall_status")
    List<Object> countByOverallStatus();
    
    /**
     * 获取需要面试的申请列表（笔试已通过）
     */
    @Select("SELECT * FROM interview_progress WHERE written_test_passed = 1 AND interview_status = 'not_started' ORDER BY written_test_completed_at ASC")
    List<InterviewProgress> findReadyForInterview();
}
