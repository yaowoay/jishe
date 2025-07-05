package com.coldwind.easyoj.service.impl;

import com.coldwind.easyoj.model.dto.request.UserAnswersRequest;
import com.coldwind.easyoj.service.AnswerStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class AnswerStorageServiceImpl implements AnswerStorageService {

    @Value("${answer.storage.path:./answers}")
    private String storagePath;

    @Override
    public String saveAnswersToTxt(String taskId, UserAnswersRequest userAnswers) {
        // 创建存储目录
        File directory = new File(storagePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 生成文件名（即使taskId无效也能保存）
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = String.format("answers_%s_%s.txt", 
            StringUtils.isEmpty(taskId) ? "invalid-task" : taskId, timestamp);
        File answerFile = new File(directory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(answerFile))) {
            // 写入基本信息
            writer.write("Task ID: " + (StringUtils.isEmpty(taskId) ? "无效任务ID" : taskId));
            writer.newLine();
            writer.write("Submit Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.newLine();
            writer.write("Status: " + (StringUtils.isEmpty(taskId) ? "无效任务ID" : "正常提交"));
            writer.newLine();
            writer.write("========================================");
            writer.newLine();

            // 写入答案内容
            if (userAnswers.getAnswers() != null) {
                userAnswers.getAnswers().forEach(answer -> {
                    try {
                        writer.write(answer.getQuestionId() + ": " + answer.getUserAnswer());
                        writer.newLine();
                    } catch (IOException e) {
                        log.error("写入答案失败: {}", e.getMessage());
                    }
                });
            }

            log.info("答案已保存: {}", answerFile.getAbsolutePath());
            return answerFile.getAbsolutePath();
        } catch (IOException e) {
            log.error("答案保存失败: {}", e.getMessage());
            throw new RuntimeException("保存答案文件失败: " + e.getMessage());
        }
    }
}