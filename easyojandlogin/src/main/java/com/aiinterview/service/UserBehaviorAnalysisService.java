package com.aiinterview.service;

import com.aiinterview.entity.UserAction;
import java.util.List;
import java.util.Map;

/**
 * 用户行为分析Service接口
 */
public interface UserBehaviorAnalysisService {

    /**
     * 记录用户行为
     */
    boolean recordUserAction(UserAction action);

    /**
     * 批量记录用户行为
     */
    boolean recordUserActionBatch(List<UserAction> actions);

    /**
     * 获取用户的行为记录
     */
    List<UserAction> getUserActions(Long userId);

    /**
     * 获取用户在时间范围内的行为记录
     */
    List<UserAction> getUserActionsByTimeRange(Long userId, String startTime, String endTime);

    /**
     * 获取用户的行为统计
     */
    Map<String, Object> getUserBehaviorStats(Long userId);

    /**
     * 获取用户的行为类型统计
     */
    Map<String, Long> getUserActionTypeStats(Long userId);

    /**
     * 分析用户行为模式
     */
    Map<String, Object> analyzeUserBehaviorPattern(Long userId, int timeRangeDays);

    /**
     * 获取用户活跃度
     */
    Map<String, Object> getUserActivityStats(Long userId);

    /**
     * 获取用户兴趣标签
     */
    Map<String, Double> getUserInterestTags(Long userId);

    /**
     * 删除用户的行为记录
     */
    boolean deleteUserActions(Long userId);

    /**
     * 获取用户行为总数
     */
    long getUserActionCount(Long userId);
}
