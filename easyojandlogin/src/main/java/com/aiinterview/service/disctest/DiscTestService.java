package com.aiinterview.service.disctest;

import com.aiinterview.model.dto.disctest.DiscTestRequest;
import com.aiinterview.model.dto.disctest.DiscTestResponse;

import java.util.List;

/**
 * DISC测试服务接口
 */
public interface DiscTestService {
    
    /**
     * 开始DISC测试，获取题目
     * 
     * @param userId 用户ID（可选）
     * @return 测试响应，包含题目和测试会话ID
     */
    DiscTestResponse startTest(Long userId);
    
    /**
     * 提交DISC测试答案并获取结果
     * 
     * @param request 测试请求，包含答案
     * @return 测试响应，包含分析结果
     */
    DiscTestResponse submitTest(DiscTestRequest request);
    
    /**
     * 根据测试会话ID获取测试结果
     * 
     * @param testSession 测试会话ID
     * @return 测试结果
     */
    DiscTestResponse getTestResult(String testSession);
    
    /**
     * 根据用户ID获取最新的测试结果
     * 
     * @param userId 用户ID
     * @return 测试结果
     */
    DiscTestResponse getLatestTestResult(Long userId);
    
    /**
     * 计算DISC得分
     *
     * @param request 测试请求
     * @return DISC各类型得分
     */
    DiscTestResponse.DiscResult calculateDiscScores(DiscTestRequest request);

    /**
     * 获取用户的DISC测试历史记录
     *
     * @param userId 用户ID
     * @return 测试历史记录列表
     */
    List<DiscTestResponse> getTestHistory(Long userId);
}
