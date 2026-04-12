package com.aiinterview.controller.teacher;

import com.aiinterview.model.dto.api.ApiResponse;
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
import com.aiinterview.service.teacher.TeacherService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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
            @RequestBody com.aiinterview.model.entity.teacher.Activity activity,
            HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("创建成功", teacherService.createActivity(userId, activity));
        } catch (Exception e) {
            log.error("创建活动失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/activities")
    public ApiResponse<List<com.aiinterview.model.entity.teacher.Activity>> getActivities(HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getActivities(userId));
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

    @GetMapping("/employment-stats")
    public ApiResponse<com.aiinterview.model.dto.teacher.TeacherDashboardDTO> getEmploymentStats(HttpServletRequest request) {
        try {
            Long userId = getTeacherUserId(request);
            return ApiResponse.success("获取成功", teacherService.getEmploymentStats(userId));
        } catch (Exception e) {
            log.error("获取就业统计失败: {}", e.getMessage());
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
