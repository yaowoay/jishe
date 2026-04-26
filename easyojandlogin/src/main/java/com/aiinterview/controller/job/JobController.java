package com.aiinterview.controller.job;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.job.JobDetailDTO;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.mapper.CompanyMapper;
import com.aiinterview.service.job.JobService;
import com.aiinterview.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 职位控制器
 */
@Slf4j
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;
    private final JwtUtils jwtUtils;
    private final CompanyMapper companyMapper;

    /**
     * 发布新职位
     */
    @PostMapping("/create")
    public ApiResponse<Job> createJob(@RequestBody Job job, HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            job.setCompanyId(companyId);

            Job createdJob = jobService.createJob(job);
            return ApiResponse.success("职位发布成功", createdJob);
        } catch (Exception e) {
            log.error("发布职位失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新职位信息
     */
    @PutMapping("/update")
    public ApiResponse<Job> updateJob(@RequestBody Job job, HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);

            // 验证职位是否属于当前公司
            Job existingJob = jobService.getJobById(job.getJobId());
            if (existingJob == null || !existingJob.getCompanyId().equals(companyId)) {
                return ApiResponse.error("无权限操作此职位");
            }

            job.setCompanyId(companyId);
            Job updatedJob = jobService.updateJob(job);
            return ApiResponse.success("职位更新成功", updatedJob);
        } catch (Exception e) {
            log.error("更新职位失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取职位详情
     */
    @GetMapping("/{jobId}")
    public ApiResponse<Job> getJobById(@PathVariable Long jobId) {
        try {
            Job job = jobService.getJobById(jobId);
            if (job == null) {
                return ApiResponse.error("职位不存在");
            }
            return ApiResponse.success("获取成功", job);
        } catch (Exception e) {
            log.error("获取职位详情失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取公司的所有职位
     */
    @GetMapping("/company")
    public ApiResponse<List<JobDetailDTO>> getCompanyJobs(HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            List<JobDetailDTO> jobs = jobService.getJobsByCompanyIdWithCompany(companyId);
            return ApiResponse.success("获取成功", jobs);
        } catch (Exception e) {
            log.error("获取公司职位列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 分页获取公司的职位
     */
    @GetMapping("/company/page")
    public ApiResponse<Map<String, Object>> getCompanyJobsWithPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            Page<Job> page = jobService.getJobsByCompanyIdWithPage(companyId, current, size);

            Map<String, Object> result = new HashMap<>();
            result.put("jobs", page.getRecords());
            result.put("total", page.getTotal());
            result.put("current", page.getCurrent());
            result.put("size", page.getSize());
            result.put("pages", page.getPages());

            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            log.error("分页获取公司职位列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 搜索职位（高级搜索）
     */
    @GetMapping("/search")
    public ApiResponse<List<JobDetailDTO>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) String companyScale) {
        try {
            List<JobDetailDTO> jobs = jobService.advancedSearchJobs(
                keyword, jobType, location, city, minSalary, maxSalary,
                industry, experience, education, companyScale
            );
            return ApiResponse.success("搜索成功", jobs);
        } catch (Exception e) {
            log.error("搜索职位失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有职位列表（带公司信息）
     */
    @GetMapping("/list")
    public ApiResponse<List<JobDetailDTO>> getAllJobsList() {
        try {
            List<JobDetailDTO> jobs = jobService.getActiveJobsWithCompany();
            return ApiResponse.success("获取成功", jobs);
        } catch (Exception e) {
            log.error("获取职位列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 切换职位状态
     */
    @PostMapping("/{jobId}/toggle-status")
    public ApiResponse<String> toggleJobStatus(@PathVariable Long jobId, HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            boolean success = jobService.toggleJobStatus(jobId, companyId);

            if (success) {
                return ApiResponse.success("操作成功", "职位状态已更新");
            } else {
                return ApiResponse.error("操作失败");
            }
        } catch (Exception e) {
            log.error("切换职位状态失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除职位
     */
    @DeleteMapping("/{jobId}")
    public ApiResponse<String> deleteJob(@PathVariable Long jobId, HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            boolean success = jobService.deleteJob(jobId, companyId);

            if (success) {
                return ApiResponse.success("删除成功", "职位已删除");
            } else {
                return ApiResponse.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除职位失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有活跃职位（供求职者查看）
     */
    @GetMapping("/active")
    public ApiResponse<List<JobDetailDTO>> getActiveJobs() {
        try {
            List<JobDetailDTO> jobs = jobService.getActiveJobsWithCompany();
            return ApiResponse.success("获取成功", jobs);
        } catch (Exception e) {
            log.error("获取活跃职位失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 分页获取所有活跃职位（供求职者查看）
     */
    @GetMapping("/active/page")
    public ApiResponse<Map<String, Object>> getActiveJobsWithPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) String companyScale,
            @RequestParam(required = false) String applicationStatus,
            @RequestParam(required = false) String submittedJobIds,
            @RequestParam(defaultValue = "postDate") String sortBy) {
        try {
            List<Long> submittedJobIdList = new ArrayList<>();
            if (submittedJobIds != null && !submittedJobIds.trim().isEmpty()) {
                submittedJobIdList = java.util.Arrays.stream(submittedJobIds.split(","))
                        .map(String::trim)
                        .filter(id -> !id.isEmpty())
                        .map(Long::valueOf)
                        .collect(Collectors.toList());
            }

            Page<JobDetailDTO> page = jobService.getActiveJobsWithCompanyPage(
                current,
                size,
                keyword,
                jobType,
                location,
                industry,
                experience,
                education,
                companyScale,
                applicationStatus,
                submittedJobIdList,
                sortBy
            );

            Map<String, Object> result = new HashMap<>();
            result.put("jobs", page.getRecords());
            result.put("total", page.getTotal());
            result.put("current", page.getCurrent());
            result.put("size", page.getSize());
            result.put("pages", page.getPages());

            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            log.error("分页获取活跃职位失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据类型获取职位
     */
    @GetMapping("/type/{jobType}")
    public ApiResponse<List<Job>> getJobsByType(@PathVariable String jobType) {
        try {
            List<Job> jobs = jobService.getJobsByType(jobType);
            return ApiResponse.success("获取成功", jobs);
        } catch (Exception e) {
            log.error("根据类型获取职位失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取即将过期的职位
     */
    @GetMapping("/expiring")
    public ApiResponse<List<Job>> getExpiringJobs(
            @RequestParam(defaultValue = "7") int days,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            List<Job> jobs = jobService.getExpiringJobs(companyId, days);
            return ApiResponse.success("获取成功", jobs);
        } catch (Exception e) {
            log.error("获取即将过期职位失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 从Token中获取公司ID
     */
    private Long getCompanyIdFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("未找到有效的认证信息");
            }

            // 提取token
            token = token.substring(7);

            // 从token中获取用户ID
            Long userId = jwtUtils.getUserIdFromToken(token);

            // 验证用户角色是否为公司
            String role = jwtUtils.getRoleFromToken(token);
            if (!"company".equals(role)) {
                throw new RuntimeException("用户角色不正确，需要企业用户权限");
            }

            // 根据用户ID查询公司ID
            QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            Company company = companyMapper.selectOne(queryWrapper);

            if (company == null) {
                throw new RuntimeException("未找到对应的公司信息");
            }

            return company.getCompanyId();
        } catch (Exception e) {
            log.error("获取公司ID失败: {}", e.getMessage());
            throw new RuntimeException("获取公司信息失败: " + e.getMessage());
        }
    }
}
