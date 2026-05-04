package com.aiinterview.repository.interview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.interview.InterviewQuestion ;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InterviewQuestionRepository extends BaseMapper<InterviewQuestion> {
    
    @Select("SELECT * FROM interview_question WHERE session_id = #{sessionId} ORDER BY id ASC")
    List<InterviewQuestion> findBySessionId(String sessionId);
    
    @Select("SELECT * FROM interview_question WHERE question_id = #{questionId}")
    InterviewQuestion findByQuestionId(String questionId);
    
    @Select("SELECT COUNT(*) FROM interview_question WHERE session_id = #{sessionId}")
    int countBySessionId(String sessionId);
}
