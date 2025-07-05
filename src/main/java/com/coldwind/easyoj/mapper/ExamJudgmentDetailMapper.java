package com.coldwind.easyoj.mapper;

import com.coldwind.easyoj.model.entity.ExamJudgmentDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamJudgmentDetailMapper {
    @Insert("INSERT INTO exam_judgment_detail (task_id, question_id, user_answer, correct_answer, is_correct, explanation, created_at) " +
            "VALUES (#{taskId}, #{questionId}, #{userAnswer}, #{correctAnswer}, #{isCorrect}, #{explanation}, NOW())")
    void insert(ExamJudgmentDetail detail);
}