package com.aiinterview.repository.interview;

import com.aiinterview.model.entity.interview.InterviewAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面试答案数据访问层
 */
@Mapper
public interface InterviewAnswerRepository extends BaseMapper<InterviewAnswer> {
    
    /**
     * 根据申请ID查询面试答案
     */
    @Select("SELECT * FROM interview_answers WHERE application_id = #{applicationId}")
    InterviewAnswer findByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 根据申请ID和面试结果ID查询面试答案
     */
    @Select("SELECT * FROM interview_answers WHERE application_id = #{applicationId} AND interview_result_id = #{interviewResultId}")
    InterviewAnswer findByApplicationIdAndInterviewResultId(@Param("applicationId") Integer applicationId, 
                                                           @Param("interviewResultId") Long interviewResultId);
    
    /**
     * 根据面试结果ID查询面试答案
     */
    @Select("SELECT * FROM interview_answers WHERE interview_result_id = #{interviewResultId}")
    InterviewAnswer findByInterviewResultId(@Param("interviewResultId") Long interviewResultId);
    
    /**
     * 根据状态查询面试答案列表
     */
    @Select("SELECT * FROM interview_answers WHERE status = #{status} ORDER BY created_at DESC")
    List<InterviewAnswer> findByStatus(@Param("status") String status);
    
    /**
     * 统计已完成的面试数量
     */
    @Select("SELECT COUNT(*) FROM interview_answers WHERE status = 'completed'")
    int countCompletedInterviews();
    
    /**
     * 删除某个申请的所有面试答案
     */
    @Delete("DELETE FROM interview_answers WHERE application_id = #{applicationId}")
    int deleteByApplicationId(@Param("applicationId") Integer applicationId);
}
