package com.aiinterview.service.student;

import com.aiinterview.model.dto.student.StudentActivityDTO;

import java.util.List;

/**
 * 学生端活动服务
 */
public interface StudentActivityService {

    /**
     * 获取可参与活动列表
     */
    List<StudentActivityDTO> listActivities(Long userId, String status, String keyword);

    /**
     * 获取我的活动
     */
    List<StudentActivityDTO> listMyActivities(Long userId);

    /**
     * 报名活动
     */
    StudentActivityDTO registerActivity(Long userId, Long activityId);

    /**
     * 活动签到
     */
    StudentActivityDTO signInActivity(Long userId, Long activityId, String signInMethod);
}
