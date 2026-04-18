package com.aiinterview.repository.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.exam.ExamUserAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ExamUserAnswerRepository extends BaseMapper<ExamUserAnswer> {
    
    @Select("SELECT * FROM exam_user_answer WHERE task_id = #{taskId}")
    List<ExamUserAnswer> findByTaskId(String taskId);
    
    @Select("SELECT * FROM exam_user_answer WHERE task_id = #{taskId} AND question_id = #{questionId}")
    ExamUserAnswer findByTaskIdAndQuestionId(String taskId, String questionId);
}
