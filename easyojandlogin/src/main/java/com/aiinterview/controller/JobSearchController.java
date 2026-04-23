//package com.aiinterview.controller;
//
//import com.aiinterview.common.BaseResponse;
//import com.aiinterview.common.ResultUtils;
//import com.aiinterview.service.JobSearchService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import java.util.Map;
//
///**
// * 职位搜索Controller（旧版 - 基于 recruitmenttable）
// * 已废弃，请使用 JobController
// */
//@RestController
//@RequestMapping("/jobs-legacy")
//@CrossOrigin(origins = "*", maxAge = 3600)
//@Deprecated
//public class JobSearchController {
//
//    @Autowired
//    private JobSearchService jobSearchService;
//
//    /**
//     * 职位搜索与筛选（新版 - 基于 jobs + companies 表）
//     * 支持高级筛选和分页
//     */
//    @GetMapping("/search")
//    public BaseResponse<Map<String, Object>> searchJobs(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) String city,
//            @RequestParam(required = false) String[] cities,
//            @RequestParam(required = false) Integer minSalary,
//            @RequestParam(required = false) Integer maxSalary,
//            @RequestParam(required = false) String industry,
//            @RequestParam(required = false) String experience,
//            @RequestParam(required = false) String education,
//            @RequestParam(required = false) String companyScale,
//            @RequestParam(required = false) String jobType,
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        try {
//            // 使用新版 V2 接口（基于 jobs + companies 表）
//            Map<String, Object> result = jobSearchService.searchJobsV2(
//                    keyword, city, cities, minSalary, maxSalary, industry,
//                    experience, education, companyScale, jobType, page, size);
//            return ResultUtils.success(result);
//        } catch (Exception e) {
//            return ResultUtils.error(500, "搜索失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 职位搜索与筛选（旧版 - 基于 recruitmenttable）
//     * 保留用于兼容性
//     */
//    @GetMapping("/search/legacy")
//    public BaseResponse<Map<String, Object>> searchJobsLegacy(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) String city,
//            @RequestParam(required = false) String[] cities,
//            @RequestParam(required = false) Integer minSalary,
//            @RequestParam(required = false) Integer maxSalary,
//            @RequestParam(required = false) String industry,
//            @RequestParam(required = false) String experience,
//            @RequestParam(required = false) String education,
//            @RequestParam(required = false) String companyScale,
//            @RequestParam(required = false) String financingStatus,
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        try {
//            Map<String, Object> result = jobSearchService.searchJobs(
//                    keyword, city, cities, minSalary, maxSalary, industry,
//                    experience, education, companyScale, financingStatus, page, size);
//            return ResultUtils.success(result);
//        } catch (Exception e) {
//            return ResultUtils.error(500, "搜索失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取所有活跃职位
//     * 注意：必须放在 /{jobId} 之前，避免路径冲突
//     */
//    @GetMapping("/active")
//    public BaseResponse<?> getActiveJobs() {
//        try {
//            return ResultUtils.success(jobSearchService.getActiveJobs());
//        } catch (Exception e) {
//            return ResultUtils.error(500, "获取活跃职位失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取所有职位
//     */
//    @GetMapping("/list")
//    public BaseResponse<?> getAllJobs() {
//        try {
//            return ResultUtils.success(jobSearchService.getAllRecruitments());
//        } catch (Exception e) {
//            return ResultUtils.error(500, "获取职位列表失败");
//        }
//    }
//
//    /**
//     * 根据ID获取职位详情
//     * 注意：必须放在具体路径（如 /active, /list）之后
//     */
//    @GetMapping("/{jobId}")
//    public BaseResponse<?> getJobDetail(@PathVariable Integer jobId) {
//        try {
//            return ResultUtils.success(jobSearchService.getRecruitmentById(jobId));
//        } catch (Exception e) {
//            return ResultUtils.error(500, "获取职位详情失败");
//        }
//    }
//}
