package com.coldwind.easyoj.controller;

import com.coldwind.easyoj.domain.SmsVerificationCode;
import com.coldwind.easyoj.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SmsController {
    @Autowired
    private SmsService smsService;

    @PostMapping("/sms/sendCode")
    public Map<String, Object> sendCode(@RequestParam String phone) {
        Map<String, Object> result = new HashMap<>();
        try {
            SmsVerificationCode verificationCode = smsService.sendCode(phone);
            // 实际开发中不应返回验证码，这里仅为测试方便
            result.put("code", 200);
            result.put("message", "验证码发送成功");
            result.put("verificationCode", verificationCode.getCode());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "验证码发送失败");
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
    public Map<String, Object> smsLogin(@RequestParam String phone, @RequestParam String code) {
        Map<String, Object> result = new HashMap<>();
        boolean isValid = smsService.verifyCode(phone, code);
        if (isValid) {
            result.put("code", 200);
            result.put("message", "登录成功");
            // 这里可以添加生成 token 等登录后续逻辑
        } else {
            result.put("code", 400);
            result.put("message", "验证码无效或已过期，登录失败");
        }
        return result;
    }
}