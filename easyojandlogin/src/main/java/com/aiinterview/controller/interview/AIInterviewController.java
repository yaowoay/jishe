package com.aiinterview.controller.interview;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.entity.interview.AIInterview;
import com.aiinterview.service.interview.AIInterviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AI面试记录Controller
 */
@Slf4j
@RestController
@RequestMapping("/ai-interviews")
public class AIInterviewController {

    @Autowired
    private AIInterviewService aiInterviewService;

    /**
     * 保存面试记录
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> saveInterview(@RequestBody AIInterviewRequest request) {
        try {
            log.info("保存面试记录，面试ID: {}", request.getInterviewId());

            AIInterview interview = new AIInterview();
            interview.setInterviewId(request.getInterviewId());
            interview.setApplicationId(request.getApplicationId()); // 设置关联的申请ID

            // 解析时间字符串
            if (request.getStartTime() != null) {
                interview.setStartTime(LocalDateTime.parse(request.getStartTime(), DateTimeFormatter.ISO_DATE_TIME));
            }
            if (request.getEndTime() != null) {
                interview.setEndTime(LocalDateTime.parse(request.getEndTime(), DateTimeFormatter.ISO_DATE_TIME));
            }

            interview.setHistory(request.getHistory());
            interview.setEvaluation(request.getEvaluation());
            interview.setOverallScore(request.getOverallScore());

            Long savedId = aiInterviewService.saveInterview(interview);
            
            log.info("面试记录保存成功，ID: {}", savedId);
            return ResponseEntity.ok(ApiResponse.success("面试记录保存成功", savedId));

        } catch (Exception e) {
            log.error("保存面试记录失败，面试ID: {}", request.getInterviewId(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("保存面试记录失败: " + e.getMessage()));
        }
    }

    /**
     * 根据面试ID获取面试记录
     */
    @GetMapping("/{interviewId}")
    public ResponseEntity<ApiResponse<AIInterview>> getInterview(@PathVariable Long interviewId) {
        try {
            log.info("获取面试记录，面试ID: {}", interviewId);

            AIInterview interview = aiInterviewService.getByInterviewId(interviewId);
            
            if (interview == null) {
                return ResponseEntity.ok(ApiResponse.<AIInterview>success("未找到面试记录", null));
            }

            return ResponseEntity.ok(ApiResponse.success(interview));

        } catch (Exception e) {
            log.error("获取面试记录失败，面试ID: {}", interviewId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取面试记录失败: " + e.getMessage()));
        }
    }

    /**
     * 面试记录请求DTO
     */
    public static class AIInterviewRequest {
        private Long interviewId;
        private Long applicationId; // 关联的申请ID
        private String startTime;
        private String endTime;
        private String history;
        private String evaluation;
        private Double overallScore;

        // Getters and Setters
        public Long getInterviewId() { return interviewId; }
        public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }

        public Long getApplicationId() { return applicationId; }
        public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }

        public String getHistory() { return history; }
        public void setHistory(String history) { this.history = history; }

        public String getEvaluation() { return evaluation; }
        public void setEvaluation(String evaluation) { this.evaluation = evaluation; }

        public Double getOverallScore() { return overallScore; }
        public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }
    }

    /**
     * 更新面试视频路径
     */
    @PutMapping("/{interviewId}/video")
    public ResponseEntity<ApiResponse<String>> updateVideoPath(
            @PathVariable Long interviewId,
            @RequestBody VideoPathRequest request) {
        try {
            log.info("更新面试视频路径，面试ID: {}, 视频路径: {}", interviewId, request.getVideoPath());

            boolean success = aiInterviewService.updateVideoPath(interviewId, request.getVideoPath());

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("视频路径更新成功", "success"));
            } else {
                return ResponseEntity.ok(ApiResponse.success("未找到对应的面试记录", "not_found"));
            }

        } catch (Exception e) {
            log.error("更新视频路径失败，面试ID: {}", interviewId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("更新视频路径失败: " + e.getMessage()));
        }
    }

    /**
     * 视频路径更新请求DTO
     */
    public static class VideoPathRequest {
        private String videoPath;

        public String getVideoPath() { return videoPath; }
        public void setVideoPath(String videoPath) { this.videoPath = videoPath; }
    }
}
