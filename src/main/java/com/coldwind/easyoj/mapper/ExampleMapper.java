package com.coldwind.easyoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coldwind.easyoj.model.entity.Example;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 示例Mapper
 */
@Mapper
public interface ExampleMapper extends BaseMapper<Example> {
    
    /**
     * 根据题目ID查询示例
     */
    @Select("SELECT * FROM example WHERE problem_id = #{problemId}")
    List<Example> selectByProblemId(Integer problemId);
}