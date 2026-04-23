package com.aiinterview.mapper;

import com.aiinterview.model.dto.JobSearchDTO;
import com.aiinterview.model.dto.job.JobDetailDTO;
import com.aiinterview.model.entity.job.Recruitment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 职位搜索Mapper接口
 */
@Mapper
public interface JobSearchMapper {

    /**
     * 根据职位ID列表查询职位（新版 - 基于 jobs + companies 表）
     */
    List<JobSearchDTO> searchJobsByIds(@Param("jobIds") List<Integer> jobIds);

    /**
     * 根据职位ID列表查询职位详情（带公司信息）
     */
    List<JobDetailDTO> selectJobDetailsByIds(@Param("jobIds") List<Integer> jobIds);

    /**
     * 高级筛选查询职位（基于 jobs + companies 表）
     */
    List<JobSearchDTO> searchJobsWithCompany(
            @Param("keyword") String keyword,
            @Param("city") String city,
            @Param("cities") String[] cities,
            @Param("minSalary") Integer minSalary,
            @Param("maxSalary") Integer maxSalary,
            @Param("industry") String industry,
            @Param("experience") String experience,
            @Param("education") String education,
            @Param("companyScale") String companyScale,
            @Param("jobType") String jobType,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 统计高级筛选结果总数（基于 jobs + companies 表）
     */
    int countJobsWithCompany(
            @Param("keyword") String keyword,
            @Param("city") String city,
            @Param("cities") String[] cities,
            @Param("minSalary") Integer minSalary,
            @Param("maxSalary") Integer maxSalary,
            @Param("industry") String industry,
            @Param("experience") String experience,
            @Param("education") String education,
            @Param("companyScale") String companyScale,
            @Param("jobType") String jobType);

    /**
     * 高级筛选查询职位（旧版 - 基于 recruitmenttable）
     */
    List<Recruitment> findRecruitmentsByAdvancedFilters(
            @Param("keyword") String keyword,
            @Param("city") String city,
            @Param("cities") String[] cities,
            @Param("minSalary") Integer minSalary,
            @Param("maxSalary") Integer maxSalary,
            @Param("industry") String industry,
            @Param("experience") String experience,
            @Param("education") String education,
            @Param("companyScale") String companyScale,
            @Param("financingStatus") String financingStatus,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 统计高级筛选结果总数（旧版 - 基于 recruitmenttable）
     */
    int countRecruitmentsByAdvancedFilters(
            @Param("keyword") String keyword,
            @Param("city") String city,
            @Param("cities") String[] cities,
            @Param("minSalary") Integer minSalary,
            @Param("maxSalary") Integer maxSalary,
            @Param("industry") String industry,
            @Param("experience") String experience,
            @Param("education") String education,
            @Param("companyScale") String companyScale,
            @Param("financingStatus") String financingStatus);

    /**
     * 根据职位ID列表查询职位
     */
    List<Recruitment> findRecruitmentsByIds(@Param("jobIds") List<Integer> jobIds);

    /**
     * 根据ID查询职位
     */
    Recruitment findById(@Param("jobId") Integer jobId);

    /**
     * 查询所有职位
     */
    List<Recruitment> findAll();

    /**
     * 查询所有活跃职位（status = 1）
     */
    List<Recruitment> findActiveJobs();
}
