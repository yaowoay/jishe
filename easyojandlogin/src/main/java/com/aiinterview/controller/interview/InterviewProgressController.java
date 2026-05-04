package com.aiinterview.controller.interview;

import com.aiinterview.model.dto.writtenTest.WrittenTestExamDTO;
import com.aiinterview.model.entity.interview.InterviewProgress;
import com.aiinterview.service.interview.InterviewProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 面试进度控制器
 */
@Slf4j
@RestController
@RequestMapping("/interview-progress")
@CrossOrigin(origins = "*")
public class InterviewProgressController {

    @Autowired
    private InterviewProgressService interviewProgressService;

    /**
     * 保存笔试结果到面试进度
     */
    @PostMapping("/save-written-test")
    public ResponseEntity<Map<String, Object>> saveWrittenTestResult(@Valid @RequestBody SaveWrittenTestRequest request) {
        log.info("保存笔试结果到面试进度，申请ID: {}", request.getApplicationId());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            InterviewProgress progress = interviewProgressService.saveWrittenTestResult(
                request.getApplicationId(), 
                request.getTestResult(), 
                request.getPassed()
            );
            
            response.put("success", true);
            response.put("message", "笔试结果保存成功");
            response.put("data", progress);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("保存笔试结果失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取面试进度信息
     */
    @GetMapping("/info/{applicationId}")
    public ResponseEntity<Map<String, Object>> getProgressInfo(@PathVariable Integer applicationId) {
        log.info("获取面试进度信息，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            InterviewProgress progress = interviewProgressService.getProgressByApplicationId(applicationId);
            
            response.put("success", true);
            response.put("data", progress);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取面试进度信息失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取所有面试进度列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllProgress() {
        log.info("获取所有面试进度列表");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<InterviewProgress> progressList = interviewProgressService.getAllProgress();
            
            response.put("success", true);
            response.put("data", progressList);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取面试进度列表失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 更新面试状态
     */
    @PostMapping("/update-interview-status")
    public ResponseEntity<Map<String, Object>> updateInterviewStatus(@Valid @RequestBody UpdateInterviewStatusRequest request) {
        log.info("更新面试状态，申请ID: {}, 状态: {}", request.getApplicationId(), request.getStatus());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = interviewProgressService.updateInterviewStatus(request.getApplicationId(), request.getStatus());
            
            response.put("success", success);
            response.put("message", success ? "状态更新成功" : "状态更新失败");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("更新面试状态失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 保存笔试结果请求DTO
     */
    public static class SaveWrittenTestRequest {
        private Integer applicationId;
        private WrittenTestExamDTO.TestResultDTO testResult;
        private Boolean passed;

        // getters and setters
        public Integer getApplicationId() { return applicationId; }
        public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }
        public WrittenTestExamDTO.TestResultDTO getTestResult() { return testResult; }
        public void setTestResult(WrittenTestExamDTO.TestResultDTO testResult) { this.testResult = testResult; }
        public Boolean getPassed() { return passed; }
        public void setPassed(Boolean passed) { this.passed = passed; }
    }

    /**
     * 更新面试状态请求DTO
     */
    public static class UpdateInterviewStatusRequest {
        private Integer applicationId;
        private String status;

        // getters and setters
        public Integer getApplicationId() { return applicationId; }
        public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
