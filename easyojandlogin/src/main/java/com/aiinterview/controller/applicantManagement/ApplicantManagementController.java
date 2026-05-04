package com.aiinterview.controller.applicantManagement;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.applicant.ApplicantDetailDTO;
import com.aiinterview.model.entity.company.Company;
import com.aiinterview.mapper.CompanyMapper;
import com.aiinterview.service.applicatManagement.ApplicantManagementService;
import com.aiinterview.service.application.ApplicationService;
import com.aiinterview.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应聘者管理控制器 - 基于 route_application.py 重新设计
 */
@Slf4j
@RestController
@RequestMapping("/applicant-management")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicantManagementController {

    private final ApplicantManagementService applicantManagementService;
    private final ApplicationService applicationService;
    private final JwtUtils jwtUtils;
    private final CompanyMapper companyMapper;

    // ==================== 求职者端接口 ====================

    /**
     * 获取最近面试记录 (对应 get_application)
     */
    @GetMapping("/recent-interviews")
    public ApiResponse<List<Map<String, Object>>> getRecentInterviews(
            @RequestParam(defaultValue = "5") Integer limit,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<Map<String, Object>> interviews = applicantManagementService.getRecentInterviews(userId, limit);
            return ApiResponse.success("获取成功", interviews);
        } catch (Exception e) {
            log.error("获取最近面试记录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取全部面试记录 (对应 get_all_interviews)
     */
    @GetMapping("/all-interviews")
    public ApiResponse<List<Map<String, Object>>> getAllInterviews(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<Map<String, Object>> interviews = applicantManagementService.getAllInterviews(userId);
            return ApiResponse.success("获取成功", interviews);
        } catch (Exception e) {
            log.error("获取全部面试记录失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 进入面试房间 (对应 interview_room)
     */
    @GetMapping("/interview-room")
    public ApiResponse<Map<String, Object>> enterInterviewRoom(
            @RequestParam Long applicationId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            Map<String, Object> result = applicantManagementService.enterInterviewRoom(userId, applicationId);
            return ApiResponse.success("成功", result);
        } catch (Exception e) {
            log.error("进入面试房间失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    // ==================== 企业端接口 ====================
    
    /**
     * 分页获取应聘者列表
     */
    @GetMapping("/list")
    public ApiResponse<Page<ApplicantDetailDTO>> getApplicantsList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            Page<ApplicantDetailDTO> page = applicantManagementService.getApplicantsByCompany(
                companyId, current, size, status, keyword
            );
            return ApiResponse.success("获取成功", page);
        } catch (Exception e) {
            log.error("获取应聘者列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取职位详情和申请者信息 (对应 job_detail)
     */
    @GetMapping("/job/{jobId}/detail")
    public ApiResponse<Map<String, Object>> getJobDetailWithApplicants(
            @PathVariable Long jobId,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            Map<String, Object> result = applicantManagementService.getJobDetailWithApplicants(jobId, companyId);
            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            log.error("获取职位详情失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取候选人列表 (对应 get_candidates)
     */
    @GetMapping("/job/{jobId}/candidates")
    public ApiResponse<List<Map<String, Object>>> getCandidates(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "false") Boolean showAll,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            List<Map<String, Object>> candidates = applicantManagementService.getCandidates(jobId, showAll, companyId);
            return ApiResponse.success("获取成功", candidates);
        } catch (Exception e) {
            log.error("获取候选人列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取应聘者详细信息
     */
    @GetMapping("/detail/{applicationId}")
    public ApiResponse<ApplicantDetailDTO> getApplicantDetail(
            @PathVariable Long applicationId,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            ApplicantDetailDTO detail = applicantManagementService.getApplicantDetail(applicationId, companyId);
            return ApiResponse.success("获取成功", detail);
        } catch (Exception e) {
            log.error("获取应聘者详情失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    // ==================== 状态管理接口 ====================

    /**
     * 标记申请为已查看 (对应 mark_viewed)
     */
    @PostMapping("/application/{applicationId}/mark-viewed")
    public ApiResponse<Boolean> markApplicationAsViewed(
            @PathVariable Long applicationId,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            boolean result = applicantManagementService.markApplicationAsViewed(applicationId, companyId);
            return ApiResponse.success("标记成功", result);
        } catch (Exception e) {
            log.error("标记申请为已查看失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 录用申请 (对应 accept)
     */
    @PostMapping("/application/{applicationId}/accept")
    public ApiResponse<Boolean> acceptApplication(
            @PathVariable Long applicationId,
            @RequestParam(required = false) String feedback,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            boolean result = applicantManagementService.acceptApplication(applicationId, feedback, companyId);
            return ApiResponse.success("申请已录用", result);
        } catch (Exception e) {
            log.error("录用申请失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新申请状态
     */
    @PostMapping("/update-status")
    public ApiResponse<String> updateApplicationStatus(
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            Long applicationId = Long.valueOf(requestData.get("applicationId").toString());
            String status = (String) requestData.get("status");
            String feedback = (String) requestData.get("feedback");
            
            boolean success = applicantManagementService.updateApplicationStatus(
                applicationId, status, feedback, companyId
            );
            
            if (success) {
                return ApiResponse.success("状态更新成功", "操作完成");
            } else {
                return ApiResponse.error("状态更新失败");
            }
        } catch (Exception e) {
            log.error("更新申请状态失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 发送面试邀请
     */
    @PostMapping("/send-interview")
    public ApiResponse<String> sendInterviewInvitation(
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            Long applicationId = Long.valueOf(requestData.get("applicationId").toString());
            String interviewTime = (String) requestData.get("interviewTime");
            String notes = (String) requestData.get("notes");

            boolean success = applicantManagementService.sendInterviewInvitation(
                applicationId, interviewTime, notes, companyId
            );

            if (success) {
                return ApiResponse.success("面试邀请发送成功", "邀请已发送");
            } else {
                return ApiResponse.error("面试邀请发送失败");
            }
        } catch (Exception e) {
            log.error("发送面试邀请失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 设置面试时间
     */
    @PostMapping("/application/{applicationId}/set-interview-time")
    public ApiResponse<Boolean> setInterviewTime(
            @PathVariable Long applicationId,
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            String interviewTimeStr = (String) requestData.get("interviewTime");

            // 解析时间字符串为 LocalDateTime
            LocalDateTime interviewTime = LocalDateTime.parse(interviewTimeStr);

            boolean result = applicationService.setInterviewTime(applicationId, interviewTime, companyId);
            return ApiResponse.success("面试时间设置成功", result);
        } catch (Exception e) {
            log.error("设置面试时间失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 拒绝申请
     */
    @PostMapping("/reject")
    public ApiResponse<String> rejectApplication(
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            Long applicationId = Long.valueOf(requestData.get("applicationId").toString());
            String reason = (String) requestData.get("reason");
            
            boolean success = applicantManagementService.rejectApplication(
                applicationId, reason, companyId
            );
            
            if (success) {
                return ApiResponse.success("申请已拒绝", "操作完成");
            } else {
                return ApiResponse.error("拒绝申请失败");
            }
        } catch (Exception e) {
            log.error("拒绝申请失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 录用应聘者
     */
    @PostMapping("/{applicationId}/hire")
    public ApiResponse<String> hireApplicant(
            @PathVariable Long applicationId,
            @RequestBody(required = false) Map<String, Object> requestData,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            String feedback = requestData != null ? (String) requestData.get("feedback") : null;

            boolean success = applicantManagementService.hireApplicant(
                applicationId, feedback, companyId
            );

            if (success) {
                return ApiResponse.success("已录用该应聘者", "操作完成");
            } else {
                return ApiResponse.error("录用操作失败");
            }
        } catch (Exception e) {
            log.error("录用操作失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 拒绝录用
     */
    @PostMapping("/{applicationId}/reject-hire")
    public ApiResponse<String> rejectHire(
            @PathVariable Long applicationId,
            @RequestBody(required = false) Map<String, Object> requestData,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            String reason = requestData != null ? (String) requestData.get("reason") : null;

            boolean success = applicantManagementService.rejectHire(
                applicationId, reason, companyId
            );

            if (success) {
                return ApiResponse.success("已拒绝录用", "操作完成");
            } else {
                return ApiResponse.error("拒绝录用失败");
            }
        } catch (Exception e) {
            log.error("拒绝录用失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 批量操作申请
     */
    @PostMapping("/batch-update")
    public ApiResponse<String> batchUpdateApplications(
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            @SuppressWarnings("unchecked")
            List<Long> applicationIds = (List<Long>) requestData.get("applicationIds");
            String status = (String) requestData.get("status");
            String feedback = (String) requestData.get("feedback");
            
            boolean success = applicantManagementService.batchUpdateApplications(
                applicationIds, status, feedback, companyId
            );
            
            if (success) {
                return ApiResponse.success("批量操作成功", "操作完成");
            } else {
                return ApiResponse.error("批量操作失败");
            }
        } catch (Exception e) {
            log.error("批量操作失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取申请统计信息
     */
    @GetMapping("/statistics/applications")
    public ApiResponse<Map<String, Object>> getApplicationStatistics(HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            Map<String, Object> statistics = applicantManagementService.getApplicationStatistics(companyId);
            return ApiResponse.success("获取成功", statistics);
        } catch (Exception e) {
            log.error("获取申请统计失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取面试统计信息
     */
    @GetMapping("/statistics/interviews")
    public ApiResponse<Map<String, Object>> getInterviewStatistics(HttpServletRequest request) {
        try {
            Long companyId = getCompanyIdFromToken(request);
            Map<String, Object> statistics = applicantManagementService.getInterviewStatistics(companyId);
            return ApiResponse.success("获取成功", statistics);
        } catch (Exception e) {
            log.error("获取面试统计失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    // ==================== 辅助方法 ====================

    /**
     * 从Token中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("未找到有效的认证信息");
            }

            token = token.substring(7);
            return jwtUtils.getUserIdFromToken(token);
        } catch (Exception e) {
            log.error("获取用户ID失败: {}", e.getMessage());
            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 从Token中获取公司ID
     */
    private Long getCompanyIdFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("未找到有效的认证信息");
            }

            token = token.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            String role = jwtUtils.getRoleFromToken(token);

            if (!"company".equals(role)) {
                throw new RuntimeException("用户角色不正确，需要企业用户权限");
            }

            QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            Company company = companyMapper.selectOne(queryWrapper);

            if (company == null) {
                throw new RuntimeException("未找到对应的公司信息");
            }

            return company.getCompanyId();
        } catch (Exception e) {
            log.error("获取公司ID失败: {}", e.getMessage());
            throw new RuntimeException("获取公司信息失败: " + e.getMessage());
        }
    }
}
