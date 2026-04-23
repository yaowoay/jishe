package com.aiinterview.controller;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.model.dto.job.JobDetailDTO;
import com.aiinterview.model.entity.student.UserJobCollection;
import com.aiinterview.service.JobSearchService;
import com.aiinterview.service.UserJobCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 职位收藏Controller
 */
@RestController
@RequestMapping("/jobs")
@CrossOrigin
public class JobCollectionController {

    @Autowired
    private UserJobCollectionService userJobCollectionService;

    @Autowired
    private JobSearchService jobSearchService;

    /**
     * 收藏职位（支持切换）
     */
    @PostMapping("/collect")
    public BaseResponse<String> collectJob(@RequestBody Map<String, Integer> payload) {
        Integer userId = payload.get("userId");
        Integer jobId = payload.get("jobId");

        if (userId == null || jobId == null) {
            return ResultUtils.error(400, "userId 和 jobId 不能为空");
        }

        try {
            // 检查是否已经收藏
            if (userJobCollectionService.isCollected(userId, jobId)) {
                // 已收藏，则取消收藏
                boolean success = userJobCollectionService.removeCollection(userId, jobId);
                if (success) {
                    return ResultUtils.success("取消收藏成功");
                } else {
                    return ResultUtils.error(500, "取消收藏失败");
                }
            } else {
                // 未收藏，则添加收藏
                boolean success = userJobCollectionService.addCollection(userId, jobId);
                if (success) {
                    return ResultUtils.success("收藏成功");
                } else {
                    return ResultUtils.error(500, "收藏失败");
                }
            }
        } catch (Exception e) {
            return ResultUtils.error(500, "操作失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏职位
     */
    @PostMapping("/cancel-collect")
    public BaseResponse<String> cancelCollectJob(@RequestBody Map<String, Integer> payload) {
        Integer userId = payload.get("userId");
        Integer jobId = payload.get("jobId");

        if (userId == null || jobId == null) {
            return ResultUtils.error(400, "userId 和 jobId 不能为空");
        }

        // 检查是否已收藏
        if (!userJobCollectionService.isCollected(userId, jobId)) {
            return ResultUtils.error(400, "该职位未收藏");
        }

        try {
            boolean success = userJobCollectionService.removeCollection(userId, jobId);
            if (success) {
                return ResultUtils.success("取消收藏成功");
            } else {
                return ResultUtils.error(500, "取消收藏失败");
            }
        } catch (Exception e) {
            return ResultUtils.error(500, "取消收藏失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户收藏的职位列表（新版 - 基于 jobs + companies 表）
     */
    @GetMapping("/collections/{userId}")
    public BaseResponse<List<JobDetailDTO>> getCollectedJobs(@PathVariable Integer userId) {
        try {
            // 查询用户的所有收藏记录
            List<UserJobCollection> collections = userJobCollectionService.getUserCollections(userId);

            if (collections == null || collections.isEmpty()) {
                return ResultUtils.success(new ArrayList<>());
            }

            // 提取所有 jobId
            List<Integer> jobIds = collections.stream()
                    .map(UserJobCollection::getJobId)
                    .collect(Collectors.toList());

            // 根据 jobId 列表查询职位信息（基于 jobs + companies 表）
            List<JobDetailDTO> collectedJobs = jobSearchService.getJobDetailsByIds(jobIds);

            return ResultUtils.success(collectedJobs);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取收藏列表失败: " + e.getMessage());
        }
    }

    /**
     * 检查职位是否已收藏
     */
    @GetMapping("/is-collected")
    public BaseResponse<Boolean> isCollected(
            @RequestParam Integer userId,
            @RequestParam Integer jobId) {
        try {
            boolean collected = userJobCollectionService.isCollected(userId, jobId);
            return ResultUtils.success(collected);
        } catch (Exception e) {
            return ResultUtils.error(500, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户收藏总数
     */
    @GetMapping("/collection-count/{userId}")
    public BaseResponse<Long> getCollectionCount(@PathVariable Integer userId) {
        try {
            long count = userJobCollectionService.getCollectionCount(userId);
            return ResultUtils.success(count);
        } catch (Exception e) {
            return ResultUtils.error(500, "查询失败: " + e.getMessage());
        }
    }
}
