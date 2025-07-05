package com.coldwind.easyoj.service;
import com.coldwind.easyoj.model.dto.request.UserAnswersRequest;

public interface AnswerStorageService {
    /**
     * 将用户答案保存到TXT文件
     * @param taskId 任务ID
     * @param userAnswers 用户答案请求对象
     * @return 保存的文件路径
     */
    String saveAnswersToTxt(String taskId, UserAnswersRequest userAnswers);
}