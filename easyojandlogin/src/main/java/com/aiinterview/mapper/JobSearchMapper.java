package com.aiinterview.mapper;

import com.aiinterview.entity.Recruitment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 职位搜索Mapper接口
 */
@Mapper
public interface JobSearchMapper {

    /**
     * 高级筛选查询职位
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
     * 统计高级筛选结果总数
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
}
