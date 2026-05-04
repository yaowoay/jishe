package com.aiinterview.mapper;

import com.aiinterview.model.entity.resume.Resume;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 简历Mapper接口
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {
}
