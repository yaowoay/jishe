package com.aiinterview.service.impl.teacher;

import com.aiinterview.mapper.AssistanceRecordMapper;
import com.aiinterview.mapper.AssistanceTrackingMapper;
import com.aiinterview.mapper.MockInterviewAppointmentMapper;
import com.aiinterview.mapper.ResumeGuidanceAppointmentMapper;
import com.aiinterview.model.dto.teacher.AssistanceRecordDTO;
import com.aiinterview.model.dto.teacher.MockInterviewAppointmentDTO;
import com.aiinterview.model.dto.teacher.ResumeGuidanceAppointmentDTO;
import com.aiinterview.model.entity.teacher.AssistanceRecord;
import com.aiinterview.model.entity.teacher.AssistanceTracking;
import com.aiinterview.model.entity.teacher.MockInterviewAppointment;
import com.aiinterview.model.entity.teacher.ResumeGuidanceAppointment;
import com.aiinterview.service.teacher.AssistanceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 精准帮扶服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssistanceServiceImpl implements AssistanceService {

    private final AssistanceRecordMapper assistanceRecordMapper;
    private final AssistanceTrackingMapper assistanceTrackingMapper;
    private final ResumeGuidanceAppointmentMapper resumeGuidanceMapper;
    private final MockInterviewAppointmentMapper mockInterviewMapper;

    @Override
    @Transactional
    public AssistanceRecord createAssistanceRecord(Long teacherId, AssistanceRecordDTO dto) {
        AssistanceRecord record = new AssistanceRecord();
        BeanUtils.copyProperties(dto, record);
        record.setTeacherId(teacherId);
        record.setStartDate(LocalDate.now());
        record.setFollowUpDate(LocalDate.now().plusDays(7));
        if (record.getStatus() == null) {
            record.setStatus("pending");
        }
        assistanceRecordMapper.insert(record);
        return record;
    }

    @Override
    @Transactional
    public AssistanceRecord updateAssistanceRecord(Long recordId, AssistanceRecordDTO dto) {
        AssistanceRecord record = assistanceRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("帮扶记录不存在");
        }
        BeanUtils.copyProperties(dto, record, "recordId", "teacherId", "createdAt");
        assistanceRecordMapper.updateById(record);
        return record;
    }

    @Override
    public List<AssistanceRecord> getAssistanceRecords(Long teacherId, String status) {
        QueryWrapper<AssistanceRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("created_at");
        return assistanceRecordMapper.selectList(wrapper);
    }

    @Override
    public AssistanceRecord getAssistanceRecordById(Long recordId) {
        return assistanceRecordMapper.selectById(recordId);
    }

    @Override
    @Transactional
    public boolean deleteAssistanceRecord(Long recordId) {
        return assistanceRecordMapper.deleteById(recordId) > 0;
    }

    @Override
    @Transactional
    public AssistanceTracking addTracking(Long recordId, String content, String progressStatus,
                                          String nextAction, Integer beforeScore, Integer afterScore, Long createdBy) {
        AssistanceTracking tracking = new AssistanceTracking();
        tracking.setRecordId(recordId);
        tracking.setTrackingDate(LocalDate.now());
        tracking.setTrackingContent(content);
        tracking.setProgressStatus(progressStatus);
        tracking.setNextAction(nextAction);
        tracking.setBeforeScore(beforeScore);
        tracking.setAfterScore(afterScore);
        tracking.setImprovementRate(calculateImprovementRate(beforeScore, afterScore));
        tracking.setEffectiveness(evaluateEffectiveness(tracking.getImprovementRate()));
        tracking.setCreatedBy(createdBy);
        assistanceTrackingMapper.insert(tracking);
        return tracking;
    }

    @Override
    public List<AssistanceTracking> getTrackingList(Long recordId) {
        QueryWrapper<AssistanceTracking> wrapper = new QueryWrapper<>();
        wrapper.eq("record_id", recordId).orderByDesc("tracking_date");
        return assistanceTrackingMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public ResumeGuidanceAppointment createResumeGuidanceAppointment(Long teacherId, ResumeGuidanceAppointmentDTO dto) {
        ResumeGuidanceAppointment appointment = new ResumeGuidanceAppointment();
        BeanUtils.copyProperties(dto, appointment);
        appointment.setTeacherId(teacherId);
        if (appointment.getStatus() == null) {
            appointment.setStatus("pending");
        }
        if (appointment.getDuration() == null) {
            appointment.setDuration(60);
        }
        resumeGuidanceMapper.insert(appointment);
        return appointment;
    }

    @Override
    @Transactional
    public ResumeGuidanceAppointment updateResumeGuidanceAppointment(Long appointmentId, ResumeGuidanceAppointmentDTO dto) {
        ResumeGuidanceAppointment appointment = resumeGuidanceMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        BeanUtils.copyProperties(dto, appointment, "appointmentId", "teacherId", "createdAt");
        resumeGuidanceMapper.updateById(appointment);
        return appointment;
    }

    @Override
    public List<ResumeGuidanceAppointment> getResumeGuidanceAppointments(Long teacherId, String status) {
        QueryWrapper<ResumeGuidanceAppointment> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("appointment_time");
        return resumeGuidanceMapper.selectList(wrapper);
    }

    @Override
    public ResumeGuidanceAppointment getResumeGuidanceAppointmentById(Long appointmentId) {
        return resumeGuidanceMapper.selectById(appointmentId);
    }

    @Override
    @Transactional
    public boolean cancelResumeGuidanceAppointment(Long appointmentId) {
        ResumeGuidanceAppointment appointment = resumeGuidanceMapper.selectById(appointmentId);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus("cancelled");
        return resumeGuidanceMapper.updateById(appointment) > 0;
    }

    @Override
    @Transactional
    public ResumeGuidanceAppointment completeResumeGuidance(Long appointmentId, String feedback) {
        ResumeGuidanceAppointment appointment = resumeGuidanceMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        appointment.setStatus("completed");
        appointment.setTeacherFeedback(feedback);
        resumeGuidanceMapper.updateById(appointment);
        return appointment;
    }

    @Override
    @Transactional
    public MockInterviewAppointment createMockInterviewAppointment(Long teacherId, MockInterviewAppointmentDTO dto) {
        MockInterviewAppointment appointment = new MockInterviewAppointment();
        BeanUtils.copyProperties(dto, appointment);
        appointment.setTeacherId(teacherId);
        if (appointment.getStatus() == null) {
            appointment.setStatus("pending");
        }
        if (appointment.getDuration() == null) {
            appointment.setDuration(60);
        }
        if (appointment.getInterviewMode() == null) {
            appointment.setInterviewMode("offline");
        }
        mockInterviewMapper.insert(appointment);
        return appointment;
    }

    @Override
    @Transactional
    public MockInterviewAppointment updateMockInterviewAppointment(Long appointmentId, MockInterviewAppointmentDTO dto) {
        MockInterviewAppointment appointment = mockInterviewMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        BeanUtils.copyProperties(dto, appointment, "appointmentId", "teacherId", "createdAt");
        mockInterviewMapper.updateById(appointment);
        return appointment;
    }

    @Override
    public List<MockInterviewAppointment> getMockInterviewAppointments(Long teacherId, String status) {
        QueryWrapper<MockInterviewAppointment> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("appointment_time");
        return mockInterviewMapper.selectList(wrapper);
    }

    @Override
    public MockInterviewAppointment getMockInterviewAppointmentById(Long appointmentId) {
        return mockInterviewMapper.selectById(appointmentId);
    }

    @Override
    @Transactional
    public boolean cancelMockInterviewAppointment(Long appointmentId) {
        MockInterviewAppointment appointment = mockInterviewMapper.selectById(appointmentId);
        if (appointment == null) {
            return false;
        }
        appointment.setStatus("cancelled");
        return mockInterviewMapper.updateById(appointment) > 0;
    }

    @Override
    @Transactional
    public MockInterviewAppointment completeMockInterview(Long appointmentId, String feedback, Integer score,
                                                         String strengths, String weaknesses, String suggestions) {
        MockInterviewAppointment appointment = mockInterviewMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        appointment.setStatus("completed");
        appointment.setInterviewFeedback(feedback);
        appointment.setPerformanceScore(score);
        appointment.setStrengths(strengths);
        appointment.setWeaknesses(weaknesses);
        appointment.setImprovementSuggestions(suggestions);
        mockInterviewMapper.updateById(appointment);
        return appointment;
    }

    @Override
    public Map<String, Object> getAssistanceStatistics(Long teacherId) {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<AssistanceRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId);

        long total = assistanceRecordMapper.selectCount(wrapper);
        long pending = assistanceRecordMapper.selectCount(wrapper.clone().eq("status", "pending"));
        long inProgress = assistanceRecordMapper.selectCount(wrapper.clone().eq("status", "in_progress"));
        long completed = assistanceRecordMapper.selectCount(wrapper.clone().eq("status", "completed"));

        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("inProgress", inProgress);
        stats.put("completed", completed);

        return stats;
    }

    @Override
    public Map<String, Object> getResumeGuidanceStatistics(Long teacherId) {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<ResumeGuidanceAppointment> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId);

        long total = resumeGuidanceMapper.selectCount(wrapper);
        long pending = resumeGuidanceMapper.selectCount(wrapper.clone().eq("status", "pending"));
        long confirmed = resumeGuidanceMapper.selectCount(wrapper.clone().eq("status", "confirmed"));
        long completed = resumeGuidanceMapper.selectCount(wrapper.clone().eq("status", "completed"));

        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("confirmed", confirmed);
        stats.put("completed", completed);

        return stats;
    }

    @Override
    public Map<String, Object> getMockInterviewStatistics(Long teacherId) {
        Map<String, Object> stats = new HashMap<>();

        QueryWrapper<MockInterviewAppointment> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId);

        long total = mockInterviewMapper.selectCount(wrapper);
        long pending = mockInterviewMapper.selectCount(wrapper.clone().eq("status", "pending"));
        long confirmed = mockInterviewMapper.selectCount(wrapper.clone().eq("status", "confirmed"));
        long completed = mockInterviewMapper.selectCount(wrapper.clone().eq("status", "completed"));

        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("confirmed", confirmed);
        stats.put("completed", completed);

        return stats;
    }

    private BigDecimal calculateImprovementRate(Integer beforeScore, Integer afterScore) {
        if (beforeScore == null || afterScore == null || beforeScore <= 0) {
            return null;
        }
        return BigDecimal.valueOf(afterScore - beforeScore)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(beforeScore), 2, RoundingMode.HALF_UP);
    }

    private String evaluateEffectiveness(BigDecimal improvementRate) {
        if (improvementRate == null) {
            return null;
        }
        if (improvementRate.compareTo(BigDecimal.valueOf(20)) >= 0) {
            return "excellent";
        }
        if (improvementRate.compareTo(BigDecimal.valueOf(10)) >= 0) {
            return "good";
        }
        if (improvementRate.compareTo(BigDecimal.ZERO) >= 0) {
            return "fair";
        }
        return "poor";
    }
}
