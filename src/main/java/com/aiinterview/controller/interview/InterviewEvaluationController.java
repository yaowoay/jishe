package com.aiinterview.controller.interview;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.interview.InterviewEvaluationRequest;
import com.aiinterview.model.dto.interview.InterviewEvaluationResponse;
import com.aiinterview.service.interview.InterviewEvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 面试评估与资源推荐控制器
 */
@Slf4j
@RestController
@RequestMapping("/interview-evaluation")
public class InterviewEvaluationController {
    
    @Autowired
    private InterviewEvaluationService interviewEvaluationService;
    
    /**
     * 执行面试评估与资源推荐
     */
    @PostMapping("/evaluate")
    public ResponseEntity<ApiResponse<InterviewEvaluationResponse>> evaluateInterview(
            @RequestBody InterviewEvaluationRequest request) {
        try {
            log.info("收到面试评估请求，申请ID: {}", request.getApplicationId());
            
            InterviewEvaluationResponse response = interviewEvaluationService.evaluateInterview(request);
            
            log.info("面试评估完成，申请ID: {}, 评估ID: {}", 
                    request.getApplicationId(), response.getEvaluationId());
            
            return ResponseEntity.ok(ApiResponse.success("面试评估完成", response));
            
        } catch (Exception e) {
            log.error("面试评估失败，申请ID: {}", request.getApplicationId(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("面试评估失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据申请ID获取评估结果
     */
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<ApiResponse<InterviewEvaluationResponse>> getEvaluationByApplicationId(
            @PathVariable Integer applicationId) {
        try {
            log.info("获取评估结果，申请ID: {}", applicationId);
            
            InterviewEvaluationResponse response = interviewEvaluationService.getEvaluationByApplicationId(applicationId);
            
            return ResponseEntity.ok(ApiResponse.success("获取评估结果成功", response));
            
        } catch (Exception e) {
            log.error("获取评估结果失败，申请ID: {}", applicationId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取评估结果失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据评估ID获取评估结果
     */
    @GetMapping("/{evaluationId}")
    public ResponseEntity<ApiResponse<InterviewEvaluationResponse>> getEvaluationById(
            @PathVariable Long evaluationId) {
        try {
            log.info("获取评估结果，评估ID: {}", evaluationId);
            
            InterviewEvaluationResponse response = interviewEvaluationService.getEvaluationById(evaluationId);
            
            return ResponseEntity.ok(ApiResponse.success("获取评估结果成功", response));
            
        } catch (Exception e) {
            log.error("获取评估结果失败，评估ID: {}", evaluationId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取评估结果失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取候选人的所有评估记录
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<List<InterviewEvaluationResponse>>> getEvaluationsByCandidateId(
            @PathVariable String candidateId) {
        try {
            log.info("获取候选人评估记录，候选人ID: {}", candidateId);
            
            List<InterviewEvaluationResponse> responses = interviewEvaluationService.getEvaluationsByCandidateId(candidateId);
            
            return ResponseEntity.ok(ApiResponse.success("获取候选人评估记录成功", responses));
            
        } catch (Exception e) {
            log.error("获取候选人评估记录失败，候选人ID: {}", candidateId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取候选人评估记录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 重新评估面试结果
     */
    @PostMapping("/re-evaluate/{applicationId}")
    public ResponseEntity<ApiResponse<InterviewEvaluationResponse>> reEvaluateInterview(
            @PathVariable Integer applicationId) {
        try {
            log.info("重新评估面试，申请ID: {}", applicationId);
            
            InterviewEvaluationResponse response = interviewEvaluationService.reEvaluateInterview(applicationId);
            
            log.info("重新评估完成，申请ID: {}, 评估ID: {}", 
                    applicationId, response.getEvaluationId());
            
            return ResponseEntity.ok(ApiResponse.success("重新评估完成", response));
            
        } catch (Exception e) {
            log.error("重新评估失败，申请ID: {}", applicationId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("重新评估失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除评估记录
     */
    @DeleteMapping("/application/{applicationId}")
    public ResponseEntity<ApiResponse<String>> deleteEvaluation(
            @PathVariable Integer applicationId) {
        try {
            log.info("删除评估记录，申请ID: {}", applicationId);
            
            boolean success = interviewEvaluationService.deleteEvaluation(applicationId);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("删除评估记录成功", "success"));
            } else {
                return ResponseEntity.ok(ApiResponse.success("未找到评估记录", "not_found"));
            }
            
        } catch (Exception e) {
            log.error("删除评估记录失败，申请ID: {}", applicationId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("删除评估记录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取最近的评估记录
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<InterviewEvaluationResponse>>> getRecentEvaluations(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("获取最近评估记录，限制数量: {}", limit);
            
            List<InterviewEvaluationResponse> responses = interviewEvaluationService.getRecentEvaluations(limit);
            
            return ResponseEntity.ok(ApiResponse.success("获取最近评估记录成功", responses));
            
        } catch (Exception e) {
            log.error("获取最近评估记录失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取最近评估记录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 验证资源链接
     */
    @PostMapping("/{evaluationId}/verify-resources")
    public ResponseEntity<ApiResponse<String>> verifyResourceLinks(
            @PathVariable Long evaluationId) {
        try {
            log.info("验证资源链接，评估ID: {}", evaluationId);
            
            boolean success = interviewEvaluationService.verifyResourceLinks(evaluationId);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("资源链接验证完成", "success"));
            } else {
                return ResponseEntity.ok(ApiResponse.success("资源链接验证失败", "failed"));
            }
            
        } catch (Exception e) {
            log.error("验证资源链接失败，评估ID: {}", evaluationId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("验证资源链接失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取评估统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<InterviewEvaluationService.EvaluationStatistics>> getEvaluationStatistics() {
        try {
            log.info("获取评估统计信息");
            
            InterviewEvaluationService.EvaluationStatistics statistics = 
                    interviewEvaluationService.getEvaluationStatistics();
            
            return ResponseEntity.ok(ApiResponse.success("获取评估统计信息成功", statistics));
            
        } catch (Exception e) {
            log.error("获取评估统计信息失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取评估统计信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 快速评估接口（仅需申请ID）
     */
    @PostMapping("/quick-evaluate/{applicationId}")
    public ResponseEntity<ApiResponse<InterviewEvaluationResponse>> quickEvaluate(
            @PathVariable Integer applicationId) {
        try {
            log.info("快速评估，申请ID: {}", applicationId);
            
            InterviewEvaluationRequest request = InterviewEvaluationRequest.builder()
                    .applicationId(applicationId)
                    .evaluationType("full")
                    .forceReEvaluate(false)
                    .build();
            
            InterviewEvaluationResponse response = interviewEvaluationService.evaluateInterview(request);
            
            log.info("快速评估完成，申请ID: {}, 评估ID: {}", 
                    applicationId, response.getEvaluationId());
            
            return ResponseEntity.ok(ApiResponse.success("快速评估完成", response));
            
        } catch (Exception e) {
            log.error("快速评估失败，申请ID: {}", applicationId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("快速评估失败: " + e.getMessage()));
        }
    }
}
