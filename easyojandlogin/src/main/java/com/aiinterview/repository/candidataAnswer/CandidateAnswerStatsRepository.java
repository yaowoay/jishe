package com.aiinterview.repository.candidataAnswer;

import com.aiinterview.model.entity.candidataAnswer.CandidateAnswerStats;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 应聘者答题统计信息数据访问层
 */
@Mapper
public interface CandidateAnswerStatsRepository extends BaseMapper<CandidateAnswerStats> {
    
    /**
     * 根据申请ID查询答题统计信息
     */
    @Select("SELECT * FROM candidate_answer_stats WHERE application_id = #{applicationId}")
    CandidateAnswerStats findByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 根据申请ID列表查询答题统计信息
     */
    @Select("<script>" +
            "SELECT * FROM candidate_answer_stats WHERE application_id IN " +
            "<foreach collection='applicationIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<CandidateAnswerStats> findByApplicationIds(@Param("applicationIds") List<Integer> applicationIds);
    
    /**
     * 查询有笔试设置的记录
     */
    @Select("SELECT * FROM candidate_answer_stats WHERE written_answer_count > 0 ORDER BY created_at DESC")
    List<CandidateAnswerStats> findWithWrittenTestSettings();
    
    /**
     * 查询有面试设置的记录
     */
    @Select("SELECT * FROM candidate_answer_stats WHERE interview_answer_count > 0 ORDER BY created_at DESC")
    List<CandidateAnswerStats> findWithInterviewSettings();
    
    /**
     * 根据申请ID删除记录
     */
    @Delete("DELETE FROM candidate_answer_stats WHERE application_id = #{applicationId}")
    int deleteByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 统计笔试设置数量
     */
    @Select("SELECT COUNT(*) FROM candidate_answer_stats WHERE written_answer_count > 0")
    long countWithWrittenTestSettings();
    
    /**
     * 统计面试设置数量
     */
    @Select("SELECT COUNT(*) FROM candidate_answer_stats WHERE interview_answer_count > 0")
    long countWithInterviewSettings();
}
