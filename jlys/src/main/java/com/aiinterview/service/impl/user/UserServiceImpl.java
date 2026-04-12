package com.aiinterview.service.impl.user;

import com.aiinterview.mapper.*;
import com.aiinterview.model.dto.LoginRequest;
import com.aiinterview.model.dto.RegisterRequest;
import com.aiinterview.model.entity.applicant.Applicant;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.model.entity.teacher.StudentProfile;
import com.aiinterview.model.entity.teacher.Teacher;
import com.aiinterview.model.entity.user.User;
import com.aiinterview.service.user.UserService;
import com.aiinterview.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final CompanyMapper companyMapper;
    private final TeacherMapper teacherMapper;
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
        user.setProfileCompleted(0); // 初始化为未完善
        
        userMapper.insert(user);
        
        // 根据角色创建对应的详细信息
        if ("company".equals(request.getRole())) {
            Company company = new Company();
            company.setUserId(user.getUserId());
            company.setCompanyName(request.getCompanyName());
            company.setIndustry(request.getIndustry());
            company.setScale(request.getScale());
            company.setContactPhone(request.getContactPhone());
            company.setVerifyStatus("pending");
            company.setCreditScore(60);
            companyMapper.insert(company);
        } else if ("teacher".equals(request.getRole())) {
            // 添加 roleType 校验
            if (request.getRoleType() == null ||
                    !Arrays.asList("counselor", "advisor", "admin", "leader").contains(request.getRoleType())) {
                throw new RuntimeException("教师角色类型只能是counselor、advisor、admin或leader");
            }
            Teacher teacher = new Teacher();
            teacher.setUserId(user.getUserId());
            teacher.setTeacherNo(request.getTeacherNo());
            teacher.setRealName(request.getRealName());
            teacher.setCollegeId(request.getCollegeId());
            teacher.setRoleType(request.getRoleType());
            teacher.setPhone(request.getPhone());
            teacher.setEmail(request.getEmail());
            teacher.setStatus("active");
            teacherMapper.insert(teacher);
        }
        
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
