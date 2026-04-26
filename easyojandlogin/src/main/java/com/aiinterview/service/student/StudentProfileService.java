package com.aiinterview.service.student;

import com.aiinterview.model.dto.student.StudentProfileRequest;
import com.aiinterview.model.dto.student.ExperienceRequest;
import com.aiinterview.model.entity.student.StudentProfile;

import java.util.Map;

/**
 * 学生档案服务接口
 */
public interface StudentProfileService {

    /**
     * 完善学生档案
     */
    StudentProfile completeProfile(Long userId, StudentProfileRequest request);

    /**
     * 根据用户ID获取学生档案
     */
    StudentProfile getByUserId(Long userId);

    /**
     * 检查学生档案是否已完善
     */
    boolean isProfileCompleted(Long userId);

    /**
     * 统计指定毕业年份的毕业生数量
     */
    Long countGraduatesByYear(Integer graduationYear);

    /**
     * 更新学生经历信息
     */
    StudentProfile updateExperience(Long userId, ExperienceRequest request);

    /**
     * 获取学生经历信息
     */
    Map<String, Object> getExperience(Long userId);

    /**
     * 更新简历完善状态
     */
    void updateResumeCompletionStatus(Long userId, Integer status);
}