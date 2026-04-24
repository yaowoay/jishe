package com.aiinterview.controller.teacher;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.teacher.AssistanceRecordDTO;
import com.aiinterview.model.dto.teacher.EmploymentAuditDTO;
import com.aiinterview.model.dto.teacher.MockInterviewAppointmentDTO;
import com.aiinterview.model.dto.teacher.ResumeGuidanceAppointmentDTO;
import com.aiinterview.model.dto.teacher.StudentQueryDTO;
import com.aiinterview.model.dto.teacher.TeacherDashboardDTO;
import com.aiinterview.model.dto.teacher.TeacherProfileDTO;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.model.entity.teacher.AssistanceRecord;
import com.aiinterview.model.entity.teacher.AssistanceTracking;
import com.aiinterview.model.entity.teacher.EmploymentLedger;
import com.aiinterview.model.entity.teacher.MockInterviewAppointment;
import com.aiinterview.model.entity.teacher.ResumeGuidanceAppointment;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.service.teacher.AssistanceService;
import com.aiinterview.service.teacher.TeacherService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 教师院校端控制器
 */
@Slf4j
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
public class TeacherController {

    private final TeacherService teacherService;
    private final AssistanceService assistanceService;
    private final JwtUtils jwtUtils;

    @GetMapping("/profile")
    public ApiResponse<TeacherProfileDTO> getProfile(HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getTeacherProfile(userId));
        } catch (Exception e) {
            log.error("获取教师信息失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/profile")
    public ApiResponse<TeacherProfileDTO> saveProfile(@Valid @RequestBody TeacherProfileDTO dto, HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("保存成功", teacherService.saveOrUpdateTeacherProfile(userId, dto));
        } catch (Exception e) {
            log.error("保存教师信息失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/dashboard")
    public ApiResponse<TeacherDashboardDTO> dashboard(HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getDashboard(userId));
        } catch (Exception e) {
            log.error("获取工作台失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/students/query")
    public ApiResponse<List<StudentProfile>> getStudents(@RequestBody(required = false) StudentQueryDTO queryDTO,
                                                         HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            if (queryDTO == null) {
                queryDTO = new StudentQueryDTO();
            }
            return ApiResponse.success("获取成功", teacherService.getStudents(queryDTO, userId));
        } catch (Exception e) {
            log.error("获取学生列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/employment")
    public ApiResponse<List<EmploymentLedger>> getEmploymentList(HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getEmploymentList(userId));
        } catch (Exception e) {
            log.error("获取就业台账失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/employment/audit")
    public ApiResponse<EmploymentLedger> auditEmployment(@RequestBody EmploymentAuditDTO dto,
                                                         HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("审核成功", teacherService.auditEmployment(userId, dto));
        } catch (Exception e) {
            log.error("审核就业台账失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/companies")
    public ApiResponse<List<Company>> getCompanies(@RequestParam(required = false) String verifyStatus,
                                                   HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getCompanyList(verifyStatus));
        } catch (Exception e) {
            log.error("获取企业列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/companies/{companyId}/audit")
    public ApiResponse<Company> auditCompany(@PathVariable Long companyId,
                                             @RequestParam String verifyStatus,
                                             @RequestParam(required = false) String remark,
                                             HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("审核成功", teacherService.auditCompany(userId, companyId, verifyStatus, remark));
        } catch (Exception e) {
            log.error("审核企业失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/jobs")
    public ApiResponse<List<Job>> getJobs(@RequestParam(required = false) String verifyStatus,
                                          HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getJobList(verifyStatus));
        } catch (Exception e) {
            log.error("获取职位列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/jobs/{jobId}/audit")
    public ApiResponse<Job> auditJob(@PathVariable Long jobId,
                                     @RequestParam String verifyStatus,
                                     @RequestParam(required = false) String remark,
                                     HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("审核成功", teacherService.auditJob(userId, jobId, verifyStatus, remark));
        } catch (Exception e) {
            log.error("审核职位失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/assistance")
    public ApiResponse<List<AssistanceRecord>> getAssistanceRecords(HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getAssistanceRecords(userId));
        } catch (Exception e) {
            log.error("获取帮扶记录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/assistance")
    public ApiResponse<AssistanceRecord> saveAssistanceRecord(@Valid @RequestBody AssistanceRecordDTO dto,
                                                              HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("保存成功", teacherService.saveAssistanceRecord(userId, dto));
        } catch (Exception e) {
            log.error("保存帮扶记录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/activities")
    public ApiResponse<com.aiinterview.model.entity.teacher.Activity> createActivity(
            @Valid @RequestBody com.aiinterview.model.dto.teacher.ActivityDTO activityDTO,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("创建成功", teacherService.createActivity(userId, activityDTO));
        } catch (Exception e) {
            log.error("创建活动失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/activities")
    public ApiResponse<List<com.aiinterview.model.entity.teacher.Activity>> getActivities(
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getActivities(userId, status));
        } catch (Exception e) {
            log.error("获取活动列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/activities/{activityId}")
    public ApiResponse<com.aiinterview.model.entity.teacher.Activity> getActivityById(@PathVariable Long activityId,
                                                                                       HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getActivityById(activityId));
        } catch (Exception e) {
            log.error("获取活动详情失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/activities/{activityId}")
    public ApiResponse<com.aiinterview.model.entity.teacher.Activity> updateActivity(
            @PathVariable Long activityId,
            @Valid @RequestBody com.aiinterview.model.dto.teacher.ActivityDTO activityDTO,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("更新成功", teacherService.updateActivity(userId, activityId, activityDTO));
        } catch (Exception e) {
            log.error("更新活动失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/activities/{activityId}")
    public ApiResponse<Void> deleteActivity(@PathVariable Long activityId, HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            teacherService.deleteActivity(userId, activityId);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除活动失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/activities/{activityId}/registrations")
    public ApiResponse<List<com.aiinterview.model.dto.teacher.ActivityRegistrationDTO>> getActivityRegistrations(
            @PathVariable Long activityId,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getActivityRegistrations(activityId));
        } catch (Exception e) {
            log.error("获取活动报名列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/employment-stats")
    public ApiResponse<com.aiinterview.model.dto.teacher.EmploymentStatsDTO> getEmploymentStats(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Long majorId,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getEmploymentStats(userId, collegeId, majorId));
        } catch (Exception e) {
            log.error("获取就业统计失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/early-warnings")
    public ApiResponse<List<com.aiinterview.model.dto.teacher.EarlyWarningDTO>> getEarlyWarnings(
            @RequestParam(required = false) String warningLevel,
            @RequestParam(required = false) String handleStatus,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getEarlyWarnings(userId, warningLevel, handleStatus));
        } catch (Exception e) {
            log.error("获取预警列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/early-warnings/{warningId}/handle")
    public ApiResponse<com.aiinterview.model.dto.teacher.EarlyWarningDTO> handleEarlyWarning(
            @PathVariable Long warningId,
            @RequestParam String handleStatus,
            @RequestParam(required = false) String handleRemark,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("处理成功", teacherService.handleEarlyWarning(userId, warningId, handleStatus, handleRemark));
        } catch (Exception e) {
            log.error("处理预警失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/cooperation-applications")
    public ApiResponse<List<com.aiinterview.model.entity.teacher.CooperationApplication>> getCooperationApplications(
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getCooperationApplications(userId));
        } catch (Exception e) {
            log.error("获取校企合作申请失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/cooperation-applications/{applicationId}/audit")
    public ApiResponse<com.aiinterview.model.entity.teacher.CooperationApplication> auditCooperationApplication(
            @PathVariable Long applicationId,
            @RequestParam String status,
            @RequestParam(required = false) String comment,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("审核成功", teacherService.auditCooperationApplication(userId, applicationId, status, comment));
        } catch (Exception e) {
            log.error("审核校企合作申请失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    // ==================== 精准帮扶功能 ====================

    @GetMapping("/assistance/records")
    public ApiResponse<List<AssistanceRecord>> getAssistanceRecordList(
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", assistanceService.getAssistanceRecords(userId, status));
        } catch (Exception e) {
            log.error("获取帮扶记录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/assistance/records")
    public ApiResponse<AssistanceRecord> createAssistanceRecord(
            @Valid @RequestBody AssistanceRecordDTO dto,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("创建成功", assistanceService.createAssistanceRecord(userId, dto));
        } catch (Exception e) {
            log.error("创建帮扶记录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/assistance/records/{recordId}")
    public ApiResponse<AssistanceRecord> updateAssistanceRecord(
            @PathVariable Long recordId,
            @Valid @RequestBody AssistanceRecordDTO dto,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("更新成功", assistanceService.updateAssistanceRecord(recordId, dto));
        } catch (Exception e) {
            log.error("更新帮扶记录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/assistance/records/{recordId}")
    public ApiResponse<AssistanceRecord> getAssistanceRecordDetail(
            @PathVariable Long recordId,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("获取成功", assistanceService.getAssistanceRecordById(recordId));
        } catch (Exception e) {
            log.error("获取帮扶记录详情失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/assistance/records/{recordId}")
    public ApiResponse<Void> deleteAssistanceRecord(
            @PathVariable Long recordId,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            assistanceService.deleteAssistanceRecord(recordId);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除帮扶记录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/assistance/records/{recordId}/tracking")
    public ApiResponse<AssistanceTracking> addAssistanceTracking(
            @PathVariable Long recordId,
            @RequestParam String content,
            @RequestParam String progressStatus,
            @RequestParam(required = false) String nextAction,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("添加成功",
                assistanceService.addTracking(recordId, content, progressStatus, nextAction, userId));
        } catch (Exception e) {
            log.error("添加跟踪记录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/assistance/records/{recordId}/tracking")
    public ApiResponse<List<AssistanceTracking>> getAssistanceTrackingList(
            @PathVariable Long recordId,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("获取成功", assistanceService.getTrackingList(recordId));
        } catch (Exception e) {
            log.error("获取跟踪记录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/assistance/statistics")
    public ApiResponse<Map<String, Object>> getAssistanceStatistics(HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", assistanceService.getAssistanceStatistics(userId));
        } catch (Exception e) {
            log.error("获取帮扶统计失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    // ==================== 简历指导预约 ====================

    @GetMapping("/resume-guidance/appointments")
    public ApiResponse<List<ResumeGuidanceAppointment>> getResumeGuidanceAppointments(
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", assistanceService.getResumeGuidanceAppointments(userId, status));
        } catch (Exception e) {
            log.error("获取简历指导预约失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/resume-guidance/appointments")
    public ApiResponse<ResumeGuidanceAppointment> createResumeGuidanceAppointment(
            @Valid @RequestBody ResumeGuidanceAppointmentDTO dto,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("创建成功", assistanceService.createResumeGuidanceAppointment(userId, dto));
        } catch (Exception e) {
            log.error("创建简历指导预约失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/resume-guidance/appointments/{appointmentId}")
    public ApiResponse<ResumeGuidanceAppointment> updateResumeGuidanceAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody ResumeGuidanceAppointmentDTO dto,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("更新成功", assistanceService.updateResumeGuidanceAppointment(appointmentId, dto));
        } catch (Exception e) {
            log.error("更新简历指导预约失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/resume-guidance/appointments/{appointmentId}")
    public ApiResponse<ResumeGuidanceAppointment> getResumeGuidanceAppointmentDetail(
            @PathVariable Long appointmentId,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("获取成功", assistanceService.getResumeGuidanceAppointmentById(appointmentId));
        } catch (Exception e) {
            log.error("获取简历指导预约详情失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/resume-guidance/appointments/{appointmentId}/cancel")
    public ApiResponse<Void> cancelResumeGuidanceAppointment(
            @PathVariable Long appointmentId,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            assistanceService.cancelResumeGuidanceAppointment(appointmentId);
            return ApiResponse.success("取消成功", null);
        } catch (Exception e) {
            log.error("取消简历指导预约失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/resume-guidance/appointments/{appointmentId}/complete")
    public ApiResponse<ResumeGuidanceAppointment> completeResumeGuidance(
            @PathVariable Long appointmentId,
            @RequestParam String feedback,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("完成成功", assistanceService.completeResumeGuidance(appointmentId, feedback));
        } catch (Exception e) {
            log.error("完成简历指导失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/resume-guidance/statistics")
    public ApiResponse<Map<String, Object>> getResumeGuidanceStatistics(HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", assistanceService.getResumeGuidanceStatistics(userId));
        } catch (Exception e) {
            log.error("获取简历指导统计失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    // ==================== 模拟面试安排 ====================

    @GetMapping("/mock-interview/appointments")
    public ApiResponse<List<MockInterviewAppointment>> getMockInterviewAppointments(
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", assistanceService.getMockInterviewAppointments(userId, status));
        } catch (Exception e) {
            log.error("获取模拟面试预约失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/mock-interview/appointments")
    public ApiResponse<MockInterviewAppointment> createMockInterviewAppointment(
            @Valid @RequestBody MockInterviewAppointmentDTO dto,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("创建成功", assistanceService.createMockInterviewAppointment(userId, dto));
        } catch (Exception e) {
            log.error("创建模拟面试预约失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/mock-interview/appointments/{appointmentId}")
    public ApiResponse<MockInterviewAppointment> updateMockInterviewAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody MockInterviewAppointmentDTO dto,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("更新成功", assistanceService.updateMockInterviewAppointment(appointmentId, dto));
        } catch (Exception e) {
            log.error("更新模拟面试预约失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/mock-interview/appointments/{appointmentId}")
    public ApiResponse<MockInterviewAppointment> getMockInterviewAppointmentDetail(
            @PathVariable Long appointmentId,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("获取成功", assistanceService.getMockInterviewAppointmentById(appointmentId));
        } catch (Exception e) {
            log.error("获取模拟面试预约详情失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/mock-interview/appointments/{appointmentId}/cancel")
    public ApiResponse<Void> cancelMockInterviewAppointment(
            @PathVariable Long appointmentId,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            assistanceService.cancelMockInterviewAppointment(appointmentId);
            return ApiResponse.success("取消成功", null);
        } catch (Exception e) {
            log.error("取消模拟面试预约失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/mock-interview/appointments/{appointmentId}/complete")
    public ApiResponse<MockInterviewAppointment> completeMockInterview(
            @PathVariable Long appointmentId,
            @RequestParam String feedback,
            @RequestParam Integer score,
            @RequestParam(required = false) String strengths,
            @RequestParam(required = false) String weaknesses,
            @RequestParam(required = false) String suggestions,
            HttpServletRequest request) {
        try {
            getTeacherUserId(request);
            return ApiResponse.success("完成成功",
                assistanceService.completeMockInterview(appointmentId, feedback, score, strengths, weaknesses, suggestions));
        } catch (Exception e) {
            log.error("完成模拟面试失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/mock-interview/statistics")
    public ApiResponse<Map<String, Object>> getMockInterviewStatistics(HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", assistanceService.getMockInterviewStatistics(userId));
        } catch (Exception e) {
            log.error("获取模拟面试统计失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    private Long getTeacherUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未找到有效的认证信息");
        }
        token = token.substring(7);
        String role = jwtUtils.getRoleFromToken(token);
        if (!"teacher".equals(role)) {
            throw new RuntimeException("用户角色不正确，需要教师用户权限");
        }
        return jwtUtils.getUserIdFromToken(token);
    }
}
