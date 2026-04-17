package com.aiinterview.controller;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.service.JobSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 职位搜索Controller
 */
@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobSearchController {

    @Autowired
    private JobSearchService jobSearchService;

    /**
     * 职位搜索与筛选（支持高级筛选和分页）
     */
    @GetMapping("/search")
    public BaseResponse<Map<String, Object>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String[] cities,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) String companyScale,
            @RequestParam(required = false) String financingStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Map<String, Object> result = jobSearchService.searchJobs(
                    keyword, city, cities, minSalary, maxSalary, industry,
                    experience, education, companyScale, financingStatus, page, size);
            return ResultUtils.success(result);
        } catch (Exception e) {
            return ResultUtils.error(500, "搜索失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取职位详情
     */
    @GetMapping("/{jobId}")
    public BaseResponse<?> getJobDetail(@PathVariable Integer jobId) {
        try {
            return ResultUtils.success(jobSearchService.getRecruitmentById(jobId));
        } catch (Exception e) {
            return ResultUtils.error(500, "获取职位详情失败");
        }
    }

    /**
     * 获取所有职位
     */
    @GetMapping("/list")
    public BaseResponse<?> getAllJobs() {
        try {
            return ResultUtils.success(jobSearchService.getAllRecruitments());
        } catch (Exception e) {
            return ResultUtils.error(500, "获取职位列表失败");
        }
    }
}
