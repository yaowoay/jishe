package com.aiinterview.controller.candidataAnswer;

import com.aiinterview.model.dto.candidataAnswer.CandidateAnswerStatsDTO;
import com.aiinterview.model.dto.writtenTest.WrittenTestGenerationResponse;
import com.aiinterview.model.entity.candidataAnswer.CandidateAnswerStats;
import com.aiinterview.service.application.ApplicationService;
import com.aiinterview.service.candidataAnswer.CandidateAnswerStatsService;
import com.aiinterview.service.writtenTest.WrittenTestGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应聘者答题统计信息控制器
 */
@Slf4j
@RestController
@RequestMapping("/candidate-answer-stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CandidateAnswerStatsController {

    // 添加一个静态初始化块来确认类被加载
    static {
        System.out.println("==========================================");
        System.out.println("=== CandidateAnswerStatsController 类已加载 ===");
        System.out.println("==========================================");
    }

    private final CandidateAnswerStatsService candidateAnswerStatsService;
    private final WrittenTestGenerationService writtenTestGenerationService;
    private final ApplicationService applicationService;
    
    /**
     * 保存或更新答题统计信息
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveOrUpdate(@Valid @RequestBody CandidateAnswerStatsDTO dto) {
        log.info("接收到保存答题统计信息请求，申请ID: {}", dto.getApplicationId());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            CandidateAnswerStats result = candidateAnswerStatsService.saveOrUpdate(dto);
            
            response.put("success", true);
            response.put("message", "答题统计信息保存成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("保存答题统计信息失败", e);
            response.put("success", false);
            response.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据申请ID获取答题统计信息
     */
    @GetMapping("/{applicationId}")
    public ResponseEntity<Map<String, Object>> getByApplicationId(@PathVariable Integer applicationId) {
        log.info("获取答题统计信息，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            CandidateAnswerStats stats = candidateAnswerStatsService.getByApplicationId(applicationId);
            
            if (stats != null) {
                response.put("success", true);
                response.put("data", stats);
            } else {
                response.put("success", false);
                response.put("message", "未找到相关记录");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取答题统计信息失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 最简单的测试接口
     */
    @GetMapping("/simple-test")
    public String simpleTest() {
        System.out.println("==========================================");
        System.out.println("=== 简单测试接口被调用 ===");
        System.out.println("==========================================");
        return "CandidateAnswerStatsController is working!";
    }

    /**
     * 测试接口 - 确认后端是否正常工作
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        System.out.println("==========================================");
        System.out.println("=== 测试接口被调用 ===");
        System.out.println("==========================================");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "后端服务正常工作");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * 设置笔试参数
     */
    @PostMapping("/written-test")
    public ResponseEntity<Map<String, Object>> setWrittenTestSettings(@RequestBody Map<String, Object> requestBody) {
        System.out.println("==========================================");
        System.out.println("=== 收到笔试设置请求 ===");
        System.out.println("请求体: " + requestBody);
        System.out.println("==========================================");

        Integer applicationId = (Integer) requestBody.get("applicationId");
        Integer answerCount = (Integer) requestBody.get("writtenAnswerCount");
        String answerRange = (String) requestBody.get("writtenAnswerRange");
        Boolean aiGenerated = (Boolean) requestBody.get("writtenAiGenerated");

        System.out.println("解析参数 - 申请ID: " + applicationId + ", 答题数量: " + answerCount + ", AI生成: " + aiGenerated);
        log.info("设置笔试参数，申请ID: {}, 答题数量: {}, 答题范围: {}, AI生成: {}", applicationId, answerCount, answerRange, aiGenerated);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (applicationId == null) {
                response.put("success", false);
                response.put("message", "申请ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // AI生成模式下，answerCount可以为空或0
            if (!Boolean.TRUE.equals(aiGenerated) && answerCount == null) {
                response.put("success", false);
                response.put("message", "自定义模式下答题数量不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 1. 保存笔试设置
            CandidateAnswerStats result = candidateAnswerStatsService.setWrittenTestSettings(applicationId, answerCount, answerRange, aiGenerated);

            // 2. 更新应聘者状态为"待笔试"
            try {
                applicationService.updateApplicationStatus(
                    applicationId.longValue(),
                    "待笔试",
                    "笔试题目已生成，请及时完成笔试"
                );
                log.info("应聘者状态已更新为'待笔试'，申请ID: {}", applicationId);
            } catch (Exception statusUpdateError) {
                log.warn("更新应聘者状态失败，但笔试设置成功，申请ID: {}", applicationId, statusUpdateError);
            }

            // 3. 如果是AI生成模式，触发笔试生成
            if (Boolean.TRUE.equals(aiGenerated)) {
                try {
                    // 直接调用笔试生成服务，传入applicationId
                    // 服务内部会自动获取应聘者信息和简历文件
                    WrittenTestGenerationResponse testResult = writtenTestGenerationService.generateWrittenTestByApplicationId(applicationId);

                    response.put("success", true);
                    response.put("message", "笔试已发送成功");
                    response.put("data", result);
                    response.put("testResult", testResult);

                } catch (Exception e) {
                    log.error("笔试生成失败", e);
                    // 即使笔试生成失败，设置保存成功的情况下也返回成功，但提示生成失败
                    response.put("success", true);
                    response.put("message", "笔试设置保存成功，但笔试生成失败: " + e.getMessage());
                    response.put("data", result);
                }
            } else {
                response.put("success", true);
                response.put("message", "笔试已发送成功");
                response.put("data", result);
            }

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("==========================================");
            System.out.println("=== 笔试设置失败 ===");
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            System.out.println("==========================================");

            log.error("设置笔试参数失败", e);
            response.put("success", false);
            response.put("message", "设置失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 设置面试参数
     */
    @PostMapping("/interview")
    public ResponseEntity<Map<String, Object>> setInterviewSettings(@RequestBody Map<String, Object> requestBody) {
        Integer applicationId = (Integer) requestBody.get("applicationId");
        Integer answerCount = (Integer) requestBody.get("interviewAnswerCount");
        String answerRange = (String) requestBody.get("interviewAnswerRange");
        Boolean aiGenerated = (Boolean) requestBody.get("interviewAiGenerated");

        log.info("设置面试参数，申请ID: {}, 答题数量: {}, 答题范围: {}, AI生成: {}", applicationId, answerCount, answerRange, aiGenerated);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (applicationId == null || answerCount == null) {
                response.put("success", false);
                response.put("message", "申请ID和答题数量不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            CandidateAnswerStats result = candidateAnswerStatsService.setInterviewSettings(applicationId, answerCount, answerRange, aiGenerated);
            
            response.put("success", true);
            response.put("message", "面试设置保存成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("设置面试参数失败", e);
            response.put("success", false);
            response.put("message", "设置失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量获取答题统计信息
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> getBatchByApplicationIds(@RequestBody Map<String, Object> requestBody) {
        @SuppressWarnings("unchecked")
        List<Integer> applicationIds = (List<Integer>) requestBody.get("applicationIds");
        
        log.info("批量获取答题统计信息，申请ID数量: {}", applicationIds != null ? applicationIds.size() : 0);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CandidateAnswerStats> statsList = candidateAnswerStatsService.getByApplicationIds(applicationIds);
            
            response.put("success", true);
            response.put("data", statsList);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("批量获取答题统计信息失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 删除答题统计信息
     */
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Map<String, Object>> deleteByApplicationId(@PathVariable Integer applicationId) {
        log.info("删除答题统计信息，申请ID: {}", applicationId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = candidateAnswerStatsService.deleteByApplicationId(applicationId);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "删除成功");
            } else {
                response.put("success", false);
                response.put("message", "未找到相关记录或删除失败");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("删除答题统计信息失败", e);
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("获取答题统计信息统计数据");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> statistics = candidateAnswerStatsService.getStatistics();
            
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            response.put("success", false);
            response.put("message", "获取统计数据失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取有笔试设置的记录
     */
    @GetMapping("/written-test-settings")
    public ResponseEntity<Map<String, Object>> getWithWrittenTestSettings() {
        log.info("获取有笔试设置的记录");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CandidateAnswerStats> statsList = candidateAnswerStatsService.getWithWrittenTestSettings();
            
            response.put("success", true);
            response.put("data", statsList);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取笔试设置记录失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取有面试设置的记录
     */
    @GetMapping("/interview-settings")
    public ResponseEntity<Map<String, Object>> getWithInterviewSettings() {
        log.info("获取有面试设置的记录");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CandidateAnswerStats> statsList = candidateAnswerStatsService.getWithInterviewSettings();
            
            response.put("success", true);
            response.put("data", statsList);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取面试设置记录失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
