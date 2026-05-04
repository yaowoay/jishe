package com.aiinterview.service;

import com.aiinterview.model.dto.JobSearchDTO;
import com.aiinterview.model.dto.job.JobDetailDTO;
import com.aiinterview.model.entity.job.Recruitment;
import java.util.List;
import java.util.Map;

/**
 * 职位搜索Service接口
 */
public interface JobSearchService {

    /**
     * 高级筛选查询职位（新版 - 基于 jobs + companies 表）
     */
    Map<String, Object> searchJobsV2(
            String keyword,
            String city,
            String[] cities,
            Integer minSalary,
            Integer maxSalary,
            String industry,
            String experience,
            String education,
            String companyScale,
            String jobType,
            int page,
            int size);

    /**
     * 高级筛选查询职位（旧版 - 基于 recruitmenttable）
     */
    Map<String, Object> searchJobs(
            String keyword,
            String city,
            String[] cities,
            Integer minSalary,
            Integer maxSalary,
            String industry,
            String experience,
            String education,
            String companyScale,
            String financingStatus,
            int page,
            int size);

    /**
     * 根据职位ID列表查询职位（旧版 - 基于 recruitmenttable）
     */
    List<Recruitment> getRecruitmentsByIds(List<Integer> jobIds);

    /**
     * 根据职位ID列表查询职位（新版 - 基于 jobs + companies 表）
     */
    List<JobSearchDTO> getJobsByIds(List<Integer> jobIds);

    /**
     * 根据职位ID列表查询职位详情（带公司信息）
     */
    List<JobDetailDTO> getJobDetailsByIds(List<Integer> jobIds);

    /**
     * 根据ID查询职位
     */
    Recruitment getRecruitmentById(Integer jobId);

    /**
     * 查询所有职位
     */
    List<Recruitment> getAllRecruitments();

    /**
     * 获取所有活跃职位
     */
   //List<Recruitment> getActiveJobs();
}
