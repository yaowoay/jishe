package com.aiinterview.mapper;

import com.aiinterview.model.entity.disctest.DiscQuestion;
import com.aiinterview.model.entity.disctest.DiscTestRecord;
import com.aiinterview.model.entity.disctest.DiscTestResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * DISC测试Mapper接口
 */
@Mapper
public interface DiscTestMapper extends BaseMapper<DiscTestRecord> {
    
    /**
     * 获取所有DISC测试题目，按题目组分组
     */
    @Select("SELECT question_group, option_letter, option_text, disc_type, is_positive " +
            "FROM disc_questions ORDER BY question_group, option_letter")
    List<DiscQuestion> getAllQuestions();
    
    /**
     * 根据题目组和选项获取DISC类型
     */
    @Select("SELECT disc_type FROM disc_questions " +
            "WHERE question_group = #{questionGroup} AND option_letter = #{optionLetter}")
    String getDiscTypeByOption(@Param("questionGroup") Integer questionGroup, 
                              @Param("optionLetter") String optionLetter);
    
    /**
     * 保存测试答案
     */
    void saveTestAnswer(@Param("recordId") Long recordId, 
                       @Param("questionGroup") Integer questionGroup,
                       @Param("selectedOption") String selectedOption, 
                       @Param("discType") String discType);
    
    /**
     * 保存测试结果
     */
    void saveTestResult(DiscTestResult result);
    
    /**
     * 根据测试会话ID获取测试记录
     */
    @Select("SELECT * FROM disc_test_records WHERE test_session = #{testSession}")
    DiscTestRecord getRecordBySession(@Param("testSession") String testSession);
    
    /**
     * 根据记录ID获取测试结果
     */
    @Select("SELECT * FROM disc_test_results WHERE record_id = #{recordId}")
    DiscTestResult getResultByRecordId(@Param("recordId") Long recordId);
    
    /**
     * 获取DISC类型描述
     */
    @Select("SELECT * FROM disc_type_descriptions WHERE disc_type = #{discType}")
    Map<String, Object> getTypeDescription(@Param("discType") String discType);
    
    /**
     * 根据用户ID获取最新的测试结果
     */
    @Select("SELECT r.* FROM disc_test_results r " +
            "INNER JOIN disc_test_records rec ON r.record_id = rec.record_id " +
            "WHERE rec.user_id = #{userId} AND rec.test_status = 'completed' " +
            "ORDER BY r.created_at DESC LIMIT 1")
    DiscTestResult getLatestResultByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID获取所有测试历史记录
     */
    @Select("SELECT r.* FROM disc_test_results r " +
            "INNER JOIN disc_test_records rec ON r.record_id = rec.record_id " +
            "WHERE rec.user_id = #{userId} AND rec.test_status = 'completed' " +
            "ORDER BY r.created_at DESC")
    List<DiscTestResult> getTestHistoryByUserId(@Param("userId") Long userId);
}
