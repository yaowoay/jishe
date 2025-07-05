package com.coldwind.easyoj.mapper;

import com.coldwind.easyoj.model.entity.ExamQuestionDetail;
import com.coldwind.easyoj.model.entity.ExamJudgmentDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 笔试记录Mapper
 */
@Mapper
public interface ExamRecordMapper {
    
    /**
     * 根据taskId查询题目详情
     */
    @Select("SELECT * FROM exam_question_detail WHERE task_id = #{taskId} ORDER BY question_id")
    List<ExamQuestionDetail> getQuestionsByTaskId(String taskId);
    
    /**
     * 根据taskId查询判题详情
     */
    @Select("SELECT * FROM exam_judgment_detail WHERE task_id = #{taskId} ORDER BY question_id")
    List<ExamJudgmentDetail> getJudgmentsByTaskId(String taskId);
    
    /**
     * 获取所有已完成的考试taskId列表
     */
    @Select("SELECT task_id FROM exam_judgment_detail GROUP BY task_id ORDER BY MIN(created_at) DESC")
    List<String> getAllCompletedTaskIds();
    
    /**
     * 获取同一批次生成的考试记录（created_at相差不到1秒的题目）
     */
    @Select("SELECT DISTINCT t1.task_id FROM exam_judgment_detail t1 " +
            "INNER JOIN exam_judgment_detail t2 ON t1.task_id = t2.task_id " +
            "WHERE ABS(UNIX_TIMESTAMP(t1.created_at) - UNIX_TIMESTAMP(t2.created_at)) <= 1 " +
            "GROUP BY t1.task_id " +
            "ORDER BY MIN(t1.created_at) DESC")
    List<String> getBatchExamTaskIds();
    
    /**
     * 根据taskId获取考试时间
     */
    @Select("SELECT MIN(created_at) as exam_time FROM exam_judgment_detail WHERE task_id = #{taskId}")
    String getExamTimeByTaskId(String taskId);
    
    /**
     * 根据taskId统计正确题目数
     */
    @Select("SELECT COUNT(*) FROM exam_judgment_detail WHERE task_id = #{taskId} AND is_correct = 1")
    Integer getCorrectCountByTaskId(String taskId);
    
    /**
     * 根据taskId统计总题目数
     */
    @Select("SELECT COUNT(*) FROM exam_judgment_detail WHERE task_id = #{taskId}")
    Integer getTotalCountByTaskId(String taskId);
} 