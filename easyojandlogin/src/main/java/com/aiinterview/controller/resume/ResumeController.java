package com.aiinterview.controller.resume;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.model.dto.request.ResumeCreateRequest;
import com.aiinterview.model.dto.request.ResumeUpdateRequest;
import com.aiinterview.model.dto.response.ResumeResponse;
import com.aiinterview.service.resume.ResumeService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final JwtUtils jwtUtils;

    /* ================= 用户简历 ================= */

    @GetMapping("/my")
    public BaseResponse<List<ResumeResponse>> getMyResumes(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        return BaseResponse.success(resumeService.getUserResumes(userId), "获取我的简历列表成功");
    }

    @GetMapping("/list")
    public BaseResponse<List<ResumeResponse>> getResumeList(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        return BaseResponse.success(resumeService.getUserResumes(userId), "获取简历列表成功");
    }

    @GetMapping
    public BaseResponse<List<ResumeResponse>> getUserResumes(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        return BaseResponse.success(resumeService.getUserResumes(userId), "获取简历列表成功");
    }

    @GetMapping("/page")
    public BaseResponse<Page<ResumeResponse>> getUserResumesPage(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return BaseResponse.success(resumeService.getUserResumesPage(userId, pageable));
    }

    /* ================= CRUD ================= */

    @PostMapping
    public BaseResponse<ResumeResponse> createResume(
            HttpServletRequest request,
            @Valid @RequestBody ResumeCreateRequest req) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录333");
        }
        return BaseResponse.success(resumeService.createResume(req, userId), "简历创建成功");
    }

    @PutMapping("/{resumeId}")
    public BaseResponse<ResumeResponse> updateResume(
            HttpServletRequest request,
            @PathVariable Long resumeId,
            @Valid @RequestBody ResumeUpdateRequest req) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        return BaseResponse.success(resumeService.updateResume(resumeId, req, userId), "简历更新成功");
    }

    @DeleteMapping("/{resumeId}")
    public BaseResponse<Void> deleteResume(
            HttpServletRequest request,
            @PathVariable Long resumeId) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        resumeService.deleteResume(resumeId, userId);
        return BaseResponse.success(null, "简历删除成功");
    }

    @GetMapping("/{resumeId}")
    public BaseResponse<ResumeResponse> getResume(
            HttpServletRequest request,
            @PathVariable Long resumeId) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        return BaseResponse.success(resumeService.getResumeById(resumeId, userId), "获取简历成功");
    }

    /* ================= 文件上传 ================= */

    @PostMapping("/upload")
    public BaseResponse<Object> uploadResume(
            HttpServletRequest request,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }

        try {
            String filename = file.getOriginalFilename();
            Object resume = resumeService.uploadResume(file, userId, filename);
            return BaseResponse.success(resume, "简历上传成功");
        } catch (Exception e) {
            log.error("简历上传失败: {}", e.getMessage());
            return BaseResponse.error(500, "简历上传失败: " + e.getMessage());
        }
    }

    /* ================= 功能扩展 ================= */

    @PutMapping("/{resumeId}/default")
    public BaseResponse<Void> setDefaultResume(
            HttpServletRequest request,
            @PathVariable Long resumeId) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        resumeService.setDefaultResume(resumeId, userId);
        return BaseResponse.success(null, "设置默认简历成功");
    }

    @PostMapping("/{resumeId}/copy")
    public BaseResponse<ResumeResponse> copyResume(
            HttpServletRequest request,
            @PathVariable Long resumeId) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        return BaseResponse.success(resumeService.copyResume(resumeId, userId), "复制简历成功");
    }

    @PostMapping("/{resumeId}/share")
    public BaseResponse<String> generateShareCode(
            HttpServletRequest request,
            @PathVariable Long resumeId) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        return BaseResponse.success(resumeService.generateShareCode(resumeId, userId), "生成分享码成功");
    }

    @GetMapping("/share/{shareCode}")
    public BaseResponse<ResumeResponse> getResumeByShareCode(@PathVariable String shareCode) {
        return BaseResponse.success(resumeService.getResumeByShareCode(shareCode), "获取分享简历成功");
    }

    @GetMapping("/search")
    public BaseResponse<Page<ResumeResponse>> searchResumes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return BaseResponse.success(resumeService.searchResumes(keyword, pageable));
    }

    @GetMapping("/popular")
    public BaseResponse<List<ResumeResponse>> getPopularResumes(
            @RequestParam(defaultValue = "10") int limit) {
        return BaseResponse.success(resumeService.getPopularResumes(limit), "获取热门简历成功");
    }

    @GetMapping("/recent")
    public BaseResponse<List<ResumeResponse>> getRecentResumes(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") int limit) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        return BaseResponse.success(resumeService.getRecentResumes(userId, limit), "获取最近简历成功");
    }

    /* ================= 工具方法 ================= */

    /**
     * 从JWT Token或Session中获取用户ID
     */
    private Long getUserId(HttpServletRequest request) {
        // 优先从JWT Token中获取用户ID
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                Long userId = jwtUtils.getUserIdFromToken(token);
                log.debug("从JWT Token获取用户ID: {}", userId);
                return userId;
            } catch (Exception e) {
                log.warn("JWT Token解析失败: {}", e.getMessage());
            }
        }
        
        // 尝试从Spring Security上下文获取用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            
            // 首先尝试从details中获取用户ID（JWT认证过滤器设置的）
            Object details = authentication.getDetails();
            if (details instanceof Long) {
                Long userId = (Long) details;
                log.debug("从Security上下文details获取用户ID: {}", userId);
                return userId;
            }
            
            // 如果details不是Long类型，尝试将principal转换为用户ID
            try {
                String principal = authentication.getName();
                Long userId = Long.valueOf(principal);
                log.debug("从Security上下文principal获取用户ID: {}", userId);
                return userId;
            } catch (NumberFormatException e) {
                log.debug("Security上下文中的principal不是用户ID: {}", authentication.getName());
            }
        }

        // 最后尝试从Session获取（兼容旧版本）
        Object userKey = request.getSession().getAttribute("userKey");
        if (userKey != null) {
            try {
                Long userId = Long.valueOf(userKey.toString());
                log.debug("从Session获取用户ID: {}", userId);
                return userId;
            } catch (NumberFormatException e) {
                log.warn("Session中的userKey无法转换为Long: {}", userKey);
            }
        }

        log.warn("无法获取用户ID");
        return null;
    }
}