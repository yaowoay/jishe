package com.aiinterview.service.teacher;

import com.aiinterview.model.dto.TeacherProfileRequest;
import com.aiinterview.model.entity.teacher.Teacher;

/**
 * 教师档案服务接口
 */
public interface TeacherProfileService {
    
    /**
     * 完善教师档案
     */
    Teacher completeProfile(Long userId, TeacherProfileRequest request);
    
    /**
     * 更新教师档案
     */
    Teacher updateProfile(Long userId, TeacherProfileRequest request);
    
    /**
     * 根据用户ID获取教师档案
     */
    Teacher getByUserId(Long userId);
    
    /**
     * 检查教师档案是否已完善
     */
    boolean isProfileCompleted(Long userId);
}