package com.coldwind.easyoj.service;

import com.coldwind.easyoj.domain.SmsVerificationCode;

public interface SmsService {
    SmsVerificationCode sendCode(String phone);
    boolean verifyCode(String phone, String code);
}