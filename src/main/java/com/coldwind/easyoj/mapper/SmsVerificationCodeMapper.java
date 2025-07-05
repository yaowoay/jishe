package com.coldwind.easyoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coldwind.easyoj.domain.SmsVerificationCode;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface SmsVerificationCodeMapper extends BaseMapper<SmsVerificationCode> {
    SmsVerificationCode selectValidVerificationCode(String phone, String code, Date currentTime);
}