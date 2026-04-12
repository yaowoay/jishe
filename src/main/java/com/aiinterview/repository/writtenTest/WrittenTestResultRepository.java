package com.aiinterview.repository.writtenTest;

import com.aiinterview.model.entity.writtenTest.WrittenTestResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 笔试结果数据访问层
 */
@Mapper
public interface WrittenTestResultRepository extends BaseMapper<WrittenTestResult> {
    
    /**
     * 根据申请ID查询笔试结果
     */
    @Select("SELECT * FROM written_test_results WHERE application_id = #{applicationId}")
    WrittenTestResult findByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 根据简历ID查询笔试结果列表
     */
    @Select("SELECT * FROM written_test_results WHERE resume_id = #{resumeId} ORDER BY created_at DESC")
    List<WrittenTestResult> findByResumeId(@Param("resumeId") Long resumeId);
    
    /**
     * 根据工作流运行ID查询笔试结果
     */
    @Select("SELECT * FROM written_test_results WHERE workflow_run_id = #{workflowRunId}")
    WrittenTestResult findByWorkflowRunId(@Param("workflowRunId") String workflowRunId);
    
    /**
     * 根据生成状态查询笔试结果列表
     */
    @Select("SELECT * FROM written_test_results WHERE generation_status = #{status} ORDER BY created_at DESC")
    List<WrittenTestResult> findByGenerationStatus(@Param("status") String status);
    
    /**
     * 统计某个申请的笔试结果数量
     */
    @Select("SELECT COUNT(*) FROM written_test_results WHERE application_id = #{applicationId}")
    int countByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 删除某个申请的所有笔试结果
     */
    @Select("DELETE FROM written_test_results WHERE application_id = #{applicationId}")
    int deleteByApplicationId(@Param("applicationId") Integer applicationId);
}
