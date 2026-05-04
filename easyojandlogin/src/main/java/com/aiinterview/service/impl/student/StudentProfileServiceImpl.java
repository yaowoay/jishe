package com.aiinterview.service.impl.student;

import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.mapper.UserMapper;
import com.aiinterview.model.dto.student.StudentProfileRequest;
import com.aiinterview.model.dto.student.ExperienceRequest;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.model.entity.user.User;
import com.aiinterview.service.student.StudentProfileService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 学生档案服务实现类
 */
@Service
@RequiredArgsConstructor
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileMapper studentProfileMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

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
            profile.setUserId((long) userId.intValue());
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

    @Override
    public Long countGraduatesByYear(Integer graduationYear) {
        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("graduation_year", graduationYear);
        return studentProfileMapper.selectCount(queryWrapper);
    }

    @Override
    @Transactional
    public StudentProfile updateExperience(Long userId, ExperienceRequest request) {
        try {
            // 查找学生档案
            QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            StudentProfile profile = studentProfileMapper.selectOne(queryWrapper);

            if (profile == null) {
                throw new RuntimeException("学生档案不存在，请先完善基本信息");
            }

            // 将经历信息转换为JSON字符串
            if (request.getResearchExperiences() != null) {
                String researchJson = objectMapper.writeValueAsString(request.getResearchExperiences());
                profile.setResearchExperience(researchJson);
            }

            if (request.getHonorsAwards() != null) {
                String honorsJson = objectMapper.writeValueAsString(request.getHonorsAwards());
                profile.setHonorsAwards(honorsJson);
            }

            if (request.getInternshipExperiences() != null) {
                String internshipJson = objectMapper.writeValueAsString(request.getInternshipExperiences());
                profile.setInternshipExperience(internshipJson);
            }

            // 更新数据库
            studentProfileMapper.updateById(profile);

            return profile;
        } catch (Exception e) {
            throw new RuntimeException("更新经历信息失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getExperience(Long userId) {
        try {
            QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            StudentProfile profile = studentProfileMapper.selectOne(queryWrapper);

            Map<String, Object> result = new HashMap<>();

            if (profile != null) {
                // 解析科研经历
                if (profile.getResearchExperience() != null && !profile.getResearchExperience().isEmpty()) {
                    result.put("researchExperiences", 
                        objectMapper.readValue(profile.getResearchExperience(), Object.class));
                }

                // 解析荣誉获奖
                if (profile.getHonorsAwards() != null && !profile.getHonorsAwards().isEmpty()) {
                    result.put("honorsAwards", 
                        objectMapper.readValue(profile.getHonorsAwards(), Object.class));
                }

                // 解析实习经历
                if (profile.getInternshipExperience() != null && !profile.getInternshipExperience().isEmpty()) {
                    result.put("internshipExperiences", 
                        objectMapper.readValue(profile.getInternshipExperience(), Object.class));
                }

                result.put("resumeCompletionStatus", profile.getResumeCompletionStatus());
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("获取经历信息失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateResumeCompletionStatus(Long userId, Integer status) {
        QueryWrapper<StudentProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        StudentProfile profile = studentProfileMapper.selectOne(queryWrapper);

        if (profile == null) {
            throw new RuntimeException("学生档案不存在");
        }

        profile.setResumeCompletionStatus(status);
        studentProfileMapper.updateById(profile);
    }
}