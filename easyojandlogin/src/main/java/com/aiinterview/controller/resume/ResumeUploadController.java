package com.aiinterview.controller.resume;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.model.entity.resume.Resume;
import com.aiinterview.service.resume.ResumeService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 简历上传控制器
 * 处理文件上传到 resumes 表
 */
@Slf4j
@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResumeUploadController {

    private final ResumeService resumeService;
    private final JwtUtils jwtUtils;

    @GetMapping("/my-list")
    public BaseResponse<List<Resume>> getMyUploadedResumeList(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }

        try {
            log.info("获取我的已上传简历列表: userId={}", userId);
            List<Resume> resumes = resumeService.getUploadedResumesByUserId(userId);
            log.info("获取我的已上传简历列表成功: userId={}, count={}", userId, resumes.size());
            return BaseResponse.success(resumes, "获取已上传简历列表成功");
        } catch (Exception e) {
            log.error("获取我的已上传简历列表失败: userId={}, error={}", userId, e.getMessage(), e);
            return BaseResponse.error(500, "获取已上传简历列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/analysis-list")
    public BaseResponse<List<Resume>> getAnalysisResumeList(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }

        try {
            log.info("获取简历分析列表: userId={}", userId);
            List<Resume> resumes = resumeService.getUploadedResumesByUserId(userId);
            log.info("获取简历分析列表成功: userId={}, count={}", userId, resumes.size());
            return BaseResponse.success(resumes, "获取简历分析列表成功");
        } catch (Exception e) {
            log.error("获取简历分析列表失败: userId={}, error={}", userId, e.getMessage(), e);
            return BaseResponse.error(500, "获取简历分析列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public BaseResponse<List<Resume>> getUploadedResumeList(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }

        try {
            log.info("获取已上传简历列表: userId={}", userId);
            List<Resume> resumes = resumeService.getUploadedResumesByUserId(userId);
            log.info("获取已上传简历列表成功: userId={}, count={}", userId, resumes.size());
            return BaseResponse.success(resumes, "获取已上传简历列表成功");
        } catch (Exception e) {
            log.error("获取已上传简历列表失败: userId={}, error={}", userId, e.getMessage(), e);
            return BaseResponse.error(500, "获取已上传简历列表失败: " + e.getMessage());
        }
    }
    //@CrossOrigin(origins = "*", allowCredentials = "true")
    @PostMapping("/upload")
    public BaseResponse<Object> uploadResume(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "filename", required = false) String customFilename) {

        Long userId = getUserId(request);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }

        try {
            if (file == null || file.isEmpty()) {
                return BaseResponse.error(400, "文件不能为空");
            }

            // 使用自定义文件名或原始文件名
            String filename = customFilename != null && !customFilename.isEmpty()
                ? customFilename
                : file.getOriginalFilename();

            log.info("开始上传简历: userId={}, filename={}", userId, filename);

            Object resume = resumeService.uploadResume(file, userId, filename);

            log.info("简历上传成功: userId={}, filename={}", userId, filename);
            return BaseResponse.success(resume, "简历上传成功");
        } catch (Exception e) {
            log.error("简历上传失败: userId={}, error={}", userId, e.getMessage(), e);
            return BaseResponse.error(500, "简历上传失败: " + e.getMessage());
        }
    }

    /**
     * 从JWT Token或Session中获取用户ID
     */
    private Long getUserId(HttpServletRequest request) {
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
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            
            Object details = authentication.getDetails();
            if (details instanceof Long) {
                Long userId = (Long) details;
                log.debug("从Security上下文details获取用户ID: {}", userId);
                return userId;
            }
            
            try {
                String principal = authentication.getName();
                Long userId = Long.valueOf(principal);
                log.debug("从Security上下文principal获取用户ID: {}", userId);
                return userId;
            } catch (NumberFormatException e) {
                log.debug("Security上下文中的principal不是用户ID: {}", authentication.getName());
            }
        }

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
