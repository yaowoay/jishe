package com.aiinterview.repository.interview;

import com.aiinterview.model.entity.interview.InterviewEvaluation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面试评估数据访问层
 */
@Mapper
public interface InterviewEvaluationRepository extends BaseMapper<InterviewEvaluation> {
    
    /**
     * 根据申请ID查找评估记录
     */
    default InterviewEvaluation findByApplicationId(Integer applicationId) {
        QueryWrapper<InterviewEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        queryWrapper.orderByDesc("created_at");
        queryWrapper.last("LIMIT 1");
        return selectOne(queryWrapper);
    }
    
    /**
     * 根据面试ID查找评估记录
     */
    default InterviewEvaluation findByInterviewId(Long interviewId) {
        QueryWrapper<InterviewEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interview_id", interviewId);
        return selectOne(queryWrapper);
    }
    
    /**
     * 根据笔试答案ID查找评估记录
     */
    default InterviewEvaluation findByWrittenTestAnswerId(Long writtenTestAnswerId) {
        QueryWrapper<InterviewEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("written_test_answer_id", writtenTestAnswerId);
        return selectOne(queryWrapper);
    }
    
    /**
     * 根据申请ID和评估状态查找评估记录
     */
    default List<InterviewEvaluation> findByApplicationIdAndStatus(Integer applicationId, String status) {
        QueryWrapper<InterviewEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        queryWrapper.eq("evaluation_status", status);
        queryWrapper.orderByDesc("created_at");
        return selectList(queryWrapper);
    }
    
    /**
     * 根据候选人ID查找所有评估记录
     */
    default List<InterviewEvaluation> findByCandidateId(String candidateId) {
        QueryWrapper<InterviewEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("candidate_id", candidateId);
        queryWrapper.orderByDesc("created_at");
        return selectList(queryWrapper);
    }
    
    /**
     * 删除指定申请ID的评估记录
     */
    default int deleteByApplicationId(Integer applicationId) {
        QueryWrapper<InterviewEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("application_id", applicationId);
        return delete(queryWrapper);
    }
    
    /**
     * 统计指定状态的评估记录数量
     */
    @Select("SELECT COUNT(*) FROM interview_evaluations WHERE evaluation_status = #{status}")
    Long countByStatus(@Param("status") String status);
    
    /**
     * 获取最近的评估记录
     */
    default List<InterviewEvaluation> findRecentEvaluations(int limit) {
        QueryWrapper<InterviewEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");
        queryWrapper.last("LIMIT " + limit);
        return selectList(queryWrapper);
    }
}
