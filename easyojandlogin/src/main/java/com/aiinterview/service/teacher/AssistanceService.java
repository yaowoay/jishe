package com.aiinterview.service.teacher;

import com.aiinterview.model.dto.teacher.AssistanceRecordDTO;
import com.aiinterview.model.dto.teacher.MockInterviewAppointmentDTO;
import com.aiinterview.model.dto.teacher.ResumeGuidanceAppointmentDTO;
import com.aiinterview.model.entity.teacher.AssistanceRecord;
import com.aiinterview.model.entity.teacher.AssistanceTracking;
import com.aiinterview.model.entity.teacher.MockInterviewAppointment;
import com.aiinterview.model.entity.teacher.ResumeGuidanceAppointment;

import java.util.List;
import java.util.Map;

/**
 * 精准帮扶服务接口
 */
public interface AssistanceService {

    // ==================== 帮扶任务管理 ====================
    
    /**
     * 创建帮扶记录
     */
    AssistanceRecord createAssistanceRecord(Long teacherId, AssistanceRecordDTO dto);

    /**
     * 更新帮扶记录
     */
    AssistanceRecord updateAssistanceRecord(Long recordId, AssistanceRecordDTO dto);

    /**
     * 获取帮扶记录列表
     */
    List<AssistanceRecord> getAssistanceRecords(Long teacherId, String status);

    /**
     * 获取帮扶记录详情
     */
    AssistanceRecord getAssistanceRecordById(Long recordId);

    /**
     * 删除帮扶记录
     */
    boolean deleteAssistanceRecord(Long recordId);

    /**
     * 添加帮扶跟踪记录（支持干预前后评分与效果评估）
     */
    AssistanceTracking addTracking(Long recordId, String content, String progressStatus, String nextAction,
                                   Integer beforeScore, Integer afterScore, Long createdBy);

    /**
     * 获取帮扶跟踪记录
     */
    List<AssistanceTracking> getTrackingList(Long recordId);

    // ==================== 简历指导预约 ====================

    /**
     * 创建简历指导预约
     */
    ResumeGuidanceAppointment createResumeGuidanceAppointment(Long teacherId, ResumeGuidanceAppointmentDTO dto);

    /**
     * 更新简历指导预约
     */
    ResumeGuidanceAppointment updateResumeGuidanceAppointment(Long appointmentId, ResumeGuidanceAppointmentDTO dto);

    /**
     * 获取简历指导预约列表
     */
    List<ResumeGuidanceAppointment> getResumeGuidanceAppointments(Long teacherId, String status);

    /**
     * 获取简历指导预约详情
     */
    ResumeGuidanceAppointment getResumeGuidanceAppointmentById(Long appointmentId);

    /**
     * 取消简历指导预约
     */
    boolean cancelResumeGuidanceAppointment(Long appointmentId);

    /**
     * 完成简历指导并提交反馈
     */
    ResumeGuidanceAppointment completeResumeGuidance(Long appointmentId, String feedback);

    // ==================== 模拟面试安排 ====================

    /**
     * 创建模拟面试预约
     */
    MockInterviewAppointment createMockInterviewAppointment(Long teacherId, MockInterviewAppointmentDTO dto);

    /**
     * 更新模拟面试预约
     */
    MockInterviewAppointment updateMockInterviewAppointment(Long appointmentId, MockInterviewAppointmentDTO dto);

    /**
     * 获取模拟面试预约列表
     */
    List<MockInterviewAppointment> getMockInterviewAppointments(Long teacherId, String status);

    /**
     * 获取模拟面试预约详情
     */
    MockInterviewAppointment getMockInterviewAppointmentById(Long appointmentId);

    /**
     * 取消模拟面试预约
     */
    boolean cancelMockInterviewAppointment(Long appointmentId);

    /**
     * 完成模拟面试并提交反馈
     */
    MockInterviewAppointment completeMockInterview(Long appointmentId, String feedback, Integer score, 
                                                   String strengths, String weaknesses, String suggestions);

    // ==================== 帮扶效果统计 ====================

    /**
     * 获取帮扶效果统计
     */
    Map<String, Object> getAssistanceStatistics(Long teacherId);

    /**
     * 获取简历指导统计
     */
    Map<String, Object> getResumeGuidanceStatistics(Long teacherId);

    /**
     * 获取模拟面试统计
     */
    Map<String, Object> getMockInterviewStatistics(Long teacherId);
}
