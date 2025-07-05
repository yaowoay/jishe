package com.coldwind.easyoj.mapper.mybatis;

import org.apache.ibatis.annotations.*;
import com.coldwind.easyoj.domain.InterviewerConfig;
import com.coldwind.easyoj.domain.QuestionConfig;
import com.coldwind.easyoj.domain.InterviewMode;

/**
 * InterviewConfigMapper
 * 用于操作面试配置相关的数据库表，包括面试官配置、题目配置和面试模式配置。
 */
@Mapper
public interface InterviewConfigMapper {

    /**
     * 插入面试官配置
     * @param appearance 面试官外貌
     * @param tone 面试官语气
     * @param personality 面试官性格
     * @return 插入的记录数
     */
    @Insert("INSERT INTO interviewer_config (appearance, tone, personality) VALUES (#{appearance}, #{tone}, #{personality})")
    int insertInterviewerConfig(@Param("appearance") String appearance, @Param("tone") String tone, @Param("personality") String personality);

    /**
     * 根据 ID 查询面试官配置
     * @param id 配置 ID
     * @return 面试官配置对象
     */
    @Select("SELECT * FROM interviewer_config WHERE id = #{id}")
    InterviewerConfig selectInterviewerConfigById(@Param("id") Long id);

    /**
     * 插入题目配置
     * @param config 题目配置对象
     * @return 插入的记录数
     */
    @Insert("INSERT INTO question_config (question_range, difficulty, question_count, preference) VALUES (#{questionRange}, #{difficulty}, #{questionCount}, #{preference})")
    int insertQuestionConfig(QuestionConfig config);

    /**
     * 根据 ID 查询题目配置
     * @param id 配置 ID
     * @return 题目配置对象
     */
    @Select("SELECT * FROM question_config WHERE id = #{id}")
    QuestionConfig selectQuestionConfigById(@Param("id") Long id);

    /**
     * 插入面试模式配置
     * @param mode 面试模式对象
     * @return 插入的记录数
     */
    @Insert("INSERT INTO interview_mode (mode_type, position, duration) VALUES (#{modeType}, #{position}, #{duration})")
    int insertInterviewMode(InterviewMode mode);

    /**
     * 根据 ID 查询面试模式配置
     * @param id 配置 ID
     * @return 面试模式对象
     */
    @Select("SELECT * FROM interview_mode WHERE id = #{id}")
    InterviewMode selectInterviewModeById(@Param("id") Long id);
}