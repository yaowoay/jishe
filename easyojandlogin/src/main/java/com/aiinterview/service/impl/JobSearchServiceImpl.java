package com.aiinterview.service.impl;

import com.aiinterview.entity.Recruitment;
import com.aiinterview.mapper.JobSearchMapper;
import com.aiinterview.service.JobSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

        // 查询职位列表
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
    public Recruitment getRecruitmentById(Integer jobId) {
        return jobSearchMapper.findById(jobId);
    }

    @Override
    public List<Recruitment> getAllRecruitments() {
        return jobSearchMapper.findAll();
    }
}
