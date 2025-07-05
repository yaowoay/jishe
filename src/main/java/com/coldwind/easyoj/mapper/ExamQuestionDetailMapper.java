package com.coldwind.easyoj.mapper;

import com.coldwind.easyoj.model.entity.ExamQuestionDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ExamQuestionDetailMapper {
    @Insert("INSERT INTO exam_question_detail (task_id, question_id, type, content, option_a, option_b, option_c, option_d, answer, explanation, created_at) " +
            "VALUES (#{taskId}, #{questionId}, #{type}, #{content}, #{optionA}, #{optionB}, #{optionC}, #{optionD}, #{answer}, #{explanation}, NOW())")
    void insert(ExamQuestionDetail detail);
    
    @Select("SELECT * FROM exam_question_detail WHERE task_id = #{taskId} ORDER BY question_id")
    List<ExamQuestionDetail> getQuestionsByTaskId(String taskId);
}