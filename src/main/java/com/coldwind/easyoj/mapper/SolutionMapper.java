package com.coldwind.easyoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coldwind.easyoj.model.entity.Solution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 解答Mapper
 */
@Mapper
public interface SolutionMapper extends BaseMapper<Solution> {
    
    /**
     * 根据题目ID和语言ID查询解答
     */
    @Select("SELECT * FROM solution WHERE problem_id = #{problemId} AND language_id = #{languageId}")
    Solution selectByProblemAndLanguage(Integer problemId, Integer languageId);
}