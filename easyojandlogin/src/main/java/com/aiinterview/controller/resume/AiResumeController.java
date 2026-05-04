package com.aiinterview.controller.resume;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.model.dto.resume.ResumeCreateRequest;
import com.aiinterview.service.ai.AiResumeService;
import com.aiinterview.service.resume.ResumeService;
import com.aiinterview.utils.JwtUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AI简历生成控制器
 */
@Slf4j
@RestController
@RequestMapping("/make")
@RequiredArgsConstructor
public class AiResumeController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;


    private final AiResumeService aiResumeService;
    private final ResumeService resumeService;
    @Autowired
    private JwtUtils jwtUtils;
    /**
     * 生成简历  目前传参为空POST "/api/make/generate1", parameters={}
     */
    @PostMapping("/generate1")
    public BaseResponse<ResumeGenerateResponse> generateResume(
            HttpServletRequest httpRequest,
            @RequestBody AiResumeRequest aiRequest) {

        // 改为从 JWT Token 获取用户ID
        Long userId = getUserIdFromToken(httpRequest);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }

        try {
            aiRequest.setUserInfo(buildUserInfoFromRequest(aiRequest));
            ResumeCreateRequest resumeRequest;
            if (aiRequest.getJobTitle() != null && !aiRequest.getJobTitle().isEmpty()) {
                resumeRequest = aiResumeService.generateResumeForJob(
                        aiRequest.getJobTitle(),
                        aiRequest.getUserInfo()
                );
            } else {
                resumeRequest = aiResumeService.generateResumeFromUserInfo(aiRequest.getUserInfo());
            }
            ResumeGenerateResponse response = ResumeGenerateResponse.fromResumeCreateRequest(resumeRequest);
            return BaseResponse.success(response, "AI生成简历成功");
        } catch (Exception e) {
            log.error("AI生成简历失败", e);
            return BaseResponse.error(500, "AI生成简历失败: " + e.getMessage());
        }
    }

    // 添加获取用户ID的方法
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                // 根据你的 JWT 工具类获取用户ID
                return jwtUtils.getUserIdFromToken(token);
            } catch (Exception e) {
                log.warn("解析token失败: {}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 生成并保存简历
     */
    @PostMapping("/save")
    public BaseResponse<ResumeGenerateResponse> generateAndSaveResume(
            HttpServletRequest httpRequest,
            @RequestBody AiResumeRequest aiRequest) {
        String userKey = getUserKeyFromSession(httpRequest);
        if (userKey == null) {
            return BaseResponse.error(401, "未登录");
        }
        try {
            aiRequest.setUserInfo(buildUserInfoFromRequest(aiRequest));
            ResumeCreateRequest resumeRequest;
            if (aiRequest.getJobTitle() != null && !aiRequest.getJobTitle().isEmpty()) {
                resumeRequest = aiResumeService.generateResumeForJob(
                    aiRequest.getJobTitle(),
                    aiRequest.getUserInfo()
                );
            } else {
                resumeRequest = aiResumeService.generateResumeFromUserInfo(aiRequest.getUserInfo());
            }
//            // 保存简历
//            resumeService.createResume(resumeRequest, userId);
            ResumeGenerateResponse response = ResumeGenerateResponse.fromResumeCreateRequest(resumeRequest);
            return BaseResponse.success(response, "简历生成并保存成功");
        } catch (Exception e) {
            log.error("AI生成并保存简历失败", e);
            return BaseResponse.error(500, "AI生成并保存简历失败: " + e.getMessage());
        }
    }

    /**
     * 上传头像，返回可直接访问的URL
     */
    @PostMapping("/upload-avatar")
    public BaseResponse<String> uploadAvatar(HttpServletRequest httpRequest,
                                             @RequestParam("file") MultipartFile file) {
        Long userId = getUserIdFromToken(httpRequest);
        if (userId == null) {
            return BaseResponse.error(401, "未登录");
        }
        if (file == null || file.isEmpty()) {
            return BaseResponse.error(400, "请选择头像文件");
        }

        String original = file.getOriginalFilename();
        String suffix = ".png";
        if (StringUtils.hasText(original) && original.contains(".")) {
            suffix = original.substring(original.lastIndexOf('.')).toLowerCase();
        }
        if (!(".jpg".equals(suffix) || ".jpeg".equals(suffix) || ".png".equals(suffix) || ".gif".equals(suffix) || ".webp".equals(suffix))) {
            return BaseResponse.error(400, "仅支持 jpg/jpeg/png/gif/webp 图片格式");
        }

        try {
            Path dirPath = Paths.get(uploadPath, "user-avatar", String.valueOf(userId));
            File dir = dirPath.toFile();
            if (!dir.exists() && !dir.mkdirs()) {
                return BaseResponse.error(500, "头像目录创建失败");
            }

            String fileName = "avatar_" + UUID.randomUUID().toString().replace("-", "") + suffix;
            File target = dirPath.resolve(fileName).toFile();
            file.transferTo(target);

            String relativePath = "user-avatar/" + userId + "/" + fileName;
            int serverPort = httpRequest.getServerPort();
            boolean isDefaultPort = ("http".equalsIgnoreCase(httpRequest.getScheme()) && serverPort == 80)
                    || ("https".equalsIgnoreCase(httpRequest.getScheme()) && serverPort == 443);
            String host = httpRequest.getScheme() + "://" + httpRequest.getServerName() + (isDefaultPort ? "" : (":" + serverPort));
            String contextPath = StringUtils.hasText(httpRequest.getContextPath()) ? httpRequest.getContextPath() : "";
            String url = host + contextPath + "/uploads/" + relativePath;
            return BaseResponse.success(url, "头像上传成功");
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return BaseResponse.error(500, "头像上传失败: " + e.getMessage());
        }
    }

    private String buildUserInfoFromRequest(AiResumeRequest aiRequest) {
        if (StringUtils.hasText(aiRequest.getUserInfo())) {
            return aiRequest.getUserInfo();
        }

        StringBuilder builder = new StringBuilder();
        PersonalInfo personal = aiRequest.getPersonalInfo();
        if (personal != null) {
            appendLine(builder, "姓名", personal.getName());
            appendLine(builder, "性别", personal.getGender());
            appendLine(builder, "年龄", personal.getAge());
            appendLine(builder, "电话", personal.getPhone());
            appendLine(builder, "邮箱", personal.getEmail());
            appendLine(builder, "地址", personal.getAddress());
            appendLine(builder, "头像", aiRequest.getAvatarUrl());
            appendLine(builder, "个人简介", personal.getSummary());
        }
        appendLine(builder, "目标岗位", aiRequest.getTargetPosition());

        if (aiRequest.getEducation() != null && !aiRequest.getEducation().isEmpty()) {
            builder.append("\n教育背景：\n");
            for (EducationItem item : aiRequest.getEducation()) {
                builder.append("- ")
                        .append(emptyToDefault(item.getSchool(), ""))
                        .append(" ")
                        .append(emptyToDefault(item.getMajor(), ""))
                        .append(" ")
                        .append(emptyToDefault(item.getDegree(), ""))
                        .append(" ")
                        .append(emptyToDefault(item.getGraduationDate(), ""))
                        .append("\n");
            }
        }

        if (aiRequest.getWorkExperience() != null && !aiRequest.getWorkExperience().isEmpty()) {
            builder.append("\n工作经历：\n");
            for (WorkItem item : aiRequest.getWorkExperience()) {
                builder.append("- ")
                        .append(emptyToDefault(item.getCompany(), ""))
                        .append(" ")
                        .append(emptyToDefault(item.getPosition(), ""))
                        .append(" ")
                        .append(emptyToDefault(item.getStartDate(), ""))
                        .append("-")
                        .append(emptyToDefault(item.getEndDate(), "至今"))
                        .append("\n")
                        .append("  ")
                        .append(emptyToDefault(item.getDescription(), ""))
                        .append("\n");
            }
        }

        appendLine(builder, "项目经验", aiRequest.getProjects());
        appendLine(builder, "技能专长", aiRequest.getSkills());
        appendLine(builder, "其他信息", aiRequest.getAdditionalInfo());
        appendLine(builder, "简历模板", aiRequest.getTemplateType());
        appendLine(builder, "简历风格", aiRequest.getStyle());
        appendLine(builder, "生成要求", aiRequest.getPrompt());

        return builder.toString();
    }

    private void appendLine(StringBuilder builder, String title, String value) {
        if (StringUtils.hasText(value)) {
            builder.append(title).append("：").append(value).append("\n");
        }
    }

    private String emptyToDefault(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
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
    @Data
    public static class AiResumeRequest {
        private String userInfo;
        private String jobTitle;
        private String prompt;
        private String templateType;
        private String style;
        private String targetPosition;
        private String skills;
        private String projects;
        private String additionalInfo;
        private String avatarUrl;
        private PersonalInfo personalInfo;
        private List<EducationItem> education;
        private List<WorkItem> workExperience;
    }

    @Data
    public static class PersonalInfo {
        private String name;
        private String gender;
        private String age;
        private String phone;
        private String email;
        private String address;
        private String summary;
    }

    @Data
    public static class EducationItem {
        private String school;
        private String major;
        private String degree;
        private String graduationDate;
    }

    @Data
    public static class WorkItem {
        private String company;
        private String position;
        private String startDate;
        private String endDate;
        private String description;
    }

    /**
     * 封装简历链接返回对象
     */
    @Data
    public static class ResumeGenerateResponse {
        private List<LinkItem> links;
        private Map<String, Object> resumeData;

        public static ResumeGenerateResponse fromResumeCreateRequest(ResumeCreateRequest req) {
            ResumeGenerateResponse resp = new ResumeGenerateResponse();
            LinkItem item = new LinkItem();
            item.setImg_url(req.getImgUrl());
            item.setWord_url(req.getWordUrl());
            item.setPdf_url(req.getPdfUrl());
            resp.setLinks(Collections.singletonList(item));

            Map<String, Object> resumeData = new HashMap<>();
            resumeData.put("name", req.getName());
            resumeData.put("fullName", req.getFullName());
            resumeData.put("phone", req.getPhone());
            resumeData.put("email", req.getEmail());
            resumeData.put("avatarUrl", req.getAvatarUrl());
            resumeData.put("position", req.getPosition());
            resumeData.put("profile", req.getProfile());
            resumeData.put("workExperiences", req.getWorkExperiences());
            resumeData.put("educations", req.getEducations());
            resumeData.put("projectExperiences", req.getProjectExperiences());
            resumeData.put("skills", req.getSkills());
            resumeData.put("additionalInfos", req.getAdditionalInfos());
            resp.setResumeData(resumeData);

            return resp;
        }

        @Data
        public static class LinkItem {
            private String img_url;
            private String word_url;
            private String pdf_url;
        }
    }
}
