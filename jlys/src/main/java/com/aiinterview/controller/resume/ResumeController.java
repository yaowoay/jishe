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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 简历控制器
 */
@Slf4j
@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final JwtUtils jwtUtils;

    /**
     * 智能编辑简历专用：获取当前登录用户的简历列表
     */
    @GetMapping("/my")
    public BaseResponse<List<ResumeResponse>> getMyResumes(HttpServletRequest request) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        try {
            List<ResumeResponse> responses = resumeService.getUserResumes(userKey);
            return BaseResponse.success(responses, "获取我的简历列表成功");
        } catch (Exception e) {
            log.error("获取我的简历列表失败", e);
            return BaseResponse.error(500, "获取我的简历列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建简历
     */
    @PostMapping
    public BaseResponse<ResumeResponse> createResume(
            HttpServletRequest request,
            @Valid @RequestBody ResumeCreateRequest createRequest) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        
        try {
            ResumeResponse response = resumeService.createResume(createRequest, userKey);
            return BaseResponse.success(response, "简历创建成功");
        } catch (Exception e) {
            log.error("创建简历失败", e);
            return BaseResponse.error(500, "创建简历失败: " + e.getMessage());
        }
    }

    /**
     * 更新简历
     */
    @PutMapping("/{resumeId}")
    public BaseResponse<ResumeResponse> updateResume(
            HttpServletRequest request,
            @PathVariable Long resumeId,
            @Valid @RequestBody ResumeUpdateRequest updateRequest) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        
        try {
            ResumeResponse response = resumeService.updateResume(resumeId, updateRequest, userKey);
            return BaseResponse.success(response, "简历更新成功");
        } catch (Exception e) {
            log.error("更新简历失败", e);
            return BaseResponse.error(500, "更新简历失败: " + e.getMessage());
        }
    }

    /**
     * 删除简历
     */
    @DeleteMapping("/{resumeId}")
    public BaseResponse<Void> deleteResume(
            HttpServletRequest request,
            @PathVariable Long resumeId) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }

        try {
            resumeService.deleteResume(resumeId, userKey);
            return BaseResponse.success(null, "简历删除成功");
        } catch (Exception e) {
            log.error("删除简历失败", e);
            return BaseResponse.error(500, "删除简历失败: " + e.getMessage());
        }
    }

    /**
     * 获取简历详情
     */
    @GetMapping("/{resumeId}")
    public BaseResponse<ResumeResponse> getResume(
            HttpServletRequest request,
            @PathVariable Long resumeId) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        
        try {
            ResumeResponse response = resumeService.getResumeById(resumeId, userKey);
            return BaseResponse.success(response, "获取简历成功");
        } catch (Exception e) {
            log.error("获取简历失败", e);
            return BaseResponse.error(500, "获取简历失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的所有简历
     */
    @GetMapping
    public BaseResponse<List<ResumeResponse>> getUserResumes(HttpServletRequest request) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        
        try {
            List<ResumeResponse> responses = resumeService.getUserResumes(userKey);
            return BaseResponse.success(responses, "获取简历列表成功");
        } catch (Exception e) {
            log.error("获取简历列表失败", e);
            return BaseResponse.error(500, "获取简历列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取用户简历
     */
    @GetMapping("/page")
    public BaseResponse<Page<ResumeResponse>> getUserResumesPage(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updateTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ResumeResponse> responses = resumeService.getUserResumesPage(userKey, pageable);
            return BaseResponse.success(responses, "获取简历分页数据成功");
        } catch (Exception e) {
            log.error("获取简历分页数据失败", e);
            return BaseResponse.error(500, "获取简历分页数据失败: " + e.getMessage());
        }
    }

    /**
     * 设置默认简历
     */
    @PutMapping("/{resumeId}/default")
    public BaseResponse<Void> setDefaultResume(
            HttpServletRequest request,
            @PathVariable Long resumeId) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }

        try {
            resumeService.setDefaultResume(resumeId, userKey);
            return BaseResponse.success(null, "设置默认简历成功");
        } catch (Exception e) {
            log.error("设置默认简历失败", e);
            return BaseResponse.error(500, "设置默认简历失败: " + e.getMessage());
        }
    }

    /**
     * 复制简历
     */
    @PostMapping("/{resumeId}/copy")
    public BaseResponse<ResumeResponse> copyResume(
            HttpServletRequest request,
            @PathVariable Long resumeId) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        
        try {
            ResumeResponse response = resumeService.copyResume(resumeId, userKey);
            return BaseResponse.success(response, "复制简历成功");
        } catch (Exception e) {
            log.error("复制简历失败", e);
            return BaseResponse.error(500, "复制简历失败: " + e.getMessage());
        }
    }

    /**
     * 生成分享链接
     */
    @PostMapping("/{resumeId}/share")
    public BaseResponse<String> generateShareUrl(
            HttpServletRequest request,
            @PathVariable Long resumeId) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        
        try {
            String shareUrl = resumeService.generateShareUrl(resumeId, userKey);
            return BaseResponse.success(shareUrl, "生成分享链接成功");
        } catch (Exception e) {
            log.error("生成分享链接失败", e);
            return BaseResponse.error(500, "生成分享链接失败: " + e.getMessage());
        }
    }

    /**
     * 通过分享链接访问简历
     */
    @GetMapping("/share/{shareUrl}")
    public BaseResponse<ResumeResponse> getResumeByShareUrl(@PathVariable String shareUrl) {
        try {
            ResumeResponse response = resumeService.getResumeByShareUrl(shareUrl);
            return BaseResponse.success(response, "获取分享简历成功");
        } catch (Exception e) {
            log.error("获取分享简历失败", e);
            return BaseResponse.error(500, "获取分享简历失败: " + e.getMessage());
        }
    }

    /**
     * 搜索简历
     */
    @GetMapping("/search")
    public BaseResponse<Page<ResumeResponse>> searchResumes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ResumeResponse> responses = resumeService.searchResumes(keyword, pageable);
            return BaseResponse.success(responses, "搜索简历成功");
        } catch (Exception e) {
            log.error("搜索简历失败", e);
            return BaseResponse.error(500, "搜索简历失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门简历
     */
    @GetMapping("/popular")
    public BaseResponse<List<ResumeResponse>> getPopularResumes(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ResumeResponse> responses = resumeService.getPopularResumes(limit);
            return BaseResponse.success(responses, "获取热门简历成功");
        } catch (Exception e) {
            log.error("获取热门简历失败", e);
            return BaseResponse.error(500, "获取热门简历失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近更新的简历
     */
    @GetMapping("/recent")
    public BaseResponse<List<ResumeResponse>> getRecentResumes(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") int limit) {
        String userKey = getUserKeyFromSession(request);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        
        try {
            List<ResumeResponse> responses = resumeService.getRecentResumes(userKey, limit);
            return BaseResponse.success(responses, "获取最近简历成功");
        } catch (Exception e) {
            log.error("获取最近简历失败", e);
            return BaseResponse.error(500, "获取最近简历失败: " + e.getMessage());
        }
    }
    
    /**
     * 从JWT Token或Session中获取用户标识
     */
    private String getUserKeyFromSession(HttpServletRequest request) {
        // 优先从JWT Token中获取
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String username = jwtUtils.getUsernameFromToken(token);
                log.debug("从JWT Token获取用户: {}", username);
                return username;
            } catch (Exception e) {
                log.warn("JWT Token解析失败: {}", e.getMessage());
            }
        }

        // 尝试从Spring Security上下文获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            log.debug("从Security上下文获取用户: {}", username);
            return username;
        }

        // 最后尝试从Session获取（兼容旧版本）
        Object userKey = request.getSession().getAttribute("userKey");
        if (userKey != null) {
            log.debug("从Session获取用户: {}", userKey);
            return userKey.toString();
        }

        log.warn("无法获取用户信息");
        return null;
    }
}

/*ResumeController.java - 简历CRUD控制器
创建、更新、删除简历
获取简历详情和列表
设置默认简历、复制简历
生成分享链接、搜索简历*/