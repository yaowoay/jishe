package com.aiinterview.controller.student;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.student.StudentActivityDTO;
import com.aiinterview.service.student.StudentActivityService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 学生端活动控制器
 */
@Slf4j
@RestController
@RequestMapping("/student/activities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentActivityController {

    private final StudentActivityService studentActivityService;
    private final JwtUtils jwtUtils;

    @GetMapping("/list")
    public ApiResponse<List<StudentActivityDTO>> listActivities(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        try {
            Long userId = getStudentUserId(request);
            return ApiResponse.success("获取活动成功", studentActivityService.listActivities(userId, status, keyword));
        } catch (Exception e) {
            log.error("获取活动列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/my")
    public ApiResponse<List<StudentActivityDTO>> listMyActivities(HttpServletRequest request) {
        try {
            Long userId = getStudentUserId(request);
            return ApiResponse.success("获取我的活动成功", studentActivityService.listMyActivities(userId));
        } catch (Exception e) {
            log.error("获取我的活动失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{activityId}/register")
    public ApiResponse<StudentActivityDTO> registerActivity(@PathVariable Long activityId, HttpServletRequest request) {
        try {
            Long userId = getStudentUserId(request);
            return ApiResponse.success("报名成功", studentActivityService.registerActivity(userId, activityId));
        } catch (Exception e) {
            log.error("报名活动失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{activityId}/signin")
    public ApiResponse<StudentActivityDTO> signInActivity(
            @PathVariable Long activityId,
            @RequestParam(required = false) String signInMethod,
            HttpServletRequest request) {
        try {
            Long userId = getStudentUserId(request);
            return ApiResponse.success("签到成功", studentActivityService.signInActivity(userId, activityId, signInMethod));
        } catch (Exception e) {
            log.error("活动签到失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    private Long getStudentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未找到有效的认证信息");
        }
        token = token.substring(7);
        String role = jwtUtils.getRoleFromToken(token);
        if (!"student".equals(role) && !"applicant".equals(role)) {
            throw new RuntimeException("用户角色不正确，需要学生用户权限");
        }
        return jwtUtils.getUserIdFromToken(token);
    }
}
