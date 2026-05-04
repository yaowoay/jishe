package com.aiinterview.service.impl.student;

import com.aiinterview.mapper.ActivityMapper;
import com.aiinterview.mapper.ActivityRegistrationMapper;
import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.model.dto.student.StudentActivityDTO;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.model.entity.teacher.Activity;
import com.aiinterview.model.entity.teacher.ActivityRegistration;
import com.aiinterview.service.student.StudentActivityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentActivityServiceImpl implements StudentActivityService {

    private final ActivityMapper activityMapper;
    private final ActivityRegistrationMapper activityRegistrationMapper;
    private final StudentProfileMapper studentProfileMapper;

    @Override
    public List<StudentActivityDTO> listActivities(Long userId, String status, String keyword) {
        StudentProfile student = studentProfileMapper.selectByUserId(userId);
        Long studentId = student == null ? null : student.getStudentId();

        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq("status", status.trim());
        } else {
            wrapper.in("status", "published", "ongoing");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("title", keyword.trim());
        }
        wrapper.orderByDesc("start_time");

        List<Activity> activities = activityMapper.selectList(wrapper);
        return activities.stream().map(activity -> buildDTO(activity, studentId)).collect(Collectors.toList());
    }

    @Override
    public List<StudentActivityDTO> listMyActivities(Long userId) {
        StudentProfile student = getStudentProfile(userId);
        List<ActivityRegistration> registrations = activityRegistrationMapper.selectList(
                new QueryWrapper<ActivityRegistration>()
                        .eq("student_id", student.getStudentId())
                        .orderByDesc("register_time"));

        return registrations.stream().map(reg -> {
            Activity activity = activityMapper.selectById(reg.getActivityId());
            if (activity == null) {
                return null;
            }
            return buildDTO(activity, student.getStudentId());
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentActivityDTO registerActivity(Long userId, Long activityId) {
        StudentProfile student = getStudentProfile(userId);
        Activity activity = getActivityForJoin(activityId);

        ActivityRegistration existing = activityRegistrationMapper.selectOne(new QueryWrapper<ActivityRegistration>()
                .eq("activity_id", activityId)
                .eq("student_id", student.getStudentId()));
        if (existing != null) {
            return buildDTO(activity, student.getStudentId());
        }

        if (activity.getMaxParticipants() != null
                && activity.getCurrentParticipants() != null
                && activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动名额已满");
        }

        ActivityRegistration reg = new ActivityRegistration();
        reg.setActivityId(activityId);
        reg.setStudentId(student.getStudentId());
        reg.setRegisterTime(LocalDateTime.now());
        reg.setStatus("registered");
        activityRegistrationMapper.insert(reg);

        int current = activity.getCurrentParticipants() == null ? 0 : activity.getCurrentParticipants();
        activity.setCurrentParticipants(current + 1);
        activityMapper.updateById(activity);

        return buildDTO(activity, student.getStudentId());
    }

    @Override
    @Transactional
    public StudentActivityDTO signInActivity(Long userId, Long activityId, String signInMethod) {
        StudentProfile student = getStudentProfile(userId);
        Activity activity = getActivityForJoin(activityId);
        ActivityRegistration reg = activityRegistrationMapper.selectOne(new QueryWrapper<ActivityRegistration>()
                .eq("activity_id", activityId)
                .eq("student_id", student.getStudentId()));
        if (reg == null) {
            throw new RuntimeException("请先报名再签到");
        }
        reg.setSignInTime(LocalDateTime.now());
        reg.setSignInMethod((signInMethod == null || signInMethod.trim().isEmpty()) ? "qrcode" : signInMethod.trim());
        reg.setStatus("signed_in");
        activityRegistrationMapper.updateById(reg);
        return buildDTO(activity, student.getStudentId());
    }

    private StudentActivityDTO buildDTO(Activity activity, Long studentId) {
        StudentActivityDTO dto = new StudentActivityDTO();
        BeanUtils.copyProperties(activity, dto);

        if (studentId == null) {
            dto.setRegistered(false);
            return dto;
        }

        ActivityRegistration reg = activityRegistrationMapper.selectOne(new QueryWrapper<ActivityRegistration>()
                .eq("activity_id", activity.getActivityId())
                .eq("student_id", studentId));
        dto.setRegistered(reg != null);
        if (reg != null) {
            dto.setRegistrationId(reg.getRegistrationId());
            dto.setRegistrationStatus(reg.getStatus());
            dto.setRegisterTime(reg.getRegisterTime());
            dto.setSignInTime(reg.getSignInTime());
        }
        return dto;
    }

    private StudentProfile getStudentProfile(Long userId) {
        StudentProfile student = studentProfileMapper.selectByUserId(userId);
        if (student == null) {
            throw new RuntimeException("学生档案不存在，请先完善档案");
        }
        return student;
    }

    private Activity getActivityForJoin(Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        return activity;
    }
}
