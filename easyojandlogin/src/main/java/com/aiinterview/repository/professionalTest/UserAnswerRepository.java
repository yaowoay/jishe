package com.aiinterview.repository.professionalTest;

import com.aiinterview.model.entity.professionalTest.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户答题记录数据访问层
 */
@Mapper
public interface UserAnswerRepository extends BaseMapper<UserAnswer> {
    
    /**
     * 根据测试结果ID查询答题记录
     */
    @Select("SELECT * FROM user_answers WHERE test_result_id = #{testResultId} ORDER BY question_id")
    List<UserAnswer> findByTestResultId(@Param("testResultId") Long testResultId);
    
    /**
     * 根据测试结果ID和题目ID查询答题记录
     */
    @Select("SELECT * FROM user_answers WHERE test_result_id = #{testResultId} AND question_id = #{questionId}")
    UserAnswer findByTestResultIdAndQuestionId(@Param("testResultId") Long testResultId, @Param("questionId") Integer questionId);
    
    /**
     * 统计正确答案数量
     */
    @Select("SELECT COUNT(*) FROM user_answers WHERE test_result_id = #{testResultId} AND is_correct = true")
    long countCorrectAnswersByTestResultId(@Param("testResultId") Long testResultId);
    
    /**
     * 统计答题记录数量
     */
    @Select("SELECT COUNT(*) FROM user_answers WHERE test_result_id = #{testResultId}")
    long countByTestResultId(@Param("testResultId") Long testResultId);
}
