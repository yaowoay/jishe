package com.aiinterview.service.interview;

import com.aiinterview.model.dto.interview.InterviewExamDTO;
import com.aiinterview.model.entity.interview.InterviewAnswer;
import com.aiinterview.model.entity.interview.InterviewResult;

import java.util.List;
import java.util.Map;

/**
 * 面试考试服务接口
 */
public interface InterviewExamService {
    
    /**
     * 生成面试题目
     * @param applicationId 申请ID
     * @return 面试结果
     */
    InterviewResult generateInterviewQuestions(Integer applicationId);
    
    /**
     * 获取面试考试信息
     * @param applicationId 申请ID
     * @return 面试考试DTO
     */
    InterviewExamDTO getExamInfo(Integer applicationId);
    
    /**
     * 开始面试考试
     * @param applicationId 申请ID
     * @return 面试考试DTO
     */
    InterviewExamDTO startExam(Integer applicationId);
    
    /**
     * 提交面试答案
     * @param applicationId 申请ID
     * @param answers 答案列表
     * @return 面试答案记录
     */
    InterviewAnswer submitAnswers(Integer applicationId, List<Map<String, Object>> answers);
    
    /**
     * 获取面试结果
     * @param applicationId 申请ID
     * @return 面试答案记录
     */
    InterviewAnswer getInterviewResult(Integer applicationId);
    
    /**
     * 获取面试状态
     * @param applicationId 申请ID
     * @return 面试状态
     */
    String getInterviewStatus(Integer applicationId);
    
    /**
     * 重新开始面试
     * @param applicationId 申请ID
     * @return 面试考试DTO
     */
    InterviewExamDTO restartInterview(Integer applicationId);
    
    /**
     * 删除面试记录
     * @param applicationId 申请ID
     * @return 是否删除成功
     */
    boolean deleteInterviewRecord(Integer applicationId);

    /**
     * 实时保存面试回答
     * @param applicationId 申请ID
     * @param answerData 回答数据
     * @return 是否保存成功
     */
    boolean saveAnswerRealTime(Integer applicationId, Map<String, Object> answerData);

    /**
     * 更新面试状态
     * @param applicationId 申请ID
     * @param status 面试状态
     * @return 是否更新成功
     */
    boolean updateInterviewStatus(Integer applicationId, String status);
}
