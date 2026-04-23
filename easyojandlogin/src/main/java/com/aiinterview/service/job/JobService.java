package com.aiinterview.service.job;

import com.aiinterview.model.dto.job.JobDetailDTO;
import com.aiinterview.model.entity.job.Job;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 职位服务接口
 */
public interface JobService {

    /**
     * 发布新职位
     */
    Job createJob(Job job);

    /**
     * 更新职位信息
     */
    Job updateJob(Job job);

    /**
     * 根据ID获取职位详情
     */
    Job getJobById(Long jobId);

    /**
     * 根据ID获取职位详情（带公司信息）
     */
    JobDetailDTO getJobDetailById(Long jobId);

    /**
     * 获取公司的所有职位
     */
    List<Job> getJobsByCompanyId(Long companyId);

    /**
     * 获取公司的所有职位（带公司信息）
     */
    List<JobDetailDTO> getJobsByCompanyIdWithCompany(Long companyId);

    /**
     * 分页获取公司的职位
     */
    Page<Job> getJobsByCompanyIdWithPage(Long companyId, int current, int size);

    /**
     * 搜索职位
     */
    List<JobDetailDTO> searchJobsWithCompany(String keyword, String jobType, String location);

    /**
     * 高级搜索职位（带公司信息）
     */
    List<JobDetailDTO> advancedSearchJobs(
        String keyword,
        String jobType,
        String location,
        String city,
        Integer minSalary,
        Integer maxSalary,
        String industry,
        String experience,
        String education,
        String companyScale
    );

    /**
     * 搜索职位（仅职位信息）
     */
    List<Job> searchJobs(String keyword, String jobType, String location);

    /**
     * 切换职位状态（激活/停用）
     */
    boolean toggleJobStatus(Long jobId, Long companyId);

    /**
     * 删除职位
     */
    boolean deleteJob(Long jobId, Long companyId);

    /**
     * 获取所有活跃的职位（带公司信息）
     */
    List<JobDetailDTO> getActiveJobsWithCompany();

    /**
     * 获取所有活跃的职位（仅职位信息）
     */
    List<Job> getActiveJobs();

    /**
     * 根据职位类型获取职位
     */
    List<Job> getJobsByType(String jobType);

    /**
     * 获取即将过期的职位
     */
    List<Job> getExpiringJobs(Long companyId, int days);
}
