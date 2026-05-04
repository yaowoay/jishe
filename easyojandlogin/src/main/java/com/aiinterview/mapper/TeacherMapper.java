package com.aiinterview.mapper;

import com.aiinterview.model.entity.teacher.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师Mapper接口
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
}
