package com.aiinterview.service.writtenTest;

import com.aiinterview.model.dto.writtenTest.WrittenTestGenerationRequest;
import com.aiinterview.model.dto.writtenTest.WrittenTestGenerationResponse;
import com.aiinterview.model.entity.writtenTest.WrittenTestResult;

import java.util.List;

/**
 * 笔试生成服务接口
 */
public interface WrittenTestGenerationService {
    
    /**
     * 生成笔试题目
     * @param request 笔试生成请求
     * @return 笔试生成响应
     */
    WrittenTestGenerationResponse generateWrittenTest(WrittenTestGenerationRequest request);

    /**
     * 根据申请ID生成笔试题目（自动获取应聘者简历）
     * @param applicationId 申请ID
     * @return 笔试生成响应
     */
    WrittenTestGenerationResponse generateWrittenTestByApplicationId(Integer applicationId);
    
    /**
     * 根据申请ID获取笔试结果
     * @param applicationId 申请ID
     * @return 笔试结果
     */
    WrittenTestResult getByApplicationId(Integer applicationId);
    
    /**
     * 根据简历ID获取笔试结果列表
     * @param resumeId 简历ID
     * @return 笔试结果列表
     */
    List<WrittenTestResult> getByResumeId(Long resumeId);
    
    /**
     * 根据ID获取笔试结果详情（包含解析后的题目）
     * @param id 笔试结果ID
     * @return 笔试生成响应
     */
    WrittenTestGenerationResponse getDetailById(Long id);
    
    /**
     * 更新笔试结果状态
     * @param id 笔试结果ID
     * @param status 新状态
     * @param errorMessage 错误信息（可选）
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, String status, String errorMessage);
    
    /**
     * 删除笔试结果
     * @param applicationId 申请ID
     * @return 是否删除成功
     */
    boolean deleteByApplicationId(Integer applicationId);
    
    /**
     * 重新生成笔试题目
     * @param applicationId 申请ID
     * @return 笔试生成响应
     */
    WrittenTestGenerationResponse regenerateWrittenTest(Integer applicationId);
}
