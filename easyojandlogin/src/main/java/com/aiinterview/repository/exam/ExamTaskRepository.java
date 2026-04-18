package com.aiinterview.repository.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiinterview.model.entity.exam.ExamTask ;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamTaskRepository extends BaseMapper<ExamTask> {

    @Select("SELECT * FROM exam_task WHERE task_id = #{taskId}")
    ExamTask findByTaskId(String taskId);

    @Select("SELECT * FROM exam_task WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<ExamTask> findByUserId(Long userId);

    @Select("SELECT * FROM exam_task WHERE status = #{status} ORDER BY created_at DESC")
    List<ExamTask> findByStatus(String status);
}
