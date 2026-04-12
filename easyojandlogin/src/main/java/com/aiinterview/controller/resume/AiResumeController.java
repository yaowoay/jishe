package com.aiinterview.controller.resume;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.model.dto.request.ResumeCreateRequest;
import com.aiinterview.service.ai.AiResumeService;
import com.aiinterview.service.resume.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public BaseResponse<ResumeLinksResponse> generateResume(
            HttpServletRequest httpRequest,
            @RequestBody AiResumeRequest aiRequest) {
        String userKey = getUserKeyFromSession(httpRequest);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        try {
            ResumeCreateRequest resumeRequest;
            if (aiRequest.getJobTitle() != null && !aiRequest.getJobTitle().isEmpty()) {
                resumeRequest = aiResumeService.generateResumeForJob(
                    aiRequest.getJobTitle(), 
                    aiRequest.getUserInfo()
                );
            } else {
                resumeRequest = aiResumeService.generateResumeFromUserInfo(aiRequest.getUserInfo());
            }
            // 封装 links 返回
            ResumeLinksResponse linksResponse = ResumeLinksResponse.fromResumeCreateRequest(resumeRequest);
            return BaseResponse.success(linksResponse, "AI生成简历成功");
        } catch (Exception e) {
            log.error("AI生成简历失败", e);
            return BaseResponse.error(500, "AI生成简历失败: " + e.getMessage());
        }
    }

    /**
     * 生成并保存简历
     */
    @PostMapping("/save")
    public BaseResponse<ResumeLinksResponse> generateAndSaveResume(
            HttpServletRequest httpRequest,
            @RequestBody AiResumeRequest aiRequest) {
        String userKey = getUserKeyFromSession(httpRequest);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        try {
            ResumeCreateRequest resumeRequest;
            if (aiRequest.getJobTitle() != null && !aiRequest.getJobTitle().isEmpty()) {
                resumeRequest = aiResumeService.generateResumeForJob(
                    aiRequest.getJobTitle(), 
                    aiRequest.getUserInfo()
                );
            } else {
                resumeRequest = aiResumeService.generateResumeFromUserInfo(aiRequest.getUserInfo());
            }
            // 保存简历
            resumeService.createResume(resumeRequest, userKey);
            // 封装 links 返回
            ResumeLinksResponse linksResponse = ResumeLinksResponse.fromResumeCreateRequest(resumeRequest);
            return BaseResponse.success(linksResponse, "简历生成并保存成功");
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

    /**
     * 封装简历链接返回对象
     */
    public static class ResumeLinksResponse {
        private List<LinkItem> links;
        public List<LinkItem> getLinks() { return links; }
        public void setLinks(List<LinkItem> links) { this.links = links; }
        public static ResumeLinksResponse fromResumeCreateRequest(ResumeCreateRequest req) {
            ResumeLinksResponse resp = new ResumeLinksResponse();
            // 这里假设 ResumeCreateRequest 有 getImgUrl/getWordUrl/getPdfUrl，或你可以自己组装
            LinkItem item = new LinkItem();
            item.setImg_url(req.getImgUrl());
            item.setWord_url(req.getWordUrl());
            item.setPdf_url(req.getPdfUrl());
            resp.setLinks(java.util.Collections.singletonList(item));
            return resp;
        }
        public static class LinkItem {
            private String img_url;
            private String word_url;
            private String pdf_url;
            public String getImg_url() { return img_url; }
            public void setImg_url(String img_url) { this.img_url = img_url; }
            public String getWord_url() { return word_url; }
            public void setWord_url(String word_url) { this.word_url = word_url; }
            public String getPdf_url() { return pdf_url; }
            public void setPdf_url(String pdf_url) { this.pdf_url = pdf_url; }
        }
    }
} 
