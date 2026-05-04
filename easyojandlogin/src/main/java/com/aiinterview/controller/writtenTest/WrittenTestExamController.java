package com.aiinterview.controller.writtenTest;

import com.aiinterview.model.dto.writtenTest.WrittenTestExamDTO;
import com.aiinterview.model.entity.writtenTest.WrittenTestAnswer;
import com.aiinterview.service.writtenTest.WrittenTestExamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 笔试考试控制器
 */
@Slf4j
@RestController
@RequestMapping("/written-test-exam")
@CrossOrigin(origins = "*")
public class WrittenTestExamController {

    @Autowired
    private WrittenTestExamService writtenTestExamService;

    /**
     * 获取笔试考试信息
     */
    @GetMapping("/info/{applicationId}")
    public ResponseEntity<Map<String, Object>> getExamInfo(@PathVariable Integer applicationId) {
        log.info("获取笔试考试信息，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            WrittenTestExamDTO examInfo = writtenTestExamService.getExamInfo(applicationId);
            
            response.put("success", true);
            response.put("data", examInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取笔试考试信息失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 开始笔试考试
     */
    @PostMapping("/start/{applicationId}")
    public ResponseEntity<Map<String, Object>> startExam(@PathVariable Integer applicationId) {
        log.info("开始笔试考试，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            WrittenTestExamDTO examInfo = writtenTestExamService.startExam(applicationId);
            
            response.put("success", true);
            response.put("message", "笔试已开始，请认真答题");
            response.put("data", examInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("开始笔试考试失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 提交笔试答案
     */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitAnswers(@Valid @RequestBody WrittenTestExamDTO.SubmitAnswersDTO submitData) {
        log.info("提交笔试答案，申请ID: {}", submitData.getApplicationId());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            WrittenTestExamDTO.TestResultDTO result = writtenTestExamService.submitAnswers(submitData);
            
            response.put("success", true);
            response.put("message", "答案提交成功，已完成批改");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("提交笔试答案失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取笔试结果
     */
    @GetMapping("/result/{applicationId}")
    public ResponseEntity<Map<String, Object>> getTestResult(@PathVariable Integer applicationId) {
        log.info("获取笔试结果，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            WrittenTestExamDTO.TestResultDTO result = writtenTestExamService.getTestResult(applicationId);
            
            response.put("success", true);
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取笔试结果失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 检查笔试状态
     */
    @GetMapping("/status/{applicationId}")
    public ResponseEntity<Map<String, Object>> getTestStatus(@PathVariable Integer applicationId) {
        log.info("检查笔试状态，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            WrittenTestAnswer status = writtenTestExamService.getTestStatus(applicationId);
            
            response.put("success", true);
            response.put("data", status);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("检查笔试状态失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 重新开始笔试
     */
    @PostMapping("/restart/{applicationId}")
    public ResponseEntity<Map<String, Object>> restartExam(@PathVariable Integer applicationId) {
        log.info("重新开始笔试，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            WrittenTestExamDTO examInfo = writtenTestExamService.restartExam(applicationId);
            
            response.put("success", true);
            response.put("message", "笔试已重新开始");
            response.put("data", examInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("重新开始笔试失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        result.put("message", "笔试考试服务正常运行");
        result.put("service", "WrittenTestExamService");
        return ResponseEntity.ok(result);
    }
}
