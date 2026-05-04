package com.aiinterview.controller.student;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.teacher.EarlyWarningDTO;
import com.aiinterview.service.student.StudentWarningService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 学生端预警控制器
 */
@Slf4j
@RestController
@RequestMapping("/student/warnings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentWarningController {

    private final StudentWarningService warningService;
    private final JwtUtils jwtUtils;

    @GetMapping("/list")
    public ApiResponse<List<EarlyWarningDTO>> getMyWarnings(
            @RequestParam(required = false) String warningLevel,
            @RequestParam(required = false) String handleStatus,
            HttpServletRequest request) {
        try {
            Long studentId = getStudentUserId(request);
            return ApiResponse.success("获取成功", warningService.getMyWarnings(studentId, warningLevel, handleStatus));
        } catch (Exception e) {
            log.error("获取预警列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{warningId}")
    public ApiResponse<EarlyWarningDTO> getWarningDetail(@PathVariable Long warningId, HttpServletRequest request) {
        try {
            Long studentId = getStudentUserId(request);
            return ApiResponse.success("获取成功", warningService.getWarningDetail(warningId, studentId));
        } catch (Exception e) {
            log.error("获取预警详情失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{warningId}/view")
    public ApiResponse<EarlyWarningDTO> viewWarning(@PathVariable Long warningId, HttpServletRequest request) {
        try {
            Long studentId = getStudentUserId(request);
            return ApiResponse.success("查看成功", warningService.viewWarning(warningId, studentId));
        } catch (Exception e) {
            log.error("查看预警失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{warningId}/respond")
    public ApiResponse<EarlyWarningDTO> respondToWarning(
            @PathVariable Long warningId,
            @RequestParam String response,
            HttpServletRequest request) {
        try {
            Long studentId = getStudentUserId(request);
            return ApiResponse.success("回应成功", warningService.respondToWarning(warningId, studentId, response));
        } catch (Exception e) {
            log.error("回应预警失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getWarningStats(HttpServletRequest request) {
        try {
            Long studentId = getStudentUserId(request);
            return ApiResponse.success("获取成功", warningService.getWarningStats(studentId));
        } catch (Exception e) {
            log.error("获取预警统计失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/unviewed-count")
    public ApiResponse<Long> getUnviewedCount(HttpServletRequest request) {
        try {
            Long studentId = getStudentUserId(request);
            return ApiResponse.success("获取成功", warningService.getUnviewedWarningCount(studentId));
        } catch (Exception e) {
            log.error("获取未查看预警数量失败: {}", e.getMessage());
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
        if (!"student".equals(role)) {
            throw new RuntimeException("用户角色不正确，需要学生用户权限");
        }
        return jwtUtils.getUserIdFromToken(token);
    }
}
