package com.aiinterview.service.writtenTest;

import com.aiinterview.model.dto.writtenTest.WrittenTestExamDTO;
import com.aiinterview.model.entity.writtenTest.WrittenTestAnswer;

/**
 * 笔试考试服务接口
 */
public interface WrittenTestExamService {

    /**
     * 获取笔试考试信息（开始考试）
     * @param applicationId 申请ID
     * @return 考试信息
     */
    WrittenTestExamDTO getExamInfo(Integer applicationId);

    /**
     * 开始笔试考试
     * @param applicationId 申请ID
     * @return 考试信息
     */
    WrittenTestExamDTO startExam(Integer applicationId);

    /**
     * 提交笔试答案
     * @param submitData 提交数据
     * @return 考试结果
     */
    WrittenTestExamDTO.TestResultDTO submitAnswers(WrittenTestExamDTO.SubmitAnswersDTO submitData);

    /**
     * 获取笔试结果
     * @param applicationId 申请ID
     * @return 考试结果
     */
    WrittenTestExamDTO.TestResultDTO getTestResult(Integer applicationId);

    /**
     * 检查笔试状态
     * @param applicationId 申请ID
     * @return 笔试状态信息
     */
    WrittenTestAnswer getTestStatus(Integer applicationId);

    /**
     * 重新开始笔试（如果允许）
     * @param applicationId 申请ID
     * @return 考试信息
     */
    WrittenTestExamDTO restartExam(Integer applicationId);
}
