package com.aiinterview.controller;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.entity.JobRecommendation;
import com.aiinterview.service.JobRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 职位推荐Controller
 */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobRecommendationController {

    @Autowired
    private JobRecommendationService jobRecommendationService;

    /**
     * 获取用户的所有推荐职位
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<List<JobRecommendation>> getRecommendationsForUser(
            @PathVariable Integer userId) {
        try {
            List<JobRecommendation> recommendations = jobRecommendationService.getUserRecommendations(userId);
            return ResultUtils.success(recommendations);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取推荐职位失败");
        }
    }

    /**
     * 分页获取用户推荐职位
     */
    @GetMapping("/user/{userId}/page")
    public BaseResponse<List<JobRecommendation>> getRecommendationsByPage(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<JobRecommendation> recommendations = jobRecommendationService.getUserRecommendationsByPage(userId, pageNum, pageSize);
            return ResultUtils.success(recommendations);
        } catch (Exception e) {
            return ResultUtils.error(500, "分页查询推荐职位失败");
        }
    }

    /**
     * 按匹配度排序查询推荐
     */
    @GetMapping("/user/{userId}/sorted")
    public BaseResponse<List<JobRecommendation>> getRecommendationsSorted(
            @PathVariable Integer userId) {
        try {
            List<JobRecommendation> recommendations = jobRecommendationService.getUserRecommendationsSorted(userId);
            return ResultUtils.success(recommendations);
        } catch (Exception e) {
            return ResultUtils.error(500, "按匹配度排序查询失败");
        }
    }

    /**
     * 添加推荐记录
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addRecommendation(@RequestBody JobRecommendation recommendation) {
        try {
            boolean result = jobRecommendationService.addRecommendation(recommendation);
            return ResultUtils.success(result);
        } catch (Exception e) {
            return ResultUtils.error(500, "添加推荐失败");
        }
    }

    /**
     * 更新推荐记录
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> updateRecommendation(@RequestBody JobRecommendation recommendation) {
        try {
            boolean result = jobRecommendationService.updateRecommendation(recommendation);
            return ResultUtils.success(result);
        } catch (Exception e) {
            return ResultUtils.error(500, "更新推荐失败");
        }
    }

    /**
     * 删除推荐记录
     */
    @DeleteMapping("/{recId}")
    public BaseResponse<Boolean> deleteRecommendation(@PathVariable Integer recId) {
        try {
            boolean result = jobRecommendationService.deleteRecommendation(recId);
            return ResultUtils.success(result);
        } catch (Exception e) {
            return ResultUtils.error(500, "删除推荐失败");
        }
    }

    /**
     * 获取推荐总数
     */
    @GetMapping("/user/{userId}/count")
    public BaseResponse<Long> getRecommendationCount(@PathVariable Integer userId) {
        try {
            long count = jobRecommendationService.getRecommendationCount(userId);
            return ResultUtils.success(count);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取推荐总数失败");
        }
    }
}

