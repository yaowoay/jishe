package com.aiinterview.service.interview;

import com.aiinterview.model.entity.interview.AIInterview;

/**
 * AI面试服务接口
 */
public interface AIInterviewService {
    
    /**
     * 保存面试记录
     * @param interview 面试记录
     * @return 保存后的ID
     */
    Long saveInterview(AIInterview interview);
    
    /**
     * 根据面试ID获取面试记录
     * @param interviewId 面试ID
     * @return 面试记录
     */
    AIInterview getByInterviewId(Long interviewId);
    
    /**
     * 更新面试记录
     * @param interview 面试记录
     * @return 是否更新成功
     */
    boolean updateInterview(AIInterview interview);

    /**
     * 更新面试视频路径
     * @param interviewId 面试ID
     * @param videoPath 视频路径
     * @return 是否更新成功
     */
    boolean updateVideoPath(Long interviewId, String videoPath);
}
