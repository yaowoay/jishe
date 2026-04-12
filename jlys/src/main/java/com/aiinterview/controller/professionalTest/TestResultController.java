package com.aiinterview.controller.professionalTest;

import com.aiinterview.model.dto.professionalTest.TestResultDTO;
import com.aiinterview.model.entity.professionalTest.TestResult;
import com.aiinterview.model.entity.professionalTest.TestQuestion;
import com.aiinterview.model.entity.professionalTest.UserAnswer;
import com.aiinterview.service.professionalTest.TestResultService;
import com.aiinterview.repository.professionalTest.TestQuestionRepository;
import com.aiinterview.repository.professionalTest.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测试结果控制器
 */
@Slf4j
@RestController
@RequestMapping("/test-results")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestResultController {
    
    private final TestResultService testResultService;
    private final TestQuestionRepository testQuestionRepository;
    private final UserAnswerRepository userAnswerRepository;
    
    /**
     * 保存测试结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveTestResult(@Valid @RequestBody TestResultDTO testResultDTO) {
        log.info("接收到保存测试结果请求，用户ID: {}, 类别: {}", testResultDTO.getUserId(), testResultDTO.getCategory());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            TestResult savedResult = testResultService.saveTestResult(testResultDTO);
            
            response.put("success", true);
            response.put("message", "测试结果保存成功");

            Map<String, Object> data = new HashMap<>();
            data.put("testResultId", savedResult.getId());
            data.put("score", savedResult.getScore());
            data.put("completedAt", savedResult.getCompletedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("保存测试结果失败", e);
            response.put("success", false);
            response.put("message", "保存测试结果失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取用户测试历史记录
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getTestHistory(
            @RequestParam Integer userId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("获取测试历史记录，用户ID: {}, 类别: {}, 页码: {}, 页大小: {}", userId, category, page, pageSize);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Page<TestResult> pageObj = new Page<>(page, pageSize);
            Page<TestResult> testResults;

            if (category != null && !category.trim().isEmpty()) {
                testResults = testResultService.findByUserIdAndCategoryWithPagination(userId, category, pageObj);
            } else {
                testResults = testResultService.findByUserIdWithPagination(userId, pageObj);
            }
            
            response.put("success", true);

            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("pageSize", pageSize);
            pagination.put("total", testResults.getTotal());
            pagination.put("totalPages", testResults.getPages());

            Map<String, Object> data = new HashMap<>();
            data.put("list", testResults.getRecords());
            data.put("pagination", pagination);
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取测试历史记录失败", e);
            response.put("success", false);
            response.put("message", "获取测试历史记录失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取测试结果详情
     */
    @GetMapping("/{resultId}")
    public ResponseEntity<Map<String, Object>> getTestResultDetail(
            @PathVariable Long resultId,
            @RequestParam Integer userId) {
        
        log.info("获取测试结果详情，结果ID: {}, 用户ID: {}", resultId, userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<TestResult> testResultOpt = testResultService.findById(resultId);
            
            if (testResultOpt.isPresent()) {
                TestResult testResult = testResultOpt.get();
                
                // 验证是否为当前用户的测试结果
                if (!testResult.getUserId().equals(userId)) {
                    response.put("success", false);
                    response.put("message", "无权限访问该测试结果");
                    return ResponseEntity.status(403).body(response);
                }
                
                // 获取关联的题目和答题记录
                List<TestQuestion> questions = testQuestionRepository.findByTestResultId(resultId);
                List<UserAnswer> userAnswers = userAnswerRepository.findByTestResultId(resultId);

                Map<String, Object> data = new HashMap<>();
                data.put("testResult", testResult);
                data.put("questions", questions);
                data.put("userAnswers", userAnswers);

                response.put("success", true);
                response.put("data", data);
                return ResponseEntity.ok(response);
                
            } else {
                response.put("success", false);
                response.put("message", "测试结果不存在");
                return ResponseEntity.status(404).body(response);
            }
            
        } catch (Exception e) {
            log.error("获取测试结果详情失败", e);
            response.put("success", false);
            response.put("message", "获取测试结果详情失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取用户测试统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTestStatistics(
            @RequestParam Integer userId,
            @RequestParam(required = false) String category) {
        
        log.info("获取测试统计信息，用户ID: {}, 类别: {}", userId, category);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> statistics;
            
            if (category != null && !category.trim().isEmpty()) {
                statistics = testResultService.getTestStatisticsByCategory(userId, category);
            } else {
                statistics = testResultService.getTestStatistics(userId);
            }
            
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取测试统计信息失败", e);
            response.put("success", false);
            response.put("message", "获取测试统计信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 删除测试记录
     */
    @DeleteMapping("/{resultId}")
    public ResponseEntity<Map<String, Object>> deleteTestResult(
            @PathVariable Long resultId,
            @RequestParam Integer userId) {
        
        log.info("删除测试记录，结果ID: {}, 用户ID: {}", resultId, userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = testResultService.deleteTestResult(resultId, userId);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "测试记录删除成功");
            } else {
                response.put("success", false);
                response.put("message", "测试记录不存在或无权限删除");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("删除测试记录失败", e);
            response.put("success", false);
            response.put("message", "删除测试记录失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量删除测试记录
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<Map<String, Object>> batchDeleteTestResults(
            @RequestBody Map<String, Object> requestBody) {
        
        @SuppressWarnings("unchecked")
        List<Long> resultIds = (List<Long>) requestBody.get("resultIds");
        Integer userId = (Integer) requestBody.get("userId");
        
        log.info("批量删除测试记录，用户ID: {}, 记录数量: {}", userId, resultIds.size());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            int deletedCount = testResultService.batchDeleteTestResults(resultIds, userId);
            
            response.put("success", true);
            response.put("message", String.format("成功删除 %d 条测试记录", deletedCount));

            Map<String, Object> data = new HashMap<>();
            data.put("deletedCount", deletedCount);
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("批量删除测试记录失败", e);
            response.put("success", false);
            response.put("message", "批量删除测试记录失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取用户最新测试结果
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestTestResult(
            @RequestParam Integer userId,
            @RequestParam(required = false) String category) {
        
        log.info("获取最新测试结果，用户ID: {}, 类别: {}", userId, category);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<TestResult> latestResult;
            
            if (category != null && !category.trim().isEmpty()) {
                latestResult = testResultService.getLatestTestResultByCategory(userId, category);
            } else {
                latestResult = testResultService.getLatestTestResult(userId);
            }
            
            if (latestResult.isPresent()) {
                response.put("success", true);
                response.put("data", latestResult.get());
            } else {
                response.put("success", false);
                response.put("message", "未找到测试记录");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取最新测试结果失败", e);
            response.put("success", false);
            response.put("message", "获取最新测试结果失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
