package com.aiinterview.controller;

import com.aiinterview.model.dto.StudentProfileRequest;
import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.service.student.StudentProfileService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 学生档案控制器
 */
@Slf4j
@RestController
@RequestMapping("/student/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;
    private final JwtUtils jwtUtils;

    /**
     * 完善学生档案
     */
    @PostMapping("/complete")
    public ApiResponse<Map<String, Object>> completeProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody StudentProfileRequest request) {
        try {
            // 从token中获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserIdFromToken(actualToken);

            StudentProfile profile = studentProfileService.completeProfile(userId, request);

            Map<String, Object> result = new HashMap<>();
            result.put("profile", profile);
            result.put("profileCompleted", true);

            return ApiResponse.success("档案完善成功", result);
        } catch (Exception e) {
            log.error("档案完善失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生档案
     */
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getProfile(@RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserIdFromToken(actualToken);

            StudentProfile profile = studentProfileService.getByUserId(userId);
            boolean isCompleted = studentProfileService.isProfileCompleted(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("profile", profile);
            result.put("profileCompleted", isCompleted);

            return ApiResponse.success("获取档案成功", result);
        } catch (Exception e) {
            log.error("获取档案失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 检查档案完成状态
     */
    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> checkProfileStatus(@RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserIdFromToken(actualToken);

            boolean isCompleted = studentProfileService.isProfileCompleted(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("profileCompleted", isCompleted);

            return ApiResponse.success("获取状态成功", result);
        } catch (Exception e) {
            log.error("获取状态失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}