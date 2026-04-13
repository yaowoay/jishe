package com.aiinterview.service.impl.teacher;

import com.aiinterview.mapper.TeacherMapper;
import com.aiinterview.mapper.UserMapper;
import com.aiinterview.model.dto.TeacherProfileRequest;
import com.aiinterview.model.entity.teacher.Teacher;
import com.aiinterview.model.entity.user.User;
import com.aiinterview.service.teacher.TeacherProfileService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 教师档案服务实现类
 */
@Service
@RequiredArgsConstructor
public class TeacherProfileServiceImpl implements TeacherProfileService {
    
    private final TeacherMapper teacherMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public Teacher completeProfile(Long userId, TeacherProfileRequest request) {
        // 检查工号是否已存在
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_no", request.getTeacherNo());
        queryWrapper.ne("user_id", userId); // 排除当前用户
        Teacher existingTeacher = teacherMapper.selectOne(queryWrapper);
        if (existingTeacher != null) {
            throw new RuntimeException("工号已存在");
        }
        
        // 查找是否已有档案记录
        QueryWrapper<Teacher> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", userId);
        Teacher teacher = teacherMapper.selectOne(userQueryWrapper);
        
        if (teacher == null) {
            // 创建新档案
            teacher = new Teacher();
            teacher.setUserId(userId);
            teacher.setTeacherNo(request.getTeacherNo());
            teacher.setRealName(request.getRealName());
            teacher.setCollegeId(request.getCollegeId());
            teacher.setRoleType(request.getRoleType());
            teacher.setPhone(request.getPhone());
            teacher.setEmail(request.getEmail());
            teacher.setManagedColleges(request.getManagedColleges());
            teacher.setManagedMajors(request.getManagedMajors());
            teacher.setProfileCompletion(100); // 设置为已完成
            
            teacherMapper.insert(teacher);
        } else {
            // 更新现有档案
            teacher.setTeacherNo(request.getTeacherNo());
            teacher.setRealName(request.getRealName());
            teacher.setCollegeId(request.getCollegeId());
            teacher.setRoleType(request.getRoleType());
            teacher.setPhone(request.getPhone());
            teacher.setEmail(request.getEmail());
            teacher.setManagedColleges(request.getManagedColleges());
            teacher.setManagedMajors(request.getManagedMajors());
            
            teacherMapper.updateById(teacher);
        }
        
        // 更新用户表的profile_completed字段
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId);
        updateWrapper.set("profile_completed", 1);
        userMapper.update(null, updateWrapper);
        
        return teacher;
    }
    
    @Override
    @Transactional
    public Teacher updateProfile(Long userId, TeacherProfileRequest request) {
        // 查找现有档案记录
        QueryWrapper<Teacher> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", userId);
        Teacher teacher = teacherMapper.selectOne(userQueryWrapper);
        
        if (teacher == null) {
            throw new RuntimeException("档案不存在，请先完善档案");
        }
        
        // 检查工号是否已被其他用户使用
        if (!request.getTeacherNo().equals(teacher.getTeacherNo())) {
            QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teacher_no", request.getTeacherNo());
            queryWrapper.ne("user_id", userId);
            Teacher existingTeacher = teacherMapper.selectOne(queryWrapper);
            if (existingTeacher != null) {
                throw new RuntimeException("工号已存在");
            }
        }
        
        // 更新档案信息
        teacher.setTeacherNo(request.getTeacherNo());
        teacher.setRealName(request.getRealName());
        teacher.setCollegeId(request.getCollegeId());
        teacher.setRoleType(request.getRoleType());
        teacher.setPhone(request.getPhone());
        teacher.setEmail(request.getEmail());
        teacher.setManagedColleges(request.getManagedColleges());
        teacher.setManagedMajors(request.getManagedMajors());
        teacher.setProfileCompletion(100); // 设置为已完成
        
        teacherMapper.updateById(teacher);
        
        // 更新用户表的profile_completed字段
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId);
        updateWrapper.set("profile_completed", 1);
        userMapper.update(null, updateWrapper);
        
        return teacher;
    }
    
    @Override
    public Teacher getByUserId(Long userId) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return teacherMapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean isProfileCompleted(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null && user.getProfileCompleted() != null && user.getProfileCompleted() == 1;
    }
}