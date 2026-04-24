package com.aiinterview.controller.behavior;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.model.entity.student.UserAction;
import com.aiinterview.service.UserBehaviorAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 用户行为记录 Controller
 * 用于前端主动上报用户行为
 */
@Slf4j
@RestController
@RequestMapping("/behavior")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserBehaviorController {

    private final UserBehaviorAnalysisService userBehaviorAnalysisService;

    // 日志文件路径
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "user_action.log";

    /**
     * 记录用户行为（前端主动上报）
     * @param behaviorData 行为数据
     * @return 响应结果
     */
    @PostMapping("/log")
    public BaseResponse<String> logUserBehavior(@RequestBody Map<String, Object> behaviorData) {
        try {
            log.info("收到前端行为上报: {}", behaviorData);

            // 1. 写入日志文件
            writeToLogFile(behaviorData);

            // 2. 存储到数据库
            saveToDatabase(behaviorData);

            return ResultUtils.success("行为记录成功");
        } catch (Exception e) {
            log.error("记录用户行为失败", e);
            return ResultUtils.error(500, "记录失败: " + e.getMessage());
        }
    }

    /**
     * 写入日志文件
     */
    private void writeToLogFile(Map<String, Object> behaviorData) {
        try {
            // 确保日志目录存在
            Path logDir = Paths.get(LOG_DIR);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }

            // 构建日志内容
            String logContent = buildLogContent(behaviorData);

            // 追加写入日志文件
            Path logFile = logDir.resolve(LOG_FILE);
            Files.write(logFile, (logContent + System.lineSeparator()).getBytes(),
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            log.info("行为日志已写入文件: {}", logFile.toAbsolutePath());
        } catch (IOException e) {
            log.error("写入日志文件失败", e);
        }
    }

    /**
     * 构建日志内容（JSON格式）
     */
    private String buildLogContent(Map<String, Object> behaviorData) {
        // 使用JSON格式输出
        try {
            StringBuilder json = new StringBuilder("{");
            json.append("\"userId\":\"").append(behaviorData.get("userId")).append("\",");
            json.append("\"jobId\":\"").append(behaviorData.get("jobId")).append("\",");
            json.append("\"actionType\":\"").append(behaviorData.get("actionType")).append("\",");
            json.append("\"score\":").append(behaviorData.get("score")).append(",");
            json.append("\"dt\":\"").append(behaviorData.get("dt")).append("\",");
            json.append("\"eventTime\":\"").append(behaviorData.get("eventTime")).append("\",");
            json.append("\"duration\":").append(behaviorData.getOrDefault("duration", 0));
            json.append("}");
            return json.toString();
        } catch (Exception e) {
            log.error("构建JSON日志失败", e);
            return behaviorData.toString();
        }
    }

    /**
     * 存储到数据库
     */
    private void saveToDatabase(Map<String, Object> behaviorData) {
        try {
            UserAction action = new UserAction();
            action.setUserId(Long.valueOf(behaviorData.get("userId").toString()));
            action.setActionType(behaviorData.get("actionType").toString());
            // 将 jobId 存储到 actionTarget 字段
            action.setActionTarget(behaviorData.get("jobId").toString());
            action.setActionTime(LocalDateTime.now());

            // 记录到数据库
            userBehaviorAnalysisService.recordUserAction(action);
            log.info("行为数据已存储到数据库");
        } catch (Exception e) {
            log.error("存储到数据库失败", e);
        }
    }
}
