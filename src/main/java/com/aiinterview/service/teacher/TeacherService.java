package com.aiinterview.service.teacher;

import com.aiinterview.model.dto.teacher.AssistanceRecordDTO;
import com.aiinterview.model.dto.teacher.EmploymentAuditDTO;
import com.aiinterview.model.dto.teacher.StudentQueryDTO;
import com.aiinterview.model.dto.teacher.TeacherDashboardDTO;
import com.aiinterview.model.dto.teacher.TeacherProfileDTO;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.model.entity.teacher.AssistanceRecord;
import com.aiinterview.model.entity.teacher.EmploymentLedger;
import com.aiinterview.model.entity.teacher.StudentProfile;
import com.aiinterview.model.entity.teacher.Teacher;

import java.util.List;

/**
 * 教师端服务接口
 */
public interface TeacherService {

    TeacherProfileDTO getTeacherProfile(Long userId);

    TeacherProfileDTO saveOrUpdateTeacherProfile(Long userId, TeacherProfileDTO teacherProfileDTO);

    TeacherDashboardDTO getDashboard(Long userId);

    List<StudentProfile> getStudents(StudentQueryDTO queryDTO, Long userId);

    EmploymentLedger auditEmployment(Long userId, EmploymentAuditDTO auditDTO);

    List<EmploymentLedger> getEmploymentList(Long userId);

    List<Company> getCompanyList(String verifyStatus);

    List<Job> getJobList(String verifyStatus);

    Company auditCompany(Long userId, Long companyId, String verifyStatus, String remark);

    Job auditJob(Long userId, Long jobId, String verifyStatus, String remark);

    AssistanceRecord saveAssistanceRecord(Long userId, AssistanceRecordDTO recordDTO);

    List<AssistanceRecord> getAssistanceRecords(Long userId);

    Teacher getTeacherEntityByUserId(Long userId);

    // 活动管理
    com.aiinterview.model.entity.teacher.Activity createActivity(Long userId, com.aiinterview.model.entity.teacher.Activity activity);

    List<com.aiinterview.model.entity.teacher.Activity> getActivities(Long userId);

    com.aiinterview.model.entity.teacher.Activity getActivityById(Long activityId);

    // 数据大屏
    com.aiinterview.model.dto.teacher.TeacherDashboardDTO getEmploymentStats(Long userId);

    // 校企合作
    List<com.aiinterview.model.entity.teacher.CooperationApplication> getCooperationApplications(Long userId);

    com.aiinterview.model.entity.teacher.CooperationApplication auditCooperationApplication(Long userId, Long applicationId, String status, String comment);
}
