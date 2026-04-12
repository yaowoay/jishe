package com.aiinterview.controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 调试控制器 - 用于排查请求路由问题
 */
@RestController
@RequestMapping("/debug")
@CrossOrigin(origins = "*")
public class DebugController {

    /**
     * 捕获所有GET请求
     */
    @GetMapping("/**")
    public Map<String, Object> debugGet(HttpServletRequest request) {
        System.out.println("==========================================");
        System.out.println("=== DebugController 捕获GET请求 ===");
        System.out.println("请求URI: " + request.getRequestURI());
        System.out.println("请求URL: " + request.getRequestURL());
        System.out.println("==========================================");
        
        Map<String, Object> result = new HashMap<>();
        result.put("method", "GET");
        result.put("uri", request.getRequestURI());
        result.put("url", request.getRequestURL().toString());
        result.put("message", "DebugController 正常工作");
        
        return result;
    }

    /**
     * 捕获所有POST请求
     */
    @PostMapping("/**")
    public Map<String, Object> debugPost(HttpServletRequest request, @RequestBody(required = false) Object body) {
        System.out.println("==========================================");
        System.out.println("=== DebugController 捕获POST请求 ===");
        System.out.println("请求URI: " + request.getRequestURI());
        System.out.println("请求URL: " + request.getRequestURL());
        System.out.println("请求体: " + body);
        System.out.println("==========================================");
        
        Map<String, Object> result = new HashMap<>();
        result.put("method", "POST");
        result.put("uri", request.getRequestURI());
        result.put("url", request.getRequestURL().toString());
        result.put("body", body);
        result.put("message", "DebugController 正常工作");
        
        return result;
    }

    /**
     * 简单测试接口
     */
    @GetMapping("/test")
    public String test() {
        System.out.println("==========================================");
        System.out.println("=== DebugController test接口被调用 ===");
        System.out.println("==========================================");
        return "DebugController is working!";
    }
}
