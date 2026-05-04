package com.aiinterview.controller;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.request.LoginRequest;
import com.aiinterview.model.dto.request.RegisterRequest;
import com.aiinterview.model.entity.user.User;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.mapper.CompanyMapper;
import com.aiinterview.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CompanyMapper companyMapper;  // 注入 CompanyMapper

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);

            // ✅ 如果是公司角色，自动创建公司基本信息
            if ("company".equals(user.getRole())) {
                Company company = new Company();
                company.setUserId(user.getUserId());
                company.setCompanyName("待完善");      // 临时名称，后续可修改
                company.setIndustry("互联网");         // 临时行业
                company.setScale("1-50人");           // 临时规模
                company.setContactPhone("待完善");     // 临时电话
                company.setVerifyStatus("pending");    // 待审核状态
                company.setCreditScore(60);            // 默认信用分
                company.setProfileCompletion(0);       // 资料完成度0
                company.setCreatedAt(LocalDateTime.now());
                company.setUpdatedAt(LocalDateTime.now());

                companyMapper.insert(company);
                log.info("为公司用户自动创建公司记录: userId={}, companyId={}", user.getUserId(), company.getCompanyId());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getUserId());
            result.put("email", user.getEmail());
            result.put("role", user.getRole());

            return ApiResponse.success("注册成功", result);
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = userService.login(request);
            User user = userService.findByEmail(request.getEmail());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", user.getUserId());
            result.put("email", user.getEmail());
            result.put("role", user.getRole());
            result.put("profileCompleted", user.getProfileCompleted() != null && user.getProfileCompleted() == 1);

            return ApiResponse.success("登录成功", result);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 检查用户角色
     */
    @GetMapping("/check-role")
    public ApiResponse<Map<String, Object>> checkRole(@RequestHeader("Authorization") String token) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("role", "applicant");

            return ApiResponse.success("获取角色成功", result);
        } catch (Exception e) {
            log.error("获取角色失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}