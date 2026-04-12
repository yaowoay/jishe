package com.aiinterview.service.impl.user;

import com.aiinterview.mapper.*;
import com.aiinterview.model.dto.LoginRequest;
import com.aiinterview.model.dto.RegisterRequest;
import com.aiinterview.model.entity.user.User;
import com.aiinterview.service.user.UserService;
import com.aiinterview.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    @Override
    @Transactional
    public User register(RegisterRequest request) {
        // 检查邮箱是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", request.getEmail());
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        user.setProfileCompleted(0); // 初始化为未完善
        
        userMapper.insert(user);
        
        // 注册成功后不再创建详细信息，等待用户后续完善
        
        return user;
    }
    
    @Override
    public String login(LoginRequest request) {
        // 查找用户
        User user = findByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT token
        return jwtUtils.generateToken(user.getUserId(), user.getEmail(), user.getRole());
    }
    
    @Override
    public User findByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public User findById(Long userId) {
        return userMapper.selectById(userId);
    }
}
