package org.iflyproject.springdemos.mapper.mybatis;

import org.apache.ibatis.annotations.*;
import org.iflyproject.springdemos.domain.Resume;

import java.util.List;

/**
 * ResumeMapper
 * 用于操作简历相关的数据库表，包括插入简历、查询单个简历和查询所有简历。
 */
@Mapper
public interface ResumeMapper {

    /**
     * 插入简历记录
     * @param fileName 简历文件名
     * @param filePath 简历文件路径
     * @param uploadTime 上传时间
     * @return 插入的记录数
     */
    @Insert("INSERT INTO resumes (file_name, file_path, upload_time) VALUES (#{fileName}, #{filePath}, #{uploadTime})")
    int insertResume(@Param("fileName") String fileName, @Param("filePath") String filePath, @Param("uploadTime") String uploadTime);

    /**
     * 根据 ID 查询简历
     * @param id 简历 ID
     * @return 简历对象
     */
    @Select("SELECT * FROM resumes WHERE id = #{id}")
    Resume selectResumeById(@Param("id") Long id);

    /**
     * 查询所有简历
     * @return 简历列表
     */
    @Select("SELECT * FROM resumes")
    List<Resume> selectAllResumes();
}