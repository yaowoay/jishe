package com.coldwind.easyoj.service.impl;

import com.coldwind.easyoj.domain.SmsVerificationCode;
import com.coldwind.easyoj.mapper.SmsVerificationCodeMapper;
import com.coldwind.easyoj.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;

@Service
public class SmsServiceImpl implements SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    @Autowired
    private SmsVerificationCodeMapper smsVerificationCodeMapper;

    @Override
    @Transactional
    public SmsVerificationCode sendCode(String phone) {
        // 生成 6 位随机验证码
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }

        // 设置过期时间为 30 分钟后
        Date expireTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000);

        SmsVerificationCode verificationCode = new SmsVerificationCode();
        verificationCode.setPhone(phone);
        verificationCode.setCode(code.toString());
        verificationCode.setExpireTime(expireTime);
        //打印验证码
        logger.info("验证码: {}", code.toString());

        try {
            // 插入数据库
            smsVerificationCodeMapper.insert(verificationCode);
        } catch (Exception e) {
            logger.error("插入短信验证码到数据库失败，手机号: {}", phone, e);
            throw new RuntimeException("插入短信验证码到数据库失败", e);
        }

        return verificationCode;
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        Date currentTime = new Date();
        SmsVerificationCode verificationCode = smsVerificationCodeMapper.selectValidVerificationCode(phone, code, currentTime);
        return verificationCode != null;
    }
}