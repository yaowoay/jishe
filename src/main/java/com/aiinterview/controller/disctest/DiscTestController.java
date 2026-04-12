package com.aiinterview.controller.disctest;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.disctest.DiscTestRequest;
import com.aiinterview.model.dto.disctest.DiscTestResponse;
import com.aiinterview.service.disctest.DiscTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DISC性格测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/disc-test")
public class DiscTestController {
    
    @Autowired
    private DiscTestService discTestService;
    
    /**
     * 开始DISC测试
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<DiscTestResponse>> startTest(
            @RequestParam(value = "userId", required = false) Long userId) {
        
        try {
            log.info("开始DISC测试，用户ID: {}", userId);
            
            DiscTestResponse response = discTestService.startTest(userId);
            
            return ResponseEntity.ok(ApiResponse.success("测试开始成功", response));
            
        } catch (Exception e) {
            log.error("开始DISC测试失败，用户ID: {}", userId, e);
            return ResponseEntity.ok(ApiResponse.error("开始测试失败: " + e.getMessage()));
        }
    }
    
    /**
     * 提交DISC测试答案
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<DiscTestResponse>> submitTest(
            @RequestBody DiscTestRequest request) {
        
        try {
            log.info("提交DISC测试，会话ID: {}, 答案数量: {}", 
                    request.getTestSession(), 
                    request.getAnswers() != null ? request.getAnswers().size() : 0);
            
            // 验证答案数量（40题完整版本）
            if (request.getAnswers() == null || request.getAnswers().size() != 40) {
                return ResponseEntity.ok(ApiResponse.error("答案数量不正确，应为40题"));
            }
            
            DiscTestResponse response = discTestService.submitTest(request);
            
            return ResponseEntity.ok(ApiResponse.success("测试提交成功", response));
            
        } catch (Exception e) {
            log.error("提交DISC测试失败，会话ID: {}", request.getTestSession(), e);
            return ResponseEntity.ok(ApiResponse.error("提交测试失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取测试结果
     */
    @GetMapping("/result/{testSession}")
    public ResponseEntity<ApiResponse<DiscTestResponse>> getTestResult(
            @PathVariable String testSession) {
        
        try {
            log.info("获取DISC测试结果，会话ID: {}", testSession);
            
            DiscTestResponse response = discTestService.getTestResult(testSession);
            
            if (response == null) {
                return ResponseEntity.ok(ApiResponse.error("测试结果不存在"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取结果成功", response));
            
        } catch (Exception e) {
            log.error("获取DISC测试结果失败，会话ID: {}", testSession, e);
            return ResponseEntity.ok(ApiResponse.error("获取结果失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户最新的测试结果
     */
    @GetMapping("/latest/{userId}")
    public ResponseEntity<ApiResponse<DiscTestResponse>> getLatestTestResult(
            @PathVariable Long userId) {

        try {
            log.info("获取用户最新DISC测试结果，用户ID: {}", userId);

            DiscTestResponse response = discTestService.getLatestTestResult(userId);

            if (response == null) {
                return ResponseEntity.ok(ApiResponse.success("暂无测试记录", null));
            }

            return ResponseEntity.ok(ApiResponse.success("获取结果成功", response));

        } catch (Exception e) {
            log.error("获取用户最新DISC测试结果失败，用户ID: {}", userId, e);
            return ResponseEntity.ok(ApiResponse.error("获取结果失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户的DISC测试历史记录
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<List<DiscTestResponse>>> getTestHistory(
            @PathVariable Long userId) {

        try {
            log.info("获取用户DISC测试历史记录，用户ID: {}", userId);

            List<DiscTestResponse> history = discTestService.getTestHistory(userId);

            return ResponseEntity.ok(ApiResponse.success("获取历史记录成功", history));

        } catch (Exception e) {
            log.error("获取用户DISC测试历史记录失败，用户ID: {}", userId, e);
            return ResponseEntity.ok(ApiResponse.error("获取历史记录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取DISC测试说明
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<String>> getTestInfo() {
        
        String info = "DISC性格测试是一种基于威廉·莫尔顿·马斯顿理论的心理测评工具，" +
                     "通过40道选择题来评估个人的行为风格和性格特征。\n\n" +
                     "测试包含四个维度：\n" +
                     "D - 支配型（Dominance）：控制者，天生的领袖\n" +
                     "I - 影响型（Influence）：社交者，活泼的推动者\n" +
                     "S - 稳定型（Steadiness）：支持者，平和的协调者\n" +
                     "C - 谨慎型（Compliance）：完美主义者，专业分析师\n\n" +
                     "测试时间约10-15分钟，请根据第一印象快速选择最符合您的选项。";
        
        return ResponseEntity.ok(ApiResponse.success("获取测试说明成功", info));
    }
}
