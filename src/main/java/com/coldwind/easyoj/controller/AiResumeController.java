package com.coldwind.easyoj.controller;

import com.coldwind.easyoj.common.BaseResponse;
import com.coldwind.easyoj.model.dto.request.ResumeCreateRequest;
import com.coldwind.easyoj.service.AiResumeService;
import com.coldwind.easyoj.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * AI简历生成控制器
 */
@Slf4j
@RestController
@RequestMapping("/make")
@RequiredArgsConstructor
public class AiResumeController {

    private final AiResumeService aiResumeService;
    private final ResumeService resumeService;

    /**
     * 生成简历
     */
    @PostMapping("/generate1")
    public BaseResponse<ResumeCreateRequest> generateResume(
            HttpServletRequest httpRequest,
            @RequestBody AiResumeRequest aiRequest) {
        String userKey = getUserKeyFromSession(httpRequest);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        
        try {
            ResumeCreateRequest resumeRequest;
            if (aiRequest.getJobTitle() != null && !aiRequest.getJobTitle().isEmpty()) {
                // 根据职位生成简历
                resumeRequest = aiResumeService.generateResumeForJob(
                    aiRequest.getJobTitle(), 
                    aiRequest.getUserInfo()
                );
            } else {
                // 根据用户信息生成简历
                resumeRequest = aiResumeService.generateResumeFromUserInfo(aiRequest.getUserInfo());
            }
            return BaseResponse.success(resumeRequest, "AI生成简历成功");
        } catch (Exception e) {
            log.error("AI生成简历失败", e);
            return BaseResponse.error(500, "AI生成简历失败: " + e.getMessage());
        }
    }

    /**
     * 生成并保存简历
     */
    @PostMapping("/save")
    public BaseResponse<String> generateAndSaveResume(
            HttpServletRequest httpRequest,
            @RequestBody AiResumeRequest aiRequest) {
        String userKey = getUserKeyFromSession(httpRequest);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        
        try {
            ResumeCreateRequest resumeRequest;
            if (aiRequest.getJobTitle() != null && !aiRequest.getJobTitle().isEmpty()) {
                // 根据职位生成简历
                resumeRequest = aiResumeService.generateResumeForJob(
                    aiRequest.getJobTitle(), 
                    aiRequest.getUserInfo()
                );
            } else {
                // 根据用户信息生成简历
                resumeRequest = aiResumeService.generateResumeFromUserInfo(aiRequest.getUserInfo());
            }
            
            // 保存简历
            resumeService.createResume(resumeRequest, userKey);
            
            return BaseResponse.success("简历生成并保存成功", "AI生成并保存简历成功");
        } catch (Exception e) {
            log.error("AI生成并保存简历失败", e);
            return BaseResponse.error(500, "AI生成并保存简历失败: " + e.getMessage());
        }
    }

    /**
     * 从session中获取用户标识
     */
    private String getUserKeyFromSession(HttpServletRequest request) {
        Object userKey = request.getSession().getAttribute("userKey");
        return userKey != null ? userKey.toString() : null;
    }

    /**
     * AI简历请求类
     */
    public static class AiResumeRequest {
        private String userInfo;
        private String jobTitle;

        public String getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(String userInfo) {
            this.userInfo = userInfo;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }
    }
} 
