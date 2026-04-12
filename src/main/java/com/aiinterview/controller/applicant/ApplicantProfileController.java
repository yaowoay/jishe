package com.aiinterview.controller.applicant;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.applicant.ApplicantProfileDTO;
import com.aiinterview.model.entity.applicant.Applicant;
import com.aiinterview.mapper.ApplicantMapper;
import com.aiinterview.service.applicat.ApplicantService;
import com.aiinterview.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 求职者个人信息管理控制器
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
    private final ApplicantMapper applicantMapper;
    
    /**
     * 测试接口 - 简单保存
     */
    @PostMapping("/test-save")
    public ApiResponse<Map<String, Object>> testSave(@RequestBody Map<String, Object> data) {
        try {
            log.info("收到测试保存请求: {}", data);

            Map<String, Object> result = new HashMap<>();
            result.put("received", data);
            result.put("timestamp", System.currentTimeMillis());
            result.put("message", "测试保存成功");

            return ApiResponse.success("测试成功", result);
        } catch (Exception e) {
            log.error("测试保存失败: {}", e.getMessage());
            return ApiResponse.error("测试失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的求职者信息
     */
    @GetMapping("/get")
    public ApiResponse<ApplicantProfileDTO> getApplicantProfile(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            ApplicantProfileDTO profile = applicantService.getApplicantByUserId(userId);

            if (profile == null) {
                return ApiResponse.success("暂无个人信息", null);
            }

            return ApiResponse.success("获取成功", profile);
        } catch (Exception e) {
            log.error("获取求职者信息失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 保存或更新求职者信息
     */
    @PostMapping
    public ApiResponse<ApplicantProfileDTO> saveOrUpdateApplicantProfile(
            @RequestBody ApplicantProfileDTO applicantProfileDTO,
            HttpServletRequest request) {
        try {
            log.info("收到保存求职者信息请求，数据: {}", applicantProfileDTO);

            Long userId = getUserIdFromToken(request);
            log.info("获取到用户ID: {}", userId);

            ApplicantProfileDTO result = applicantService.saveOrUpdateApplicant(userId, applicantProfileDTO);
            log.info("保存成功，返回数据: {}", result);

            return ApiResponse.success("保存成功", result);
        } catch (Exception e) {
            log.error("保存求职者信息失败: {}", e.getMessage(), e);
            return ApiResponse.error("保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查是否已有求职者信息
     */
    @GetMapping("/exists")
    public ApiResponse<Boolean> hasApplicantProfile(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            boolean exists = applicantService.hasApplicantProfile(userId);
            return ApiResponse.success("检查成功", exists);
        } catch (Exception e) {
            log.error("检查求职者信息失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 从Token中获取求职者ID
     */
    private Long getApplicantIdFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("未找到有效的认证信息");
            }

            // 提取token
            token = token.substring(7);

            // 从token中获取用户ID
            Long userId = jwtUtils.getUserIdFromToken(token);

            // 验证用户角色是否为求职者
            String role = jwtUtils.getRoleFromToken(token);
            if (!"applicant".equals(role)) {
                throw new RuntimeException("用户角色不正确，需要求职者权限");
            }

            // 根据用户ID查询求职者ID
            QueryWrapper<Applicant> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            Applicant applicant = applicantMapper.selectOne(queryWrapper);

            if (applicant == null) {
                // 如果没有求职者信息，返回用户ID（用于创建新的求职者信息）
                log.info("未找到求职者信息，将使用用户ID: {}", userId);
                return userId;
            }

            return applicant.getApplicantId();
        } catch (Exception e) {
            log.error("获取求职者ID失败: {}", e.getMessage());
            throw new RuntimeException("获取求职者信息失败: " + e.getMessage());
        }
    }

    /**
     * 从Token中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("未找到有效的认证信息");
            }

            // 提取token
            token = token.substring(7);

            // 从token中获取用户ID
            Long userId = jwtUtils.getUserIdFromToken(token);

            // 验证用户角色是否为求职者
            String role = jwtUtils.getRoleFromToken(token);
            if (!"applicant".equals(role)) {
                throw new RuntimeException("用户角色不正确，需要求职者权限");
            }

            return userId;
        } catch (Exception e) {
            log.error("获取用户ID失败: {}", e.getMessage());
            throw new RuntimeException("认证失败: " + e.getMessage());
        }
    }
}
