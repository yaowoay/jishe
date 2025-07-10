package com.coldwind.easyoj.controller;

import com.coldwind.easyoj.domain.EmailVerificationCode;
import com.coldwind.easyoj.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class EmailController {
    @Autowired
    private EmailService emailService;

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/email/sendCode")
    public Map<String, Object> sendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        // 校验邮箱是否为空
        if (email == null || email.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", "邮箱不能为空");
            return result;
        }

        Map<String, Object> result = new HashMap<>();
        try {
            EmailVerificationCode verificationCode = emailService.sendCode(email);
            result.put("code", 200);
            result.put("message", "验证码发送成功，请注意查收");
            // 注意：生产环境不要返回验证码明文
            // result.put("verificationCode", verificationCode.getCode());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "验证码发送失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 验证邮箱验证码
     */
    @PostMapping("/email/verifyCode")
    public Map<String, Object> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        Map<String, Object> result = new HashMap<>();
        boolean isValid = emailService.verifyCode(email, code);
        if (isValid) {
            result.put("code", 200);
            result.put("message", "验证码验证成功");
        } else {
            result.put("code", 400);
            result.put("message", "验证码无效或已过期");
        }
        return result;
    }

    /**
     * 邮箱登录接口
     */
    @PostMapping("/email/login")
    public Map<String, Object> emailLogin(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String email = request.get("email");
        String code = request.get("code");
        Map<String, Object> result = new HashMap<>();
        boolean isValid = emailService.verifyCode(email, code);
        if (isValid) {
            // 将用户标识存入session
            httpRequest.getSession().setAttribute("userKey", email);
            result.put("code", 200);
            result.put("message", "登录成功");
        } else {
            result.put("code", 400);
            result.put("message", "验证码无效或已过期，登录失败");
        }
        return result;
    }
}