package com.aiinterview.controller.writtenTest;

import com.aiinterview.model.dto.writtenTest.WrittenTestGenerationRequest;
import com.aiinterview.model.dto.writtenTest.WrittenTestGenerationResponse;
import com.aiinterview.model.entity.writtenTest.WrittenTestResult;
import com.aiinterview.service.writtenTest.WrittenTestGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 笔试生成控制器
 */
@Slf4j
@RestController
@RequestMapping("/written-test-generation")
@CrossOrigin(origins = "*")
public class WrittenTestGenerationController {

    @Autowired
    private WrittenTestGenerationService writtenTestGenerationService;

    /**
     * 生成笔试题目
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateWrittenTest(@Valid @RequestBody WrittenTestGenerationRequest request) {
        log.info("接收到笔试生成请求，申请ID: {}, 简历ID: {}", request.getApplicationId(), request.getResumeId());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            WrittenTestGenerationResponse result = writtenTestGenerationService.generateWrittenTest(request);
            
            response.put("success", true);
            response.put("message", "笔试生成成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("笔试生成失败", e);
            response.put("success", false);
            response.put("message", "笔试生成失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据申请ID获取笔试结果
     */
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<Map<String, Object>> getByApplicationId(@PathVariable Integer applicationId) {
        log.info("获取笔试结果，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            WrittenTestResult result = writtenTestGenerationService.getByApplicationId(applicationId);
            
            if (result != null) {
                response.put("success", true);
                response.put("data", result);
            } else {
                response.put("success", false);
                response.put("message", "未找到相关笔试结果");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取笔试结果失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据ID获取笔试结果详情（包含题目）
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getDetailById(@PathVariable Long id) {
        log.info("获取笔试结果详情，ID: {}", id);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            WrittenTestGenerationResponse result = writtenTestGenerationService.getDetailById(id);
            
            if (result != null) {
                response.put("success", true);
                response.put("data", result);
            } else {
                response.put("success", false);
                response.put("message", "未找到相关笔试结果");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取笔试结果详情失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据简历ID获取笔试结果列表
     */
    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<Map<String, Object>> getByResumeId(@PathVariable Long resumeId) {
        log.info("获取简历的笔试结果列表，简历ID: {}", resumeId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<WrittenTestResult> results = writtenTestGenerationService.getByResumeId(resumeId);
            
            response.put("success", true);
            response.put("data", results);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取简历笔试结果列表失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 重新生成笔试题目
     */
    @PostMapping("/regenerate/{applicationId}")
    public ResponseEntity<Map<String, Object>> regenerateWrittenTest(@PathVariable Integer applicationId) {
        log.info("重新生成笔试题目，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            WrittenTestGenerationResponse result = writtenTestGenerationService.regenerateWrittenTest(applicationId);
            
            response.put("success", true);
            response.put("message", "笔试重新生成成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("重新生成笔试题目失败", e);
            response.put("success", false);
            response.put("message", "重新生成失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 更新笔试结果状态
     */
    @PutMapping("/status/{id}")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String errorMessage) {
        log.info("更新笔试结果状态，ID: {}, 状态: {}", id, status);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = writtenTestGenerationService.updateStatus(id, status, errorMessage);
            
            if (success) {
                response.put("success", true);
                response.put("message", "状态更新成功");
            } else {
                response.put("success", false);
                response.put("message", "状态更新失败");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("更新笔试结果状态失败", e);
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除笔试结果
     */
    @DeleteMapping("/application/{applicationId}")
    public ResponseEntity<Map<String, Object>> deleteByApplicationId(@PathVariable Integer applicationId) {
        log.info("删除笔试结果，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = writtenTestGenerationService.deleteByApplicationId(applicationId);
            
            if (success) {
                response.put("success", true);
                response.put("message", "删除成功");
            } else {
                response.put("success", false);
                response.put("message", "删除失败");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("删除笔试结果失败", e);
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
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
        result.put("message", "笔试生成服务正常运行");
        result.put("service", "WrittenTestGenerationService");
        return ResponseEntity.ok(result);
    }
}
