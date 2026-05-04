package com.aiinterview.mapper;

import com.aiinterview.model.entity.interview.InterviewScores;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 面试评分Mapper接口
 */
@Mapper
public interface InterviewScoresMapper extends BaseMapper<InterviewScores> {
}
