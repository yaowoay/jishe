package com.aiinterview.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 调试拦截器 - 用于调试请求路由问题
 */
@Component
public class DebugInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 拦截所有请求，看看到底有什么请求进来
        System.out.println("==========================================");
        System.out.println("=== 拦截器捕获所有请求 ===");
        System.out.println("请求URI: " + requestURI);
        System.out.println("请求方法: " + method);
        System.out.println("处理器: " + handler);
        System.out.println("==========================================");

        return true;
    }
}
