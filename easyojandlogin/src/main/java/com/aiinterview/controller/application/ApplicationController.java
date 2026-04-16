package com.aiinterview.controller.application;

import com.aiinterview.model.dto.application.ApplicationDetailDTO;
import com.aiinterview.model.entity.application.Application;
import com.aiinterview.service.application.ApplicationService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简历投递申请控制器
 */
@Slf4j
@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JwtUtils jwtUtils;

    @PostMapping("/submit")
    public Map<String, Object> submitApplication(
            @RequestParam Long jobId,
            @RequestParam Long resumeId,
            HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }
            token = token.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            Application application = applicationService.submitApplication(userId, jobId, resumeId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "简历投递成功");
            result.put("data", application);
            return result;
        } catch (Exception e) {
            log.error("投递简历失败: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        }
    }

    @GetMapping("/history")
    public Map<String, Object> getApplicationHistory(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }
            token = token.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            List<Application> applications = applicationService.getApplicationsByUserId(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取投递历史成功");
            result.put("data", applications);
            return result;
        } catch (Exception e) {
            log.error("获取投递历史失败: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        }
    }

    @GetMapping("/submitted-jobs")
    public Map<String, Object> getSubmittedJobIds(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }
            token = token.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            List<Long> jobIds = applicationService.getSubmittedJobIds(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取已投递职位列表成功");
            result.put("data", jobIds);
            return result;
        } catch (Exception e) {
            log.error("获取已投递职位列表失败: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        }
    }

    @GetMapping("/details")
    public Map<String, Object> getApplicationDetails(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }
            token = token.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            List<ApplicationDetailDTO> details = applicationService.getSubmittedApplicationDetails(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取投递详情成功");
            result.put("data", details);
            return result;
        } catch (Exception e) {
            log.error("获取投递详情失败: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        }
    }

    @PostMapping("/withdraw/{applicationId}")
    public Map<String, Object> withdrawApplication(
            @PathVariable Long applicationId,
            HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }
            token = token.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            boolean withdrawResult = applicationService.withdrawApplication(applicationId, userId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", withdrawResult);
            result.put("message", withdrawResult ? "申请撤回成功" : "申请撤回失败");
            return result;
        } catch (Exception e) {
            log.error("撤回申请失败: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        }
    }
}
