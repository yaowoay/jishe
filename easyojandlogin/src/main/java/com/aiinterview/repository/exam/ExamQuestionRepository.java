package com.aiinterview.repository.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.exam.ExamQuestion ;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamQuestionRepository extends BaseMapper<ExamQuestion> {
    
    @Select("SELECT * FROM exam_question WHERE task_id = #{taskId} ORDER BY id ASC")
    List<ExamQuestion> findByTaskId(String taskId);
    
    @Select("SELECT * FROM exam_question WHERE question_id = #{questionId}")
    ExamQuestion findByQuestionId(String questionId);
    
    @Select("SELECT COUNT(*) FROM exam_question WHERE task_id = #{taskId}")
    int countByTaskId(String taskId);
}
