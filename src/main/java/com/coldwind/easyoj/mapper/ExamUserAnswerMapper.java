package com.coldwind.easyoj.mapper;

import com.coldwind.easyoj.model.entity.ExamUserAnswer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamUserAnswerMapper {
    @Insert("INSERT INTO exam_user_answer (task_id, question_id, user_answer, created_at) " +
            "VALUES (#{taskId}, #{questionId}, #{userAnswer}, NOW())")
    void insert(ExamUserAnswer answer);
}