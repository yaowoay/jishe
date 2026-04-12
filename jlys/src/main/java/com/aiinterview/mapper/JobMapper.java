package com.aiinterview.mapper;

import com.aiinterview.model.entity.job.Job;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 职位Mapper接口
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {
}
