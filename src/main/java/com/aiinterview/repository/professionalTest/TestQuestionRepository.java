package com.aiinterview.repository.professionalTest;

import com.aiinterview.model.entity.professionalTest.TestQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 测试题目数据访问层
 */
@Mapper
public interface TestQuestionRepository extends BaseMapper<TestQuestion> {
    
    /**
     * 根据测试结果ID查询题目列表
     */
    @Select("SELECT * FROM test_questions WHERE test_result_id = #{testResultId} ORDER BY question_id")
    List<TestQuestion> findByTestResultId(@Param("testResultId") Long testResultId);
    
    /**
     * 根据测试结果ID和题目ID查询
     */
    @Select("SELECT * FROM test_questions WHERE test_result_id = #{testResultId} AND question_id = #{questionId}")
    TestQuestion findByTestResultIdAndQuestionId(@Param("testResultId") Long testResultId, @Param("questionId") Integer questionId);
    
    /**
     * 统计测试结果的题目数量
     */
    @Select("SELECT COUNT(*) FROM test_questions WHERE test_result_id = #{testResultId}")
    long countByTestResultId(@Param("testResultId") Long testResultId);
}
