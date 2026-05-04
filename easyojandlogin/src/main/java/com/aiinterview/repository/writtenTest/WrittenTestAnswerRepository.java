package com.aiinterview.repository.writtenTest;

import com.aiinterview.model.entity.writtenTest.WrittenTestAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 笔试答案数据访问层
 */
@Mapper
public interface WrittenTestAnswerRepository extends BaseMapper<WrittenTestAnswer> {
    
    /**
     * 根据申请ID查询笔试答案
     */
    @Select("SELECT * FROM written_test_answers WHERE application_id = #{applicationId}")
    WrittenTestAnswer findByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 根据申请ID和笔试结果ID查询答案
     */
    @Select("SELECT * FROM written_test_answers WHERE application_id = #{applicationId} AND written_test_result_id = #{testResultId}")
    WrittenTestAnswer findByApplicationIdAndTestResultId(@Param("applicationId") Integer applicationId, 
                                                        @Param("testResultId") Long testResultId);
    
    /**
     * 根据笔试结果ID查询所有答案
     */
    @Select("SELECT * FROM written_test_answers WHERE written_test_result_id = #{testResultId} ORDER BY created_at DESC")
    List<WrittenTestAnswer> findByTestResultId(@Param("testResultId") Long testResultId);
    
    /**
     * 根据状态查询答案列表
     */
    @Select("SELECT * FROM written_test_answers WHERE status = #{status} ORDER BY created_at DESC")
    List<WrittenTestAnswer> findByStatus(@Param("status") String status);
    
    /**
     * 统计某个申请的答题次数
     */
    @Select("SELECT COUNT(*) FROM written_test_answers WHERE application_id = #{applicationId}")
    int countByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 获取某个申请的最高分
     */
    @Select("SELECT MAX(score) FROM written_test_answers WHERE application_id = #{applicationId} AND status = 'completed'")
    Double getMaxScoreByApplicationId(@Param("applicationId") Integer applicationId);
    
    /**
     * 删除某个申请的所有答题记录
     */
    @Select("DELETE FROM written_test_answers WHERE application_id = #{applicationId}")
    int deleteByApplicationId(@Param("applicationId") Integer applicationId);
}
