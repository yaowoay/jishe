package com.aiinterview.controller;

import com.aiinterview.model.dto.CompanyProfileRequest;
import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.service.company.CompanyProfileService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业档案控制器
 */
@Slf4j
@RestController
@RequestMapping("/company/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompanyProfileController {
    
    private final CompanyProfileService companyProfileService;
    private final JwtUtils jwtUtils;
    
    /**
     * 完善企业档案
     */
    @PostMapping("/complete")
    public ApiResponse<Map<String, Object>> completeProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CompanyProfileRequest request) {
        try {
            // 从token中获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserIdFromToken(actualToken);
            
            Company company = companyProfileService.completeProfile(userId, request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("profile", company);
            result.put("profileCompleted", true);
            
            return ApiResponse.success("企业档案完善成功", result);
        } catch (Exception e) {
            log.error("企业档案完善失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新企业档案
     */
    @PutMapping("/update")
    public ApiResponse<Map<String, Object>> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CompanyProfileRequest request) {
        try {
            // 从token中获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserIdFromToken(actualToken);
            
            Company company = companyProfileService.updateProfile(userId, request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("profile", company);
            result.put("profileCompleted", true);
            
            return ApiResponse.success("企业档案更新成功", result);
        } catch (Exception e) {
            log.error("企业档案更新失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取企业档案
     */
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getProfile(@RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserIdFromToken(actualToken);
            
            Company company = companyProfileService.getByUserId(userId);
            boolean isCompleted = companyProfileService.isProfileCompleted(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("profile", company);
            result.put("profileCompleted", isCompleted);
            
            return ApiResponse.success("获取企业档案成功", result);
        } catch (Exception e) {
            log.error("获取企业档案失败: {}", e.getMessage());
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
            
            boolean isCompleted = companyProfileService.isProfileCompleted(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("profileCompleted", isCompleted);
            
            return ApiResponse.success("获取状态成功", result);
        } catch (Exception e) {
            log.error("获取状态失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}