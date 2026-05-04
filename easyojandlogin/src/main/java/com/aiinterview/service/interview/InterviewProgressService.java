package com.aiinterview.service.interview;

import com.aiinterview.model.entity.interview.InterviewProgress;
import com.aiinterview.model.dto.writtenTest.WrittenTestExamDTO;

import java.util.List;

/**
 * 面试进度服务接口
 */
public interface InterviewProgressService {
    
    /**
     * 保存笔试结果到面试进度
     * @param applicationId 申请ID
     * @param testResult 笔试结果
     * @param passed 是否通过
     * @return 面试进度记录
     */
    InterviewProgress saveWrittenTestResult(Integer applicationId, WrittenTestExamDTO.TestResultDTO testResult, Boolean passed);
    
    /**
     * 获取面试进度信息
     * @param applicationId 申请ID
     * @return 面试进度
     */
    InterviewProgress getProgressByApplicationId(Integer applicationId);
    
    /**
     * 更新面试状态
     * @param applicationId 申请ID
     * @param status 面试状态
     * @return 更新结果
     */
    boolean updateInterviewStatus(Integer applicationId, String status);
    
    /**
     * 保存面试结果
     * @param applicationId 申请ID
     * @param interviewResult 面试结果
     * @param passed 是否通过
     * @return 面试进度记录
     */
    InterviewProgress saveInterviewResult(Integer applicationId, Object interviewResult, Boolean passed);
    
    /**
     * 获取所有面试进度列表
     * @return 进度列表
     */
    List<InterviewProgress> getAllProgress();
    
    /**
     * 根据状态获取进度列表
     * @param status 状态
     * @return 进度列表
     */
    List<InterviewProgress> getProgressByStatus(String status);
    
    /**
     * 初始化面试进度记录
     * @param applicationId 申请ID
     * @return 面试进度记录
     */
    InterviewProgress initializeProgress(Integer applicationId);
}
