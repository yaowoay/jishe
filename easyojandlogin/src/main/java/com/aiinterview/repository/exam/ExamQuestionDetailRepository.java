package com.aiinterview.repository.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.exam.ExamQuestionDetail ;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamQuestionDetailRepository extends BaseMapper<ExamQuestionDetail> {
    
    @Select("SELECT * FROM exam_question_detail WHERE task_id = #{taskId} ORDER BY id ASC")
    List<ExamQuestionDetail> findByTaskId(String taskId);
    
    @Select("SELECT * FROM exam_question_detail WHERE question_id = #{questionId}")
    ExamQuestionDetail findByQuestionId(String questionId);
    
    @Select("SELECT COUNT(*) FROM exam_question_detail WHERE task_id = #{taskId}")
    int countByTaskId(String taskId);
}
