package com.aiinterview.repository.professionalTest;

import com.aiinterview.model.entity.professionalTest.TestResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 测试结果数据访问层
 */
@Mapper
public interface TestResultRepository extends BaseMapper<TestResult> {
    
    /**
     * 根据用户ID查找测试结果
     */
    @Select("SELECT * FROM test_results WHERE user_id = #{userId} ORDER BY completed_at DESC")
    List<TestResult> findByUserIdOrderByCompletedAtDesc(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID和类别查找测试结果
     */
    @Select("SELECT * FROM test_results WHERE user_id = #{userId} AND category = #{category} ORDER BY completed_at DESC")
    List<TestResult> findByUserIdAndCategoryOrderByCompletedAtDesc(@Param("userId") Integer userId, @Param("category") String category);
    
    /**
     * 查询用户在指定时间范围内的测试结果
     */
    @Select("SELECT * FROM test_results WHERE user_id = #{userId} AND completed_at BETWEEN #{startTime} AND #{endTime} ORDER BY completed_at DESC")
    List<TestResult> findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(
            @Param("userId") Integer userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询用户测试统计信息
     */
    @Select("SELECT COUNT(*) as totalTests, AVG(score) as averageScore, MAX(score) as bestScore, MIN(score) as lowestScore " +
           "FROM test_results WHERE user_id = #{userId}")
    Map<String, Object> findTestStatisticsByUserId(@Param("userId") Integer userId);
    
    /**
     * 查询用户在指定类别的测试统计信息
     */
    @Select("SELECT COUNT(*) as totalTests, AVG(score) as averageScore, MAX(score) as bestScore, MIN(score) as lowestScore " +
           "FROM test_results WHERE user_id = #{userId} AND category = #{category}")
    Map<String, Object> findTestStatisticsByUserIdAndCategory(@Param("userId") Integer userId, @Param("category") String category);
    
    /**
     * 查询用户最近的测试结果
     */
    @Select("SELECT * FROM test_results WHERE user_id = #{userId} ORDER BY completed_at DESC LIMIT 1")
    TestResult findFirstByUserIdOrderByCompletedAtDesc(@Param("userId") Integer userId);
    
    /**
     * 查询用户在指定类别的最近测试结果
     */
    @Select("SELECT * FROM test_results WHERE user_id = #{userId} AND category = #{category} ORDER BY completed_at DESC LIMIT 1")
    TestResult findFirstByUserIdAndCategoryOrderByCompletedAtDesc(@Param("userId") Integer userId, @Param("category") String category);
    
    /**
     * 统计用户总测试次数
     */
    @Select("SELECT COUNT(*) FROM test_results WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Integer userId);
    
    /**
     * 统计用户在指定类别的测试次数
     */
    @Select("SELECT COUNT(*) FROM test_results WHERE user_id = #{userId} AND category = #{category}")
    long countByUserIdAndCategory(@Param("userId") Integer userId, @Param("category") String category);
    
    /**
     * 查询用户最高分数
     */
    @Select("SELECT MAX(score) FROM test_results WHERE user_id = #{userId}")
    Integer findMaxScoreByUserId(@Param("userId") Integer userId);
    
    /**
     * 查询用户在指定类别的最高分数
     */
    @Select("SELECT MAX(score) FROM test_results WHERE user_id = #{userId} AND category = #{category}")
    Integer findMaxScoreByUserIdAndCategory(@Param("userId") Integer userId, @Param("category") String category);
}
