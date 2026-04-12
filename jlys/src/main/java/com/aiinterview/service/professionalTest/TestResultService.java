package com.aiinterview.service.professionalTest;

import com.aiinterview.model.dto.professionalTest.TestResultDTO;
import com.aiinterview.model.entity.professionalTest.TestResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测试结果服务接口
 */
public interface TestResultService {
    
    /**
     * 保存测试结果
     * @param testResultDTO 测试结果数据
     * @return 保存后的测试结果
     */
    TestResult saveTestResult(TestResultDTO testResultDTO);
    
    /**
     * 根据ID查找测试结果
     * @param id 测试结果ID
     * @return 测试结果
     */
    Optional<TestResult> findById(Long id);
    
    /**
     * 根据用户ID查找测试历史
     * @param userId 用户ID
     * @return 测试结果列表
     */
    List<TestResult> findByUserId(Integer userId);
    
    /**
     * 根据用户ID和类别查找测试历史
     * @param userId 用户ID
     * @param category 测试类别
     * @return 测试结果列表
     */
    List<TestResult> findByUserIdAndCategory(Integer userId, String category);
    
    /**
     * 分页查询用户测试历史
     * @param userId 用户ID
     * @param page 分页对象
     * @return 分页测试结果
     */
    Page<TestResult> findByUserIdWithPagination(Integer userId, Page<TestResult> page);

    /**
     * 根据用户ID和类别分页查询
     * @param userId 用户ID
     * @param category 测试类别
     * @param page 分页对象
     * @return 分页测试结果
     */
    Page<TestResult> findByUserIdAndCategoryWithPagination(Integer userId, String category, Page<TestResult> page);
    
    /**
     * 查询用户在指定时间范围内的测试结果
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 测试结果列表
     */
    List<TestResult> findByUserIdAndTimeRange(Integer userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取用户测试统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getTestStatistics(Integer userId);
    
    /**
     * 获取用户在指定类别的测试统计信息
     * @param userId 用户ID
     * @param category 测试类别
     * @return 统计信息
     */
    Map<String, Object> getTestStatisticsByCategory(Integer userId, String category);
    
    /**
     * 获取用户最近的测试结果
     * @param userId 用户ID
     * @return 最近的测试结果
     */
    Optional<TestResult> getLatestTestResult(Integer userId);
    
    /**
     * 获取用户在指定类别的最近测试结果
     * @param userId 用户ID
     * @param category 测试类别
     * @return 最近的测试结果
     */
    Optional<TestResult> getLatestTestResultByCategory(Integer userId, String category);
    
    /**
     * 删除测试结果
     * @param id 测试结果ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否删除成功
     */
    boolean deleteTestResult(Long id, Integer userId);
    
    /**
     * 批量删除测试结果
     * @param ids 测试结果ID列表
     * @param userId 用户ID（用于权限验证）
     * @return 删除成功的数量
     */
    int batchDeleteTestResults(List<Long> ids, Integer userId);
    
    /**
     * 获取用户测试总次数
     * @param userId 用户ID
     * @return 测试总次数
     */
    long getTotalTestCount(Integer userId);
    
    /**
     * 获取用户在指定类别的测试次数
     * @param userId 用户ID
     * @param category 测试类别
     * @return 测试次数
     */
    long getTestCountByCategory(Integer userId, String category);
    
    /**
     * 获取用户最高分数
     * @param userId 用户ID
     * @return 最高分数
     */
    Integer getMaxScore(Integer userId);
    
    /**
     * 获取用户在指定类别的最高分数
     * @param userId 用户ID
     * @param category 测试类别
     * @return 最高分数
     */
    Integer getMaxScoreByCategory(Integer userId, String category);
}
