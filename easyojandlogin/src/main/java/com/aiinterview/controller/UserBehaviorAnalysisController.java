package com.aiinterview.controller;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.model.entity.student.UserAction;
import com.aiinterview.service.UserBehaviorAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 用户行为分析Controller
 */
@RestController
@RequestMapping("/user-behavior-analysis")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserBehaviorAnalysisController {

    @Autowired
    private UserBehaviorAnalysisService userBehaviorAnalysisService;

    /**
     * 记录用户行为
     */
    @PostMapping("/record")
    public BaseResponse<Boolean> recordUserAction(@RequestBody UserAction action) {
        try {
            boolean success = userBehaviorAnalysisService.recordUserAction(action);
            return ResultUtils.success(success);
        } catch (Exception e) {
            return ResultUtils.error(500, "记录用户行为失败");
        }
    }

    /**
     * 批量记录用户行为
     */
    @PostMapping("/batch-record")
    public BaseResponse<Boolean> recordUserActionBatch(@RequestBody List<UserAction> actions) {
        try {
            boolean success = userBehaviorAnalysisService.recordUserActionBatch(actions);
            return ResultUtils.success(success);
        } catch (Exception e) {
            return ResultUtils.error(500, "批量记录用户行为失败");
        }
    }

    /**
     * 获取用户的行为记录
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<List<UserAction>> getUserActions(@PathVariable Long userId) {
        try {
            List<UserAction> actions = userBehaviorAnalysisService.getUserActions(userId);
            return ResultUtils.success(actions);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取用户行为记录失败");
        }
    }

    /**
     * 获取用户在时间范围内的行为记录
     */
    @GetMapping("/user/{userId}/time-range")
    public BaseResponse<List<UserAction>> getUserActionsByTimeRange(
            @PathVariable Long userId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        try {
            List<UserAction> actions = userBehaviorAnalysisService.getUserActionsByTimeRange(userId, startTime, endTime);
            return ResultUtils.success(actions);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取时间范围内的行为记录失败");
        }
    }

    /**
     * 获取用户的行为统计
     */
    @GetMapping("/user/{userId}/stats")
    public BaseResponse<Map<String, Object>> getUserBehaviorStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = userBehaviorAnalysisService.getUserBehaviorStats(userId);
            return ResultUtils.success(stats);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取用户行为统计失败");
        }
    }

    /**
     * 获取用户的行为类型统计
     */
    @GetMapping("/user/{userId}/action-type-stats")
    public BaseResponse<Map<String, Long>> getUserActionTypeStats(@PathVariable Long userId) {
        try {
            Map<String, Long> stats = userBehaviorAnalysisService.getUserActionTypeStats(userId);
            return ResultUtils.success(stats);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取行为类型统计失败");
        }
    }

    /**
     * 分析用户行为模式
     */
    @GetMapping("/user/{userId}/pattern")
    public BaseResponse<Map<String, Object>> analyzeUserBehaviorPattern(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int timeRangeDays) {
        try {
            Map<String, Object> pattern = userBehaviorAnalysisService.analyzeUserBehaviorPattern(userId, timeRangeDays);
            return ResultUtils.success(pattern);
        } catch (Exception e) {
            return ResultUtils.error(500, "分析用户行为模式失败");
        }
    }

    /**
     * 获取用户活跃度统计
     */
    @GetMapping("/user/{userId}/activity-stats")
    public BaseResponse<Map<String, Object>> getUserActivityStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = userBehaviorAnalysisService.getUserActivityStats(userId);
            return ResultUtils.success(stats);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取用户活跃度统计失败");
        }
    }

    /**
     * 获取用户兴趣标签
     */
    @GetMapping("/user/{userId}/interest-tags")
    public BaseResponse<Map<String, Double>> getUserInterestTags(@PathVariable Long userId) {
        try {
            Map<String, Double> tags = userBehaviorAnalysisService.getUserInterestTags(userId);
            return ResultUtils.success(tags);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取用户兴趣标签失败");
        }
    }

    /**
     * 删除用户的行为记录
     */
    @DeleteMapping("/user/{userId}")
    public BaseResponse<Boolean> deleteUserActions(@PathVariable Long userId) {
        try {
            boolean success = userBehaviorAnalysisService.deleteUserActions(userId);
            return ResultUtils.success(success);
        } catch (Exception e) {
            return ResultUtils.error(500, "删除用户行为记录失败");
        }
    }

    /**
     * 获取用户行为总数
     */
    @GetMapping("/user/{userId}/count")
    public BaseResponse<Long> getUserActionCount(@PathVariable Long userId) {
        try {
            long count = userBehaviorAnalysisService.getUserActionCount(userId);
            return ResultUtils.success(count);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取用户行为总数失败");
        }
    }
}
