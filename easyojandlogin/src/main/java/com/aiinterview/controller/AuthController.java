package com.aiinterview.controller;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.LoginRequest;
import com.aiinterview.model.dto.RegisterRequest;
import com.aiinterview.model.entity.user.User;
import com.aiinterview.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);

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
            // 这里需要从token中解析用户信息
            // 暂时返回成功，具体实现需要在安全配置完成后
            Map<String, Object> result = new HashMap<>();
            result.put("role", "applicant"); // 临时数据

            return ApiResponse.success("获取角色成功", result);
        } catch (Exception e) {
            log.error("获取角色失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}
