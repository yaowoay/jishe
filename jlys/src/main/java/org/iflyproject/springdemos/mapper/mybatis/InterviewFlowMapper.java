package org.iflyproject.springdemos.mapper.mybatis;

import org.apache.ibatis.annotations.*;
import org.iflyproject.springdemos.model.dto.InterviewSession;


/**
 * InterviewFlowMapper
 * 用于操作面试流程相关的数据库表，包括面试会话的创建、查询和状态更新。
 */
@Mapper
public interface InterviewFlowMapper {

    /**
     * 插入面试会话记录
     * @param sessionId 会话 ID
     * @param status 会话状态
     * @return 插入的记录数
     */
    @Insert("INSERT INTO interview_session (session_id, status) VALUES (#{sessionId}, #{status})")
    int insertInterviewSession(@Param("sessionId") String sessionId, @Param("status") String status);

    /**
     * 根据会话 ID 查询面试会话
     * @param sessionId 会话 ID
     * @return 面试会话对象
     */
    @Select("SELECT * FROM interview_session WHERE session_id = #{sessionId}")
    InterviewSession selectInterviewSessionById(@Param("sessionId") String sessionId);

    /**
     * 更新面试会话状态
     * @param sessionId 会话 ID
     * @param status 新的会话状态
     * @return 更新的记录数
     */
    @Update("UPDATE interview_session SET status = #{status} WHERE session_id = #{sessionId}")
    int updateInterviewSessionStatus(@Param("sessionId") String sessionId, @Param("status") String status);
}