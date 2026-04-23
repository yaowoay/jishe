package com.aiinterview.service.impl;

import com.aiinterview.model.dto.JobSearchDTO;
import com.aiinterview.model.dto.job.JobDetailDTO;
import com.aiinterview.model.entity.job.Recruitment;
import com.aiinterview.mapper.JobSearchMapper;
import com.aiinterview.service.JobSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 职位搜索Service实现类
 */
@Service
public class JobSearchServiceImpl implements JobSearchService {

    @Autowired
    private JobSearchMapper jobSearchMapper;

    @Override
    public Map<String, Object> searchJobsV2(
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
            int size) {

        // 计算偏移量
        int offset = (page - 1) * size;

        // 查询职位列表（基于 jobs + companies 表）
        List<JobSearchDTO> jobs = jobSearchMapper.searchJobsWithCompany(
                keyword, city, cities, minSalary, maxSalary, industry,
                experience, education, companyScale, jobType, offset, size);

        // 查询总数
        int total = jobSearchMapper.countJobsWithCompany(
                keyword, city, cities, minSalary, maxSalary, industry,
                experience, education, companyScale, jobType);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("jobs", jobs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (int) Math.ceil((double) total / size));

        return result;
    }

    @Override
    public Map<String, Object> searchJobs(
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
            int size) {

        // 计算偏移量
        int offset = (page - 1) * size;

        // 查询职位列表（旧版 - 基于 recruitmenttable）
        List<Recruitment> jobs = jobSearchMapper.findRecruitmentsByAdvancedFilters(
                keyword, city, cities, minSalary, maxSalary, industry,
                experience, education, companyScale, financingStatus, offset, size);

        // 查询总数
        int total = jobSearchMapper.countRecruitmentsByAdvancedFilters(
                keyword, city, cities, minSalary, maxSalary, industry,
                experience, education, companyScale, financingStatus);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("jobs", jobs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (int) Math.ceil((double) total / size));

        return result;
    }

    @Override
    public List<Recruitment> getRecruitmentsByIds(List<Integer> jobIds) {
        return jobSearchMapper.findRecruitmentsByIds(jobIds);
    }

    @Override
    public List<JobSearchDTO> getJobsByIds(List<Integer> jobIds) {
        if (jobIds == null || jobIds.isEmpty()) {
            return new ArrayList<>();
        }
        return jobSearchMapper.searchJobsByIds(jobIds);
    }

    @Override
    public List<JobDetailDTO> getJobDetailsByIds(List<Integer> jobIds) {
        if (jobIds == null || jobIds.isEmpty()) {
            return new ArrayList<>();
        }
        return jobSearchMapper.selectJobDetailsByIds(jobIds);
    }

    @Override
    public Recruitment getRecruitmentById(Integer jobId) {
        return jobSearchMapper.findById(jobId);
    }

    @Override
    public List<Recruitment> getAllRecruitments() {
        return jobSearchMapper.findAll();
    }

    //@Override
    //public List<Recruitment> getActiveJobs() {
        //return jobSearchMapper.findActiveJobs();
  //  }
}
