package com.aiinterview.service.student;

import com.aiinterview.model.dto.StudentProfileRequest;
import com.aiinterview.model.entity.teacher.StudentProfile;

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
}