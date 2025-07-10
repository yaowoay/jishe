package com.coldwind.easyoj.controller;

import com.coldwind.easyoj.domain.SmsVerificationCode;
import com.coldwind.easyoj.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SmsController {
    @Autowired
    private SmsService smsService;

    @PostMapping("/sms/sendCode")
    public Map<String, Object> sendCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        // 校验手机号是否为空
        if (phone == null || phone.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", "手机号不能为空");
            return result;
        }

        Map<String, Object> result = new HashMap<>();
        try {
            SmsVerificationCode verificationCode = smsService.sendCode(phone);
            result.put("code", 200);
            result.put("message", "验证码发送成功");
            // 注意：生产环境不要返回验证码明文
            result.put("verificationCode", verificationCode.getCode());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "验证码发送失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/sms/verifyCode")
    public Map<String, Object> verifyCode(@RequestParam String phone, @RequestParam String code) {
        Map<String, Object> result = new HashMap<>();
        boolean isValid = smsService.verifyCode(phone, code);
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
     * 短信登录接口
     * @param phone 手机号
     * @param code 验证码
     * @return 登录结果
     */
    @PostMapping("/sms/login")
    public Map<String, Object> smsLogin(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String phone = request.get("phone");
        String code = request.get("code");
        Map<String, Object> result = new HashMap<>();
        boolean isValid = smsService.verifyCode(phone, code);
        if (isValid) {
            // 将用户标识存入session
            httpRequest.getSession().setAttribute("userKey", phone);
            result.put("code", 200);
            result.put("message", "登录成功");
        } else {
            result.put("code", 400);
            result.put("message", "验证码无效或已过期，登录失败");
        }
        return result;
    }
}