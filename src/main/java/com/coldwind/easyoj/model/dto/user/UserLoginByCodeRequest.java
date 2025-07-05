package com.coldwind.easyoj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginByCodeRequest implements Serializable {
    private String phone; // 手机号
    private String code;  // 验证码
}