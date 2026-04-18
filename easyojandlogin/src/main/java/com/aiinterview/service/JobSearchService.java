package com.aiinterview.service;

import com.aiinterview.model.entity.job.Recruitment;
import java.util.List;
import java.util.Map;

/**
 * 职位搜索Service接口
 */
public interface JobSearchService {

    /**
     * 高级筛选查询职位
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
     * 根据职位ID列表查询职位
     */
    List<Recruitment> getRecruitmentsByIds(List<Integer> jobIds);

    /**
     * 根据ID查询职位
     */
    Recruitment getRecruitmentById(Integer jobId);

    /**
     * 查询所有职位
     */
    List<Recruitment> getAllRecruitments();
}
