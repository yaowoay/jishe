package com.aiinterview.service.impl.student;

import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.mapper.UserMapper;
import com.aiinterview.model.dto.StudentProfileRequest;
import com.aiinterview.model.entity.teacher.StudentProfile;
import com.aiinterview.model.entity.user.User;
import com.aiinterview.service.student.StudentProfileService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学生档案服务实现类
 */
@Service
@RequiredArgsConstructor
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileMapper studentProfileMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public StudentProfile completeProfile(Long userId, StudentProfileRequest request) {
        // 检查学号是否已存在
        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_no", request.getStudentNo());
        queryWrapper.ne("user_id", userId); // 排除当前用户
        StudentProfile existingProfile = studentProfileMapper.selectOne(queryWrapper);
        if (existingProfile != null) {
            throw new RuntimeException("学号已存在");
        }

        // 查找是否已有档案记录
        QueryWrapper<StudentProfile> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", userId);
        StudentProfile profile = studentProfileMapper.selectOne(userQueryWrapper);

        if (profile == null) {
            // 创建新档案
            profile = new StudentProfile();
            profile.setUserId(userId.intValue());
            profile.setStudentNo(request.getStudentNo());
            profile.setRealName(request.getRealName());
            profile.setGender(request.getGender());
            profile.setCollege(request.getCollege());
            profile.setMajor(request.getMajor());
            profile.setClassName(request.getClassName());
            profile.setEducationLevel(request.getEducationLevel());
            profile.setGraduationYear(request.getGraduationYear());
            profile.setProfileCompletion(100); // 设置为已完成

            studentProfileMapper.insert(profile);
        } else {
            // 更新现有档案
            profile.setStudentNo(request.getStudentNo());
            profile.setRealName(request.getRealName());
            profile.setGender(request.getGender());
            profile.setCollege(request.getCollege());
            profile.setMajor(request.getMajor());
            profile.setClassName(request.getClassName());
            profile.setEducationLevel(request.getEducationLevel());
            profile.setGraduationYear(request.getGraduationYear());
            profile.setProfileCompletion(100); // 设置为已完成

            studentProfileMapper.updateById(profile);
        }

        // 更新用户表的profile_completed字段
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId);
        updateWrapper.set("profile_completed", 1);
        userMapper.update(null, updateWrapper);

        return profile;
    }

    @Override
    public StudentProfile getByUserId(Long userId) {
        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return studentProfileMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean isProfileCompleted(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null && user.getProfileCompleted() != null && user.getProfileCompleted() == 1;
    }
}