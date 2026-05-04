package com.aiinterview.service.teacher;

import com.aiinterview.model.dto.teacher.*;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.model.entity.teacher.AssistanceRecord;
import com.aiinterview.model.entity.teacher.EmploymentLedger;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.model.entity.teacher.Teacher;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 教师端服务接口
 */
public interface TeacherService {

    TeacherProfileDTO getTeacherProfile(Long userId);

    TeacherProfileDTO saveOrUpdateTeacherProfile(Long userId, TeacherProfileDTO teacherProfileDTO);

    TeacherDashboardDTO getDashboard(Long userId);

    Page<StudentProfile> getStudents(StudentQueryDTO queryDTO, Long userId);

    EmploymentLedger auditEmployment(Long userId, EmploymentAuditDTO auditDTO);

    List<EmploymentLedger> getEmploymentList(Long userId);

    Page<Company> getCompanyList(String verifyStatus, String keyword, String industry, Integer current, Integer size);

    Page<Job> getJobList(String verifyStatus, String keyword, String jobType, Integer current, Integer size);

    Company auditCompany(Long userId, Long companyId, String verifyStatus, String remark);

    Job auditJob(Long userId, Long jobId, String verifyStatus, String remark);

    AssistanceRecord saveAssistanceRecord(Long userId, AssistanceRecordDTO recordDTO);

    List<AssistanceRecord> getAssistanceRecords(Long userId);

    Teacher getTeacherEntityByUserId(Long userId);

    // 活动管理
    com.aiinterview.model.entity.teacher.Activity createActivity(Long userId, ActivityDTO activityDTO);

    List<com.aiinterview.model.entity.teacher.Activity> getActivities(Long userId, String status);

    com.aiinterview.model.entity.teacher.Activity getActivityById(Long activityId);

    com.aiinterview.model.entity.teacher.Activity updateActivity(Long userId, Long activityId, ActivityDTO activityDTO);

    void deleteActivity(Long userId, Long activityId);

    List<ActivityRegistrationDTO> getActivityRegistrations(Long activityId);

    // 就业统计
    EmploymentStatsDTO getEmploymentStats(Long userId, Long collegeId, Long majorId);

    // 预警管理
    List<EarlyWarningDTO> getEarlyWarnings(Long userId, String warningLevel, String handleStatus);

    EarlyWarningDTO handleEarlyWarning(Long userId, Long warningId, String handleStatus, String handleRemark);

    EarlyWarningDTO createEarlyWarning(Long userId, EarlyWarningDTO warningDTO);

    EarlyWarningDTO getEarlyWarningDetail(Long warningId);

    List<EarlyWarningDTO> getStudentWarningHistory(Long studentId);

    int batchHandleEarlyWarnings(Long userId, List<Long> warningIds, String handleStatus, String handleRemark);

    java.util.Map<String, Object> getEarlyWarningStats(Long userId);

    // 校企合作
    List<com.aiinterview.model.entity.teacher.CooperationApplication> getCooperationApplications(Long userId);

    com.aiinterview.model.entity.teacher.CooperationApplication auditCooperationApplication(Long userId, Long applicationId, String status, String comment);
}
