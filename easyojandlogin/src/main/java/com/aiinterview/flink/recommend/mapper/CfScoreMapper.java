package com.aiinterview.flink.recommend.mapper;

import com.aiinterview.flink.recommend.entity.CfScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 协同过滤分数Mapper：从 MySQL 查询协同过滤分数
 */
@Mapper
public interface CfScoreMapper {

    /**
     * 根据用户ID，查询该用户所有岗位的协同过滤分数
     */
    @Select("SELECT user_id AS studentId, " +
            "job_id AS jobId, " +
            "cf_score AS cfScore, " +
            "create_date AS createDate " +
            "FROM student_job_cf_score " +
            "WHERE user_id = #{studentId}")
    List<CfScore> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 批量查询用户-岗位的协同过滤分数
     */
    @Select("<script>" +
            "SELECT user_id AS studentId, " +
            "job_id AS jobId, " +
            "cf_score AS cfScore, " +
            "create_date AS createDate " +
            "FROM student_job_cf_score " +
            "WHERE user_id = #{studentId} " +
            "AND job_id IN " +
            "<foreach collection='jobIds' item='jobId' open='(' separator=',' close=')'>" +
            "#{jobId}" +
            "</foreach>" +
            "</script>")
    List<CfScore> selectCfScoresByStudentAndJobs(
            @Param("studentId") Long studentId,
            @Param("jobIds") List<Long> jobIds
    );
}