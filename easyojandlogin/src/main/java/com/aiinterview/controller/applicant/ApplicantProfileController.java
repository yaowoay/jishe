package com.aiinterview.controller.applicant;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.applicant.ApplicantProfileDTO;
import com.aiinterview.service.applicat.ApplicantService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 求职者个人信息管理控制器
 * 修复：已移除 ApplicantMapper 依赖，改用 ApplicantService
 */
@Slf4j
@RestController
@RequestMapping("/applicant/profile")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
public class ApplicantProfileController {

    private final ApplicantService applicantService;
    private final JwtUtils jwtUtils;

    @GetMapping("/info")
    public ApiResponse<ApplicantProfileDTO> getProfile(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ApiResponse.error("用户未登录");
            }
            token = token.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }

            ApplicantProfileDTO profile = applicantService.getApplicantByUserId(userId);
            if (profile == null) {
                return ApiResponse.error("求职者信息不存在");
            }

            return ApiResponse.success("获取求职者信息成功", profile);
        } catch (Exception e) {
            log.error("获取求职者信息失败: {}", e.getMessage());
            return ApiResponse.error("获取求职者信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ApiResponse<ApplicantProfileDTO> saveProfile(
            @RequestBody ApplicantProfileDTO dto,
            HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ApiResponse.error("用户未登录");
            }
            token = token.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }

            ApplicantProfileDTO result = applicantService.saveOrUpdateApplicant(userId, dto);
            return ApiResponse.success("保存求职者信息成功", result);
        } catch (Exception e) {
            log.error("保存求职者信息失败: {}", e.getMessage());
            return ApiResponse.error("保存求职者信息失败: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    public ApiResponse<Map<String, Object>> checkProfile(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ApiResponse.error("用户未登录");
            }
            token = token.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }

            boolean hasProfile = applicantService.hasApplicantProfile(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("hasProfile", hasProfile);

            return ApiResponse.success("检查成功", result);
        } catch (Exception e) {
            log.error("检查求职者档案失败: {}", e.getMessage());
            return ApiResponse.error("检查失败: " + e.getMessage());
        }
    }
}
