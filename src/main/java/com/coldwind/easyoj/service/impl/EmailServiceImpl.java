// 将第一行的包声明从：
package com.coldwind.easyoj.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.coldwind.easyoj.domain.EmailVerificationCode;
import com.coldwind.easyoj.mapper.EmailVerificationCodeMapper;
import com.coldwind.easyoj.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;


@Service
public class EmailServiceImpl implements EmailService {

    @Autowired  // 添加依赖注入注解
    private JavaMailSender mailSender;

    @Autowired  // 添加依赖注入注解
    private EmailVerificationCodeMapper emailVerificationCodeMapper;

    // 验证码有效期（分钟）
    private static final int CODE_EXPIRE_MINUTES = 10;

    // 发件人邮箱（需与application.yml中配置一致）
    private static final String FROM_EMAIL = "3167967427@qq.com";

    @Override
    public EmailVerificationCode sendCode(String email) throws Exception {
        // 1. 生成6位随机验证码
        String code = generateRandomCode();

        // 2. 保存验证码到数据库
        EmailVerificationCode verificationCode = new EmailVerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setCreateTime(new Date());
        verificationCode.setExpireTime(new Date(System.currentTimeMillis() + CODE_EXPIRE_MINUTES * 60 * 1000));
        verificationCode.setStatus(0); // 0-未使用
        emailVerificationCodeMapper.insert(verificationCode);

        // 3. 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(email);
        message.setSubject("登录验证码");
        message.setText("您的登录验证码为: " + code + "，有效期" + CODE_EXPIRE_MINUTES + "分钟，请勿泄露给他人。");
        mailSender.send(message);

        return verificationCode;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        // 1. 查询未使用且未过期的验证码
        QueryWrapper<EmailVerificationCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        queryWrapper.eq("code", code);
        queryWrapper.eq("status", 0); // 未使用
        queryWrapper.ge("expire_time", new Date()); // 未过期

        EmailVerificationCode verificationCode = emailVerificationCodeMapper.selectOne(queryWrapper);

        if (verificationCode != null) {
            // 2. 标记验证码为已使用
            verificationCode.setStatus(1);
            emailVerificationCodeMapper.updateById(verificationCode);
            return true;
        }
        return false;
    }

    /**
     * 生成6位随机数字验证码
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}