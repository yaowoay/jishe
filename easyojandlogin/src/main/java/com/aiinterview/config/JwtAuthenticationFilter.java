package com.aiinterview.config;

import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtils jwtUtils;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("处理请求: {}", requestURI);

        String token = getTokenFromRequest(request);

        if (token != null) {
            try {
                // 验证token并获取用户信息
                String username = jwtUtils.getUsernameFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);
                Long userId = jwtUtils.getUserIdFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        );

                    // 设置用户详细信息
                    authentication.setDetails(userId);

                    // 设置到安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT认证成功: 用户={}, 角色={}, ID={}", username, role, userId);
                }
            } catch (Exception e) {
                log.warn("JWT认证失败: 路径={}, 错误={}", requestURI, e.getMessage());
                // 清除安全上下文
                SecurityContextHolder.clearContext();
            }
        } else {
            log.debug("未找到JWT token: {}", requestURI);
        }

        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取JWT token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // DISC测试路径优先检查
        if (path.contains("disc-test")) {
            System.out.println("=== JWT Filter Debug ===");
            System.out.println("Request URI: " + path);
            System.out.println("Skipping JWT filter for DISC test");
            System.out.println("========================");
            return true;  // 直接跳过DISC测试的所有请求
        }

        // 视频分析路径检查
        if (path.contains("video-analysis")) {
            System.out.println("=== JWT Filter Debug ===");
            System.out.println("Request URI: " + path);
            System.out.println("Skipping JWT filter for video analysis");
            System.out.println("========================");
            return true;  // 直接跳过视频分析的所有请求
        }

        // 视频上传路径检查
        if (path.contains("videos/upload") || path.contains("videos/info")) {
            System.out.println("=== JWT Filter Debug ===");
            System.out.println("Request URI: " + path);
            System.out.println("Skipping JWT filter for video upload");
            System.out.println("========================");
            return true;  // 直接跳过视频上传的所有请求
        }

        // 面试记录路径检查
        if (path.contains("ai-interviews")) {
            System.out.println("=== JWT Filter Debug ===");
            System.out.println("Request URI: " + path);
            System.out.println("Skipping JWT filter for ai-interviews");
            System.out.println("========================");
            return true;  // 直接跳过面试记录的所有请求
        }

        // 面试评估路径检查
        if (path.contains("interview-evaluation")) {
            System.out.println("=== JWT Filter Debug ===");
            System.out.println("Request URI: " + path);
            System.out.println("Skipping JWT filter for interview-evaluation");
            System.out.println("========================");
            return true;  // 直接跳过面试评估的所有请求
        }

        // 其他路径的检查
        boolean shouldSkip = path.startsWith("/api/auth/") ||
               path.startsWith("/api/public/") ||
               path.startsWith("/api/error") ||
               path.startsWith("/api/actuator/") ||
               path.equals("/api/jobs/active") ||
               path.equals("/api/jobs/search") ||
               path.startsWith("/api/jobs/type/")||
                path.startsWith("/api/external-resume")||
               path.startsWith("/api/job-description");

        return shouldSkip;
    }
}
