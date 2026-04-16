package com.aiinterview.controller.application;

import com.aiinterview.config.DifyApiConfig;
import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.application.ApplicationDetailDTO;
import com.aiinterview.model.dto.application.ApplicationSubmitDTO;
import com.aiinterview.model.dto.workflow.resumescoring.ResumeScoringRequest;
import com.aiinterview.model.entity.application.Application;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.service.application.ApplicationService;
import com.aiinterview.service.job.JobService;
import com.aiinterview.service.resume.ResumeService;
import com.aiinterview.utils.JwtUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 简历投递申请控制器
 */
@Slf4j
@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JwtUtils jwtUtils;
    private final ResumeService resumeService;
    private final JobService jobService;
    private final DifyApiConfig difyApiConfig;
    private final RestTemplate restTemplate;

    public ApplicationController(ApplicationService applicationService,
                                 JwtUtils jwtUtils,
                                 ResumeService resumeService,
                                 JobService jobService,
                                 DifyApiConfig difyApiConfig) {
        this.applicationService = applicationService;
        this.jwtUtils = jwtUtils;
        this.resumeService = resumeService;
        this.jobService = jobService;
        this.difyApiConfig = difyApiConfig;
        this.restTemplate = new RestTemplate();
    }

    /**
     * 投递简历（基础版：仅完成投递申请）
     */
    @PostMapping("/submit")
//    public Map<String, Object> submitApplication(
//            @RequestParam Long jobId,
//            @RequestParam Long resumeId,
//            HttpServletRequest request) {
//        try {
//            String token = request.getHeader("Authorization");
//            if (token == null || !token.startsWith("Bearer ")) {
//                Map<String, Object> result = new HashMap<>();
//                result.put("success", false);
//                result.put("message", "用户未登录");
//                return result;
//            }
//            token = token.substring(7);
//            Long userId = jwtUtils.getUserIdFromToken(token);
//            if (userId == null) {
//                Map<String, Object> result = new HashMap<>();
//                result.put("success", false);
//                result.put("message", "用户未登录");
//                return result;
//            }

            public ApiResponse<Application> submitApplication(@Valid @RequestBody ApplicationSubmitDTO submitDTO,
                                                      HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            Long jobId = submitDTO.getJobId();
            Long resumeId = submitDTO.getResumeId();

            log.info("收到简历投递请求: userId={}, jobId={}, resumeId={}", userId, jobId, resumeId);

            // ========== 添加这些打印语句 ==========
            System.out.println("=== 调试信息 ===");
            System.out.println("userId: " + userId);
            System.out.println("jobId: " + jobId);
            System.out.println("resumeId: " + resumeId);
            System.out.println("submitDTO: " + submitDTO);
            // ==================================

            // 提交投递申请
            Application application = applicationService.submitApplication(userId, jobId, resumeId);

            log.info("简历投递成功: applicationId={}", application.getApplicationId());
            return ApiResponse.success("投递成功", application);

        } catch (Exception e) {
            log.error("投递简历失败: {}", e.getMessage(), e);
            return ApiResponse.error(e.getMessage());
        }
    }
    /**
     * 简历筛选打分（独立接口：用于后台分析）
     */
    @PostMapping("/analyze")
    public ApiResponse<Map<String, Object>> analyzeResume(@Valid @RequestBody ApplicationSubmitDTO submitDTO,
                                                          HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            Long jobId = submitDTO.getJobId();
            Long resumeId = submitDTO.getResumeId();

            log.info("收到简历筛选打分请求: userId={}, jobId={}, resumeId={}", userId, jobId, resumeId);

            // 步骤1: 调用ExternalResumeController的analyze-existing接口
            log.info("步骤1: 调用简历分析接口...");
            Map<String, Object> analysisResult = callExternalAnalyzeApi(resumeId, "user-" + userId);

            if (analysisResult == null || !(Boolean) analysisResult.get("success")) {
                return ApiResponse.error("简历分析失败");
            }

            // 步骤2: 解析分析结果
            Map<String, Object> analysisData = (Map<String, Object>) analysisResult.get("data");
            Map<String, Object> analysis = (Map<String, Object>) analysisData.get("analysis");
            Map<String, Object> analysisDataInner = (Map<String, Object>) analysis.get("data");
            Map<String, Object> outputs = (Map<String, Object>) analysisDataInner.get("outputs");
            Map<String, Object> endEponing = (Map<String, Object>) outputs.get("end_eponing");

            // 解析overallScore，处理可能的类型转换
            Integer overallScore = null;
            Object scoreObj = endEponing.get("overallScore");
            if (scoreObj != null) {
                if (scoreObj instanceof Integer) {
                    overallScore = (Integer) scoreObj;
                } else if (scoreObj instanceof Double) {
                    overallScore = ((Double) scoreObj).intValue();
                } else if (scoreObj instanceof Float) {
                    overallScore = ((Float) scoreObj).intValue();
                }
            }

            if (overallScore == null) {
                // 如果没有overallScore，计算平均分
                overallScore = calculateOverallScore(endEponing);
            }

            // 步骤3: 更新Application表
            log.info("步骤3: 更新申请记录...");
            updateApplicationWithAnalysis(userId, jobId, resumeId, analysisResult, overallScore);

            log.info("简历筛选打分完成: userId={}, jobId={}, resumeId={}, score={}", userId, jobId, resumeId, overallScore);
            return ApiResponse.success("筛选打分完成", analysisResult);

        } catch (Exception e) {
            log.error("简历筛选打分失败: {}", e.getMessage(), e);
            return ApiResponse.error(e.getMessage());
        }
    }



    /**
     * 获取已投递的详细记录列表
     */
    @GetMapping("/submitted-jobs")
    public ApiResponse<List<ApplicationDetailDTO>> getSubmittedJobs(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<ApplicationDetailDTO> applicationDetails = applicationService.getSubmittedApplicationDetails(userId);
            return ApiResponse.success("获取成功", applicationDetails);
        } catch (Exception e) {
            log.error("获取已投递职位详细信息失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    /**
     * 获取已投递的职位ID列表
     */
    @GetMapping("/submitted-job-ids")
    public ApiResponse<List<Long>> getSubmittedJobIds(HttpServletRequest request) {
        try {
            // 从请求中解析用户ID
            Long userId = getUserIdFromToken(request);

            // 获取用户已投递的职位ID列表
            List<Long> jobIds = applicationService.getSubmittedJobIds(userId);
            return ApiResponse.success("已投递职位ID列表获取成功", jobIds);
        } catch (Exception e) {
            log.error("获取已投递职位ID列表失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取投递历史
     */
    @GetMapping("/history")
    public ApiResponse<List<Application>> getApplicationHistory(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<Application> applications = applicationService.getApplicationsByUserId(userId);
            return ApiResponse.success("获取成功", applications);
        } catch (Exception e) {
            log.error("获取投递历史失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取申请状态
     */
    @GetMapping("/{applicationId}/status")
    public ApiResponse<Application> getApplicationStatus(@PathVariable Long applicationId,
                                                         HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            Application application = applicationService.getApplicationById(applicationId, userId);
            if (application == null) {
                return ApiResponse.error("申请记录不存在");
            }
            return ApiResponse.success("获取成功", application);
        } catch (Exception e) {
            log.error("获取申请状态失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 撤回申请
     */
    @PostMapping("/{applicationId}/withdraw")
    public ApiResponse<String> withdrawApplication(@PathVariable Long applicationId,
                                                   HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            boolean success = applicationService.withdrawApplication(applicationId, userId);
            if (success) {
                return ApiResponse.success("撤回成功", "申请已撤回");
            } else {
                return ApiResponse.error("撤回失败");
            }
        } catch (Exception e) {
            log.error("撤回申请失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 上传简历到外部API
     */
    public String uploadResumeToExternalApi(File resumeFile, String user) {
        try {
            String uploadUrl = difyApiConfig.getFileUploadUrl();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", difyApiConfig.getResumeAuthorizationHeader());

            // 创建文件资源
            FileSystemResource fileResource = new FileSystemResource(resumeFile);

            // 构建multipart请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("user", user);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("调用外部文件上传接口: {}", uploadUrl);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);

            log.info("外部API响应状态: {}", response.getStatusCode());
            log.info("外部API响应内容: {}", response.getBody());

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                JSONObject responseJson = JSON.parseObject(response.getBody());

                // 检查是否有success字段的包装格式
                if (responseJson.containsKey("success")) {
                    if (responseJson.getBoolean("success")) {
                        JSONObject data = responseJson.getJSONObject("data");
                        if (data != null) {
                            return data.getString("id");
                        }
                    }
                } else {
                    // 直接返回文件信息的格式（如当前情况）
                    if (responseJson.containsKey("id")) {
                        String fileId = responseJson.getString("id");
                        log.info("成功获取文件ID: {}", fileId);
                        return fileId;
                    }
                }
            }

            log.error("无法从响应中解析文件ID，响应状态: {}, 响应内容: {}",
                    response.getStatusCode(), response.getBody());
            return null;

        } catch (Exception e) {
            log.error("上传简历到外部API失败", e);
            return null;
        }
    }

    /**
     * 构建职位描述字符串
     */
    private String buildJobDescriptionString(Job job) {
        StringBuilder jdBuilder = new StringBuilder();

        jdBuilder.append("职位名称: ").append(job.getTitle()).append("\n");
        jdBuilder.append("职位类型: ").append(job.getJobType()).append("\n");
        jdBuilder.append("工作地点: ").append(job.getLocation()).append("\n");

        if (job.getMinSalary() != null && job.getMaxSalary() != null) {
            jdBuilder.append("薪资范围: ").append(job.getMinSalary()).append("K-").append(job.getMaxSalary()).append("K\n");
        }

        if (job.getExperience() != null && !job.getExperience().trim().isEmpty()) {
            jdBuilder.append("经验要求: ").append(job.getExperience()).append("\n");
        }

        if (job.getEducation() != null && !job.getEducation().trim().isEmpty()) {
            jdBuilder.append("学历要求: ").append(job.getEducation()).append("\n");
        }

        if (job.getDescription() != null && !job.getDescription().trim().isEmpty()) {
            jdBuilder.append("岗位职责: ").append(job.getDescription()).append("\n");
        }

        if (job.getRequirements() != null && !job.getRequirements().trim().isEmpty()) {
            jdBuilder.append("岗位要求: ").append(job.getRequirements()).append("\n");
        }

        if (job.getSkills() != null && !job.getSkills().trim().isEmpty()) {
            jdBuilder.append("职位技能: ").append(job.getSkills()).append("\n");
        }

        return jdBuilder.toString();
    }

    /**
     * 调用简历筛选打分API
     */
    private Map<String, Object> callResumeScoringApi(String uploadFileId, String jdString, String user) {
        try {
            String scoringUrl = "http://localhost:8089/api/resume-scoring/analyze";

            // 构建请求体
            ResumeScoringRequest scoringRequest = new ResumeScoringRequest();
            scoringRequest.setUploadFileId(uploadFileId);
            scoringRequest.setJd(jdString);
            scoringRequest.setQuery("筛选简历并打分");
            scoringRequest.setConversationId("");
            scoringRequest.setUser(user);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ResumeScoringRequest> requestEntity = new HttpEntity<>(scoringRequest, headers);

            log.info("调用简历筛选打分接口: {}", scoringUrl);
            log.info("请求参数: {}", JSON.toJSONString(scoringRequest));

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(scoringUrl, requestEntity, String.class);

            log.info("简历筛选打分API响应状态: {}", response.getStatusCode());
            log.info("简历筛选打分API响应内容: {}", response.getBody());

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject responseJson = JSON.parseObject(response.getBody());
                if (responseJson.getBoolean("success")) {
                    return responseJson.getJSONObject("data").getInnerMap();
                }
            }

            return null;

        } catch (Exception e) {
            log.error("调用简历筛选打分API失败", e);
            return null;
        }
    }

    /**
     * 调用ExternalResumeController的analyze-existing接口
     */
    private Map<String, Object> callExternalAnalyzeApi(Long resumeId, String user) {
        try {
            String analyzeUrl = "http://localhost:8089/api/external-resume/analyze-existing";

            // 构建multipart请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("resume_id", resumeId);
            body.add("user", user);
            body.add("variable_name", "Resume_analysis");
            body.add("document_type", "document");
            body.add("response_mode", "blocking");

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            log.info("调用简历分析接口: {}", analyzeUrl);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(analyzeUrl, requestEntity, String.class);

            log.info("分析接口响应状态: {}", response.getStatusCode());
            log.info("分析接口响应内容: {}", response.getBody());

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject responseJson = JSON.parseObject(response.getBody());
                return responseJson.getInnerMap();
            }

            return null;

        } catch (Exception e) {
            log.error("调用简历分析接口失败", e);
            return null;
        }
    }

    /**
     * 计算综合评分
     */
    private Integer calculateOverallScore(Map<String, Object> endEponing) {
        try {
            int totalScore = 0;
            int count = 0;

            // 获取各项评分，处理可能的类型转换
            Map<String, Object> skills = (Map<String, Object>) endEponing.get("skills");
            if (skills != null && skills.get("matchScore") != null) {
                Object scoreObj = skills.get("matchScore");
                int score = scoreObj instanceof Integer ? (Integer) scoreObj :
                        scoreObj instanceof Double ? ((Double) scoreObj).intValue() : 0;
                totalScore += score;
                count++;
            }

            Map<String, Object> personalInfo = (Map<String, Object>) endEponing.get("personalInfo");
            if (personalInfo != null && personalInfo.get("matchScore") != null) {
                Object scoreObj = personalInfo.get("matchScore");
                int score = scoreObj instanceof Integer ? (Integer) scoreObj :
                        scoreObj instanceof Double ? ((Double) scoreObj).intValue() : 0;
                totalScore += score;
                count++;
            }

            Map<String, Object> educationBackground = (Map<String, Object>) endEponing.get("educationBackground");
            if (educationBackground != null && educationBackground.get("matchScore") != null) {
                Object scoreObj = educationBackground.get("matchScore");
                int score = scoreObj instanceof Integer ? (Integer) scoreObj :
                        scoreObj instanceof Double ? ((Double) scoreObj).intValue() : 0;
                totalScore += score;
                count++;
            }

            Map<String, Object> projectExperience = (Map<String, Object>) endEponing.get("projectExperience");
            if (projectExperience != null && projectExperience.get("matchScore") != null) {
                Object scoreObj = projectExperience.get("matchScore");
                int score = scoreObj instanceof Integer ? (Integer) scoreObj :
                        scoreObj instanceof Double ? ((Double) scoreObj).intValue() : 0;
                totalScore += score;
                count++;
            }

            return count > 0 ? totalScore / count : 0;
        } catch (Exception e) {
            log.error("计算综合评分失败", e);
            return 0;
        }
    }

    /**
     * 更新申请记录的分析结果
     */
    private void updateApplicationWithAnalysis(Long userId, Long jobId, Long resumeId,
                                               Map<String, Object> analysisResult, Integer overallScore) {
        try {
            // 查找对应的申请记录
            Application application = applicationService.findByUserAndJobAndResume(userId, jobId, resumeId);
            if (application == null) {
                log.warn("未找到对应的申请记录: userId={}, jobId={}, resumeId={}", userId, jobId, resumeId);
                return;
            }

            // 将分析结果转换为JSON字符串
            String feedbackJson = JSON.toJSONString(analysisResult);

            // 更新申请记录
            application.setFeedback(feedbackJson);
            application.setAiEvaluationScore(overallScore.floatValue());

            // 根据评分更新状态
            if (overallScore < 70) {
                application.setStatus("淘汰");
            } else {
                application.setStatus("待笔试");
            }

            applicationService.updateApplication(application);

            log.info("申请记录更新成功: applicationId={}, score={}, status={}",
                    application.getApplicationId(), overallScore, application.getStatus());

        } catch (Exception e) {
            log.error("更新申请记录失败", e);
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

    /**
     * 求职者接受面试邀请
     */
    @PostMapping("/submitted-jobs/{applicationId}/accept")
    public ApiResponse<String> acceptInterviewInvitation(@PathVariable Long applicationId,
                                                         HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            log.info("求职者接受面试邀请: applicationId={}, userId={}", applicationId, userId);

            // 获取申请记录并验证权限
            Application application = applicationService.getApplicationById(applicationId, userId);
            if (application == null) {
                return ApiResponse.error("申请记录不存在或无权限访问");
            }

            // 检查当前状态是否可以接受面试
            if (!"待面试".equals(application.getStatus()) && !"interview".equals(application.getStatus())) {
                return ApiResponse.error("当前状态不允许接受面试邀请");
            }

            // 更新状态为"接受面试"
            boolean success = applicationService.updateApplicationStatus(applicationId, "接受面试", "求职者已接受面试邀请");

            if (success) {
                log.info("接受面试邀请成功: applicationId={}", applicationId);
                return ApiResponse.success("已接受面试邀请", "操作成功");
            } else {
                return ApiResponse.error("接受面试邀请失败");
            }
        } catch (Exception e) {
            log.error("接受面试邀请失败: applicationId={}, error={}", applicationId, e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 求职者拒绝面试邀请
     */
    @PostMapping("/interview-invitations/{applicationId}/reject")
    public ApiResponse<String> rejectInterviewInvitation(@PathVariable Long applicationId,
                                                         HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            log.info("求职者拒绝面试邀请: applicationId={}, userId={}", applicationId, userId);

            // 获取申请记录并验证权限
            Application application = applicationService.getApplicationById(applicationId, userId);
            if (application == null) {
                return ApiResponse.error("申请记录不存在或无权限访问");
            }

            // 检查当前状态是否可以拒绝面试
            if (!"待面试".equals(application.getStatus()) && !"interview".equals(application.getStatus())) {
                return ApiResponse.error("当前状态不允许拒绝面试邀请");
            }

            // 更新状态为"拒绝面试"
            boolean success = applicationService.updateApplicationStatus(applicationId, "拒绝面试", "求职者已拒绝面试邀请");

            if (success) {
                log.info("拒绝面试邀请成功: applicationId={}", applicationId);
                return ApiResponse.success("已拒绝面试邀请", "操作成功");
            } else {
                return ApiResponse.error("拒绝面试邀请失败");
            }
        } catch (Exception e) {
            log.error("拒绝面试邀请失败: applicationId={}, error={}", applicationId, e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 求职者完成面试
     */
    @PostMapping("/submitted-jobs/{applicationId}/complete")
    public ApiResponse<String> completeInterview(@PathVariable Long applicationId,
                                                 HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            log.info("求职者完成面试: applicationId={}, userId={}", applicationId, userId);

            // 获取申请记录并验证权限
            Application application = applicationService.getApplicationById(applicationId, userId);
            if (application == null) {
                return ApiResponse.error("申请记录不存在或无权限访问");
            }

            // 检查当前状态是否可以完成面试
            if (!"接受面试".equals(application.getStatus()) && !"interview_accepted".equals(application.getStatus())) {
                return ApiResponse.error("当前状态不允许完成面试");
            }

            // 更新状态为"完成面试"
            boolean success = applicationService.updateApplicationStatus(applicationId, "完成面试", "求职者已完成面试");

            if (success) {
                log.info("完成面试成功: applicationId={}", applicationId);
                return ApiResponse.success("面试已完成", "操作成功");
            } else {
                return ApiResponse.error("完成面试失败");
            }
        } catch (Exception e) {
            log.error("完成面试失败: applicationId={}, error={}", applicationId, e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}
