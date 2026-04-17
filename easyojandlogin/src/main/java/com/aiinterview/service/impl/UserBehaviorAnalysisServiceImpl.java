package com.aiinterview.service.impl;

import com.aiinterview.entity.UserAction;
import com.aiinterview.mapper.UserActionMapper;
import com.aiinterview.service.UserBehaviorAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户行为分析Service实现类
 */
@Service
public class UserBehaviorAnalysisServiceImpl implements UserBehaviorAnalysisService {

    @Autowired
    private UserActionMapper userActionMapper;

    @Override
    public boolean recordUserAction(UserAction action) {
        if (action.getActionTime() == null) {
            action.setActionTime(LocalDateTime.now());
        }
        return userActionMapper.insert(action) > 0;
    }

    @Override
    public boolean recordUserActionBatch(List<UserAction> actions) {
        return userActionMapper.insertBatch(actions) > 0;
    }

    @Override
    public List<UserAction> getUserActions(Long userId) {
        return userActionMapper.selectByUserId(userId);
    }

    @Override
    public List<UserAction> getUserActionsByTimeRange(Long userId, String startTime, String endTime) {
        return userActionMapper.selectByUserIdAndTimeRange(userId, startTime, endTime);
    }

    @Override
    public Map<String, Object> getUserBehaviorStats(Long userId) {
        List<UserAction> actions = userActionMapper.selectByUserId(userId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActions", actions.size());
        stats.put("actionTypes", actions.stream()
                .map(UserAction::getActionType)
                .distinct()
                .collect(Collectors.toList()));
        return stats;
    }

    @Override
    public Map<String, Long> getUserActionTypeStats(Long userId) {
        List<UserAction> actions = userActionMapper.selectByUserId(userId);
        return actions.stream()
                .collect(Collectors.groupingBy(UserAction::getActionType, Collectors.counting()));
    }

    @Override
    public Map<String, Object> analyzeUserBehaviorPattern(Long userId, int timeRangeDays) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(timeRangeDays);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        List<UserAction> actions = userActionMapper.selectByUserIdAndTimeRange(
                userId, startTime.format(formatter), endTime.format(formatter));
        
        Map<String, Object> pattern = new HashMap<>();
        pattern.put("timeRange", timeRangeDays + " days");
        pattern.put("totalActions", actions.size());
        pattern.put("actionTypeDistribution", actions.stream()
                .collect(Collectors.groupingBy(UserAction::getActionType, Collectors.counting())));
        
        return pattern;
    }

    @Override
    public Map<String, Object> getUserActivityStats(Long userId) {
        List<UserAction> actions = userActionMapper.selectByUserId(userId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActions", actions.size());
        stats.put("lastActionTime", actions.isEmpty() ? null : actions.get(0).getActionTime());
        stats.put("actionTypeCount", actions.stream()
                .collect(Collectors.groupingBy(UserAction::getActionType, Collectors.counting())));
        return stats;
    }

    @Override
    public Map<String, Double> getUserInterestTags(Long userId) {
        List<UserAction> actions = userActionMapper.selectByUserId(userId);
        Map<String, Long> actionCounts = actions.stream()
                .collect(Collectors.groupingBy(UserAction::getActionTarget, Collectors.counting()));
        
        Map<String, Double> interestTags = new HashMap<>();
        long totalActions = actions.size();
        actionCounts.forEach((target, count) -> 
            interestTags.put(target, (double) count / totalActions)
        );
        return interestTags;
    }

    @Override
    public boolean deleteUserActions(Long userId) {
        return userActionMapper.deleteByUserId(userId) > 0;
    }

    @Override
    public long getUserActionCount(Long userId) {
        return userActionMapper.countByUserId(userId);
    }
}
