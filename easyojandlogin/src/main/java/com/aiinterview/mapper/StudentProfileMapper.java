package com.aiinterview.mapper;

import com.aiinterview.model.entity.student.StudentProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentProfileMapper extends BaseMapper<StudentProfile> {

    StudentProfile selectByUserId(@Param("userId") Long userId);
}