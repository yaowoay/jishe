package com.aiinterview.repository.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.exam.ExamJudgmentDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ExamJudgmentDetailRepository extends BaseMapper<ExamJudgmentDetail> {
    
    @Select("SELECT * FROM exam_judgment_detail WHERE task_id = #{taskId}")
    List<ExamJudgmentDetail> findByTaskId(String taskId);
    
    @Select("SELECT * FROM exam_judgment_detail WHERE task_id = #{taskId} AND question_id = #{questionId}")
    ExamJudgmentDetail findByTaskIdAndQuestionId(String taskId, String questionId);
}
