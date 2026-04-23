package com.aiinterview.mapper;

import com.aiinterview.model.dto.job.JobDetailDTO;
import com.aiinterview.model.entity.job.Job;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 职位Mapper接口
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {

    /**
     * 查询活跃职位（带公司信息）
     */
    List<JobDetailDTO> selectActiveJobsWithCompany();

    /**
     * 根据ID查询职位详情（带公司信息）
     */
    JobDetailDTO selectJobDetailById(@Param("jobId") Long jobId);

    /**
     * 搜索职位（带公司信息）
     */
    List<JobDetailDTO> searchJobsWithCompany(
        @Param("keyword") String keyword,
        @Param("jobType") String jobType,
        @Param("location") String location
    );

    /**
     * 高级搜索职位（带公司信息）
     */
    List<JobDetailDTO> advancedSearchJobs(
        @Param("keyword") String keyword,
        @Param("jobType") String jobType,
        @Param("location") String location,
        @Param("city") String city,
        @Param("minSalary") Integer minSalary,
        @Param("maxSalary") Integer maxSalary,
        @Param("industry") String industry,
        @Param("experience") String experience,
        @Param("education") String education,
        @Param("companyScale") String companyScale
    );

    /**
     * 根据公司ID查询职位（带公司信息）
     */
    List<JobDetailDTO> selectJobsByCompanyIdWithCompany(@Param("companyId") Long companyId);
}
