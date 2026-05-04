package com.aiinterview.controller.resume;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.resume.ResumeGenerationRequest;
import com.aiinterview.model.dto.resume.ResumeGenerationResponse;
import com.aiinterview.service.impl.resume.ResumeGenerationServiceImpl;
import com.aiinterview.service.resume.ResumeGenerationService;
import com.aiinterview.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 简历生成控制器
 */
@Slf4j
@RestController
@RequestMapping("/resume-generation")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResumeGenerationController {

    private final ResumeGenerationService resumeGenerationService;
    private final JwtUtils jwtUtils;

    /**
     * 生成简历
     */
    @PostMapping("/generate")
    public ApiResponse<ResumeGenerationResponse> generateResume(
            @Valid @RequestBody ResumeGenerationRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);

            log.info("用户 {} 请求生成简历", userId);

            ResumeGenerationResponse response = resumeGenerationService.generateResume(request);

            if (response.getSuccess()) {
                log.info("用户 {} 简历生成成功", userId);
                return ApiResponse.success("简历生成成功", response);
            } else {
                log.error("用户 {} 简历生成失败: {}", userId, response.getMessage());
                return ApiResponse.error(response.getMessage());
            }
        } catch (Exception e) {
            log.error("生成简历失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 基于模板生成简历
     */
    @PostMapping("/generate-with-template")
    public ApiResponse<ResumeGenerationResponse> generateResumeWithTemplate(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);

            String template = request.get("template");
            String personalInfo = request.get("personalInfo");

            if (template == null || template.trim().isEmpty()) {
                return ApiResponse.error("模板内容不能为空");
            }

            if (personalInfo == null || personalInfo.trim().isEmpty()) {
                return ApiResponse.error("个人信息不能为空");
            }

            log.info("用户 {} 请求基于模板生成简历", userId);

            ResumeGenerationResponse response = resumeGenerationService.generateResumeWithTemplate(template, personalInfo);

            if (response.getSuccess()) {
                return ApiResponse.success("基于模板生成简历成功", response);
            } else {
                return ApiResponse.error(response.getMessage());
            }
        } catch (Exception e) {
            log.error("基于模板生成简历失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 验证API配置
     */
    @GetMapping("/validate-config")
    public ApiResponse<Map<String, Object>> validateConfig(HttpServletRequest request) {
        try {
            // 注释掉用户验证，允许未登录用户检查配置
            // Long userId = getUserIdFromToken(request);

            boolean isValid = resumeGenerationService.validateApiConfig();

            Map<String, Object> data = new HashMap<>();
            data.put("configValid", isValid);
            data.put("message", isValid ? "API配置正常" : "API配置不完整");

            return ApiResponse.success("配置检查完成", data);
        } catch (Exception e) {
            log.error("验证API配置失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 测试API连接
     */
    @GetMapping("/test-connection")
    public ApiResponse<String> testConnection() {
        try {
            String result = ((ResumeGenerationServiceImpl) resumeGenerationService).testApiConnection();

            if (result.contains("成功")) {
                return ApiResponse.success("API连接测试完成", result);
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            log.error("测试API连接失败: {}", e.getMessage());
            return ApiResponse.error("测试失败: " + e.getMessage());
        }
    }

    /**
     * 查看当前配置信息（调试用）
     */
    @GetMapping("/debug-config")
    public ApiResponse<Map<String, Object>> debugConfig() {
        try {
            Map<String, Object> configInfo = ((ResumeGenerationServiceImpl) resumeGenerationService).getConfigDebugInfo();
            return ApiResponse.success("配置信息获取成功", configInfo);
        } catch (Exception e) {
            log.error("获取配置信息失败: {}", e.getMessage());
            return ApiResponse.error("获取配置信息失败: " + e.getMessage());
        }
    }

    /**
     * 简化测试接口（无需登录）
     */
    @PostMapping("/test-generate")
    public ApiResponse<ResumeGenerationResponse> testGenerate() {
        try {
            log.info("收到简化测试请求");

            // 创建测试请求
            ResumeGenerationRequest testRequest = new ResumeGenerationRequest();
            testRequest.setPrompt("请生成一份简单的测试简历");
            testRequest.setPersonalInfo("姓名：测试用户\n手机：13800138000\n邮箱：test@example.com");
            testRequest.setTargetPosition("软件工程师");
            testRequest.setTemplateType("standard");
            testRequest.setStyle("professional");

            ResumeGenerationResponse response = resumeGenerationService.generateResume(testRequest);

            if (response.getSuccess()) {
                return ApiResponse.success("测试生成成功", response);
            } else {
                return ApiResponse.error(response.getMessage());
            }
        } catch (Exception e) {
            log.error("测试生成失败: {}", e.getMessage(), e);
            return ApiResponse.error("测试生成失败: " + e.getMessage());
        }
    }

    /**
     * 获取简历模板列表
     */
    @GetMapping("/templates")
    public ApiResponse<Map<String, String>> getTemplates() {
        try {
            // 简历模板列表
            Map<String, String> templates = new HashMap<>();
            templates.put("standard", "标准简历模板");
            templates.put("creative", "创意简历模板");
            templates.put("technical", "技术简历模板");
            templates.put("executive", "高管简历模板");
            templates.put("fresh_graduate", "应届生简历模板");

            return ApiResponse.success("获取模板列表成功", templates);
        } catch (Exception e) {
            log.error("获取简历模板列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取简历风格列表
     */
    @GetMapping("/styles")
    public ApiResponse<Map<String, String>> getStyles() {
        try {
            // 简历风格列表
            Map<String, String> styles = new HashMap<>();
            styles.put("professional", "专业风格");
            styles.put("modern", "现代风格");
            styles.put("classic", "经典风格");
            styles.put("creative", "创意风格");
            styles.put("minimal", "简约风格");

            return ApiResponse.success("获取风格列表成功", styles);
        } catch (Exception e) {
            log.error("获取简历风格列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

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
}
