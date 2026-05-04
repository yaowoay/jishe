package com.aiinterview.controller.interview;

import com.aiinterview.model.dto.interview.InterviewExamDTO;
import com.aiinterview.model.entity.interview.InterviewAnswer;
import com.aiinterview.model.entity.interview.InterviewResult;
import com.aiinterview.service.interview.InterviewExamService;
import com.aiinterview.service.application.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 面试考试控制器
 */
@Slf4j
@RestController
@RequestMapping("/interview")
@CrossOrigin(origins = "*")
public class InterviewExamController {

    @Autowired
    private InterviewExamService interviewExamService;

    @Autowired
    private ApplicationService applicationService;

    /**
     * 生成面试题目
     */
    @PostMapping("/generate/{applicationId}")
    public ResponseEntity<Map<String, Object>> generateInterviewQuestions(@PathVariable Integer applicationId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("生成面试题目，申请ID: {}", applicationId);
            InterviewResult result = interviewExamService.generateInterviewQuestions(applicationId);

            // 更新应聘者状态为"待面试"
            try {
                applicationService.updateApplicationStatus(
                    applicationId.longValue(),
                    "待面试",
                    "面试题目已生成，请及时参加面试"
                );
                log.info("应聘者状态已更新为'待面试'，申请ID: {}", applicationId);
            } catch (Exception statusUpdateError) {
                log.warn("更新应聘者状态失败，但面试题目生成成功，申请ID: {}", applicationId, statusUpdateError);
            }

            response.put("success", true);
            response.put("message", "面试题目生成成功");
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("生成面试题目失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取面试考试信息
     */
    @GetMapping("/exam/{applicationId}")
    public ResponseEntity<Map<String, Object>> getExamInfo(@PathVariable Integer applicationId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("获取面试考试信息，申请ID: {}", applicationId);
            InterviewExamDTO examInfo = interviewExamService.getExamInfo(applicationId);
            
            response.put("success", true);
            response.put("data", examInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取面试考试信息失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 开始面试考试
     */
    @PostMapping("/start/{applicationId}")
    public ResponseEntity<Map<String, Object>> startExam(@PathVariable Integer applicationId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("开始面试考试，申请ID: {}", applicationId);
            InterviewExamDTO examInfo = interviewExamService.startExam(applicationId);
            
            response.put("success", true);
            response.put("message", "面试开始成功");
            response.put("data", examInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("开始面试考试失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 提交面试答案
     */
    @PostMapping("/submit/{applicationId}")
    public ResponseEntity<Map<String, Object>> submitAnswers(
            @PathVariable Integer applicationId,
            @RequestBody Map<String, Object> requestBody) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("提交面试答案，申请ID: {}", applicationId);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> answers = (List<Map<String, Object>>) requestBody.get("answers");
            
            InterviewAnswer result = interviewExamService.submitAnswers(applicationId, answers);
            
            response.put("success", true);
            response.put("message", "面试答案提交成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("提交面试答案失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取面试结果
     */
    @GetMapping("/result/{applicationId}")
    public ResponseEntity<Map<String, Object>> getInterviewResult(@PathVariable Integer applicationId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("获取面试结果，申请ID: {}", applicationId);
            InterviewAnswer result = interviewExamService.getInterviewResult(applicationId);
            
            response.put("success", true);
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取面试结果失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 实时保存面试回答
     */
    @PostMapping("/save-answer/{applicationId}")
    public ResponseEntity<Map<String, Object>> saveInterviewAnswer(
            @PathVariable Integer applicationId,
            @RequestBody Map<String, Object> answerData) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("实时保存面试回答，申请ID: {}", applicationId);

            boolean success = interviewExamService.saveAnswerRealTime(applicationId, answerData);

            response.put("success", success);
            response.put("message", success ? "保存成功" : "保存失败");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("实时保存面试回答失败", e);

            response.put("success", false);
            response.put("message", "保存失败: " + e.getMessage());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 更新面试状态
     */
    @PutMapping("/status/{applicationId}")
    public ResponseEntity<Map<String, Object>> updateInterviewStatus(
            @PathVariable Integer applicationId,
            @RequestBody Map<String, String> statusData) {
        Map<String, Object> response = new HashMap<>();

        try {
            String status = statusData.get("status");
            log.info("更新面试状态，申请ID: {}, 状态: {}", applicationId, status);

            boolean success = interviewExamService.updateInterviewStatus(applicationId, status);

            response.put("success", success);
            response.put("message", success ? "状态更新成功" : "状态更新失败");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新面试状态失败", e);

            response.put("success", false);
            response.put("message", "状态更新失败: " + e.getMessage());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取面试状态
     */
    @GetMapping("/status/{applicationId}")
    public ResponseEntity<Map<String, Object>> getInterviewStatus(@PathVariable Integer applicationId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String status = interviewExamService.getInterviewStatus(applicationId);
            
            response.put("success", true);
            response.put("status", status);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取面试状态失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 重新开始面试
     */
    @PostMapping("/restart/{applicationId}")
    public ResponseEntity<Map<String, Object>> restartInterview(@PathVariable Integer applicationId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("重新开始面试，申请ID: {}", applicationId);
            InterviewExamDTO examInfo = interviewExamService.restartInterview(applicationId);
            
            response.put("success", true);
            response.put("message", "面试重新开始成功");
            response.put("data", examInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("重新开始面试失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 删除面试记录
     */
    @DeleteMapping("/delete/{applicationId}")
    public ResponseEntity<Map<String, Object>> deleteInterviewRecord(@PathVariable Integer applicationId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("删除面试记录，申请ID: {}", applicationId);
            boolean success = interviewExamService.deleteInterviewRecord(applicationId);
            
            response.put("success", success);
            response.put("message", success ? "面试记录删除成功" : "面试记录删除失败");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除面试记录失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
