package com.coldwind.easyoj.service;

import com.coldwind.easyoj.domain.EmailVerificationCode;

public interface EmailService {
    /**
     * 发送邮箱验证码
     */
    EmailVerificationCode sendCode(String email) throws Exception;

    /**
     * 验证邮箱验证码
     */
    boolean verifyCode(String email, String code);
}