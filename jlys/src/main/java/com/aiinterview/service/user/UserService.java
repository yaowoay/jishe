package com.aiinterview.service.user;

import com.aiinterview.model.dto.LoginRequest;
import com.aiinterview.model.dto.RegisterRequest;
import com.aiinterview.model.entity.user.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    String login(LoginRequest request);
    
    /**
     * 根据邮箱查找用户
     */
    User findByEmail(String email);
    
    /**
     * 根据ID查找用户
     */
    User findById(Long userId);
}
