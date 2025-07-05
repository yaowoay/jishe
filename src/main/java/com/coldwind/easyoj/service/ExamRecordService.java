package com.coldwind.easyoj.service;

import com.coldwind.easyoj.model.dto.response.ExamRecordDTO;
import java.util.List;

/**
 * 笔试记录服务接口
 */
public interface ExamRecordService {
    
    /**
     * 获取用户的所有笔试记录
     * @param userId 用户ID
     * @return 笔试记录列表
     */
    List<ExamRecordDTO> getUserExamRecords(String userId);
    
    /**
     * 根据taskId获取具体的笔试记录详情
     * @param taskId 考试任务ID
     * @return 笔试记录详情
     */
    ExamRecordDTO getExamRecordByTaskId(String taskId);
    
    /**
     * 获取最近的笔试记录
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近的笔试记录列表
     */
    List<ExamRecordDTO> getRecentExamRecords(String userId, int limit);
    
    /**
     * 获取同一批次生成的考试记录（created_at相差不到1秒的题目）
     * @param userId 用户ID
     * @return 批次考试记录列表
     */
    List<ExamRecordDTO> getBatchExamRecords(String userId);
} 