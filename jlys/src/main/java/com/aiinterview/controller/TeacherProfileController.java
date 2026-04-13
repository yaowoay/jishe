package com.aiinterview.controller;

import com.aiinterview.model.dto.TeacherProfileRequest;
import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.entity.teacher.Teacher;
import com.aiinterview.service.teacher.TeacherProfileService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 教师档案控制器
 */
@Slf4j
@RestController
@RequestMapping("/teacher/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeacherProfileController {
    
    private final TeacherProfileService teacherProfileService;
    private final JwtUtils jwtUtils;
    
    /**
     * 完善教师档案
     */
    @PostMapping("/complete")
    public ApiResponse<Map<String, Object>> completeProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TeacherProfileRequest request) {
        try {
            // 从token中获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserIdFromToken(actualToken);
            
            Teacher teacher = teacherProfileService.completeProfile(userId, request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("profile", teacher);
            result.put("profileCompleted", true);
            
            return ApiResponse.success("教师档案完善成功", result);
        } catch (Exception e) {
            log.error("教师档案完善失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新教师档案
     */
    @PutMapping("/update")
    public ApiResponse<Map<String, Object>> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TeacherProfileRequest request) {
        try {
            // 从token中获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserIdFromToken(actualToken);
            
            Teacher teacher = teacherProfileService.updateProfile(userId, request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("profile", teacher);
            result.put("profileCompleted", true);
            
            return ApiResponse.success("教师档案更新成功", result);
        } catch (Exception e) {
            log.error("教师档案更新失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取教师档案
     */
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getProfile(@RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserIdFromToken(actualToken);
            
            Teacher teacher = teacherProfileService.getByUserId(userId);
            boolean isCompleted = teacherProfileService.isProfileCompleted(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("profile", teacher);
            result.put("profileCompleted", isCompleted);
            
            return ApiResponse.success("获取教师档案成功", result);
        } catch (Exception e) {
            log.error("获取教师档案失败: {}", e.getMessage());
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
            
            boolean isCompleted = teacherProfileService.isProfileCompleted(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("profileCompleted", isCompleted);
            
            return ApiResponse.success("获取状态成功", result);
        } catch (Exception e) {
            log.error("获取状态失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}