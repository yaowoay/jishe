package com.aiinterview.repository.interview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.interview.InterviewSession ;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InterviewSessionRepository extends BaseMapper<InterviewSession> {
    
    @Select("SELECT * FROM interview_session WHERE session_id = #{sessionId}")
    InterviewSession findBySessionId(String sessionId);
    
    @Select("SELECT * FROM interview_session WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<InterviewSession> findByUserId(Long userId);
    
    @Select("SELECT * FROM interview_session WHERE status = #{status} ORDER BY created_at DESC")
    List<InterviewSession> findByStatus(String status);
}
