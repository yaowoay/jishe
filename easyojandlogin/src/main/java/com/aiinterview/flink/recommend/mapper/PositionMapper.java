package com.aiinterview.flink.recommend.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.flink.recommend.entity.Position;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * jobs表Mapper，MyBatis-Plus BaseMapper提供基础CRUD方法
 */
@Mapper
public interface PositionMapper extends BaseMapper<Position> {
    // 联查公司行业，为推荐内容匹配补齐 industry 字段
    // @Select("SELECT " +
    //         "j.job_id AS id, " +
    //         "j.title AS name, " +
    //         "j.job_type AS type, " +
    //         "j.location AS city, " +
    //         "j.min_salary AS salaryMin, " +
    //         "j.max_salary AS salaryMax, " +
    //         "j.job_skills AS skills, " +
    //         "j.education_requirement AS education, " +
    //         "NULL AS workYears, " +
    //         "c.industry AS industry " +
    //         "FROM jobs j " +
    //         "LEFT JOIN companies c ON c.company_id = j.company_id")
    // List<Position> selectAllWithIndustry();
     @Select("SELECT " +
            "j.job_id AS id, " +
            "j.title AS name, " +
            "j.job_type AS type, " +
            "j.location AS city, " +
            "j.min_salary AS salaryMin, " +
            "j.max_salary AS salaryMax, " +
            "j.job_skills AS skills, " +
            "j.education_requirement AS education, " +
            "j.experience_requirement AS workYears, " +
             "c.industry AS industry " +
             "FROM jobs j " +
             "LEFT JOIN companies c ON c.company_id = j.company_id")
    List<Position> selectAllWithIndustry();
}
