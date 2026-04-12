//package com.aiinterview.controller.resume;
//
//import com.aiinterview.config.DifyApiConfig;
//import com.aiinterview.model.entity.resume.Resume;
//import com.aiinterview.service.resume.ResumeService;
//import com.aiinterview.utils.JwtUtils;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.*;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.File;
//
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 外部简历分析控制器
// * 调用外部API实现简历上传和分析功能
// */
//@Slf4j
//@RestController
//@RequestMapping("/external-resume")
//@CrossOrigin(origins = "*")
//public class ExternalResumeController {
//
//    @Autowired
//    private DifyApiConfig difyApiConfig;
//
//    @Autowired
//    private ResumeService resumeService;
//
//    @Autowired
//    private JwtUtils jwtUtils;
//
//    private final RestTemplate restTemplate;
//
//    public ExternalResumeController() {
//        this.restTemplate = new RestTemplate();
//    }
//
//    /**
//     * 简历上传接口
//     * 调用外部API: /files/upload
//     */
//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, Object>> uploadResume(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("user") String user) {
//
//        log.info("开始上传简历文件: 文件名={}, 用户={}", file.getOriginalFilename(), user);
//
//        try {
//            String uploadUrl = difyApiConfig.getFileUploadUrl();
//
//            // 设置请求头
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//            headers.set("Authorization", difyApiConfig.getResumeAuthorizationHeader());
//
//            // 创建文件资源
//            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
//                @Override
//                public String getFilename() {
//                    return file.getOriginalFilename();
//                }
//            };
//
//            // 构建multipart请求体
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("file", fileResource);
//            body.add("user", user);
//
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//            log.info("调用外部文件上传接口: {}", uploadUrl);
//
//            // 发送请求
//            ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
//
//            log.info("外部API响应状态: {}", response.getStatusCode());
//            log.info("外部API响应内容: {}", response.getBody());
//
//            // 解析响应
//            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
//                JSONObject responseJson = JSON.parseObject(response.getBody());
//
//                Map<String, Object> result = new HashMap<>();
//                result.put("success", true);
//                result.put("message", "文件上传成功");
//                result.put("data", responseJson);
//
//                return ResponseEntity.ok(result);
//            } else {
//                Map<String, Object> result = new HashMap<>();
//                result.put("success", false);
//                result.put("message", "文件上传失败，状态码: " + response.getStatusCode());
//                return ResponseEntity.ok(result);
//            }
//
//        } catch (IOException e) {
//            log.error("读取文件内容失败", e);
//            Map<String, Object> result = new HashMap<>();
//            result.put("success", false);
//            result.put("message", "读取文件内容失败: " + e.getMessage());
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            log.error("调用外部API失败", e);
//            Map<String, Object> result = new HashMap<>();
//            result.put("success", false);
//            result.put("message", "调用外部API失败: " + e.getMessage());
//            return ResponseEntity.ok(result);
//        }
//    }
//
//    /**
//     * 简历分析接口
//     * 调用外部API: /workflows/run
//     * 使用blocking模式获取完整JSON响应
//     */
//    @PostMapping("/analyze")
//    public ResponseEntity<Map<String, Object>> analyzeResume(
//            @RequestParam("file_id") String fileId,
//            @RequestParam("user") String user,
//            @RequestParam(value = "variable_name", defaultValue = "Resume_analysis") String variableName,
//            @RequestParam(value = "document_type", defaultValue = "document") String documentType,
//            //使用blocking模式替代streaming模式
//            @RequestParam(value = "response_mode", defaultValue = "blocking") String responseMode) {
//
//        log.info("开始分析简历: 文件ID={}, 用户={}", fileId, user);
//
//        try {
//            String workflowUrl = difyApiConfig.getWorkflowRunUrl();
//
//            // 设置请求头
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Authorization", difyApiConfig.getResumeAuthorizationHeader());
//
//            // 构建请求体
//            Map<String, Object> requestBody = new HashMap<>();
//
//            // 构建inputs
//            Map<String, Object> inputs = new HashMap<>();
//            List<Map<String, Object>> fileList = new ArrayList<>();
//
//            Map<String, Object> fileInfo = new HashMap<>();
//            fileInfo.put("transfer_method", "local_file");
//            fileInfo.put("upload_file_id", fileId);
//            fileInfo.put("type", documentType);
//
//            fileList.add(fileInfo);
//            inputs.put(variableName, fileList);
//
//            requestBody.put("inputs", inputs);
//            requestBody.put("response_mode", responseMode);
//            requestBody.put("user", user);
//
//            String requestBodyJson = JSON.toJSONString(requestBody);
//            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, headers);
//
//            log.info("调用外部工作流接口: {}", workflowUrl);
//            log.info("请求体: {}", requestBodyJson);
//
//            // 发送请求
//            ResponseEntity<String> response = restTemplate.postForEntity(workflowUrl, requestEntity, String.class);
//
//            log.info("外部工作流API响应状态: {}", response.getStatusCode());
//            log.info("外部工作流API响应内容: {}", response.getBody());
//
//            // 解析响应
//            if (response.getStatusCode() == HttpStatus.OK) {
//                // 先检查响应内容是否为有效的JSON
//                String responseBody = response.getBody();
//                if (responseBody != null && (responseBody.startsWith("{") || responseBody.startsWith("["))) {
//                    JSONObject responseJson = JSON.parseObject(responseBody);
//
//                    Map<String, Object> result = new HashMap<>();
//                    result.put("success", true);
//                    result.put("message", "简历分析成功");
//                    result.put("data", responseJson);
//
//                    return ResponseEntity.ok(result);
//                } else {
//                    Map<String, Object> result = new HashMap<>();
//                    result.put("success", false);
//                    result.put("message", "简历分析响应不是有效的JSON格式");
//                    result.put("raw_response", responseBody);
//                    return ResponseEntity.ok(result);
//                }
//            } else {
//                Map<String, Object> result = new HashMap<>();
//                result.put("success", false);
//                result.put("message", "简历分析失败，状态码: " + response.getStatusCode());
//                result.put("raw_response", response.getBody());
//                return ResponseEntity.ok(result);
//            }
//
//        } catch (Exception e) {
//            log.error("调用外部工作流API失败", e);
//            Map<String, Object> result = new HashMap<>();
//            result.put("success", false);
//            result.put("message", "调用外部工作流API失败: " + e.getMessage());
//            return ResponseEntity.ok(result);
//        }
//    }
//
//    /**
//     * 完整的简历上传和分析流程
//     */
//    @PostMapping("/upload-and-analyze")
//    public ResponseEntity<Map<String, Object>> uploadAndAnalyzeResume(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("user") String user,
//            @RequestParam(value = "variable_name", defaultValue = "Resume_analysis") String variableName,
//            @RequestParam(value = "document_type", defaultValue = "document") String documentType,
//            @RequestParam(value = "response_mode", defaultValue = "blocking") String responseMode) {
//
//        log.info("开始完整的简历上传和分析流程");
//
//        try {
//            // 步骤1: 上传文件
//            log.info("步骤1: 上传简历文件...");
//            ResponseEntity<Map<String, Object>> uploadResult = uploadResume(file, user);
//
//            if (!uploadResult.getBody().get("success").equals(true)) {
//                return uploadResult; // 上传失败，直接返回错误
//            }
//
//            // 从上传响应中获取文件ID
//            Map<String, Object> uploadData = (Map<String, Object>) uploadResult.getBody().get("data");
//            String fileId = (String) uploadData.get("id");
//
//            if (fileId == null) {
//                Map<String, Object> result = new HashMap<>();
//                result.put("success", false);
//                result.put("message", "无法从上传响应中获取文件ID");
//                return ResponseEntity.ok(result);
//            }
//
//            log.info("文件上传成功，文件ID: {}", fileId);
//
//            // 步骤2: 分析简历
//            log.info("步骤2: 分析简历...");
//            ResponseEntity<Map<String, Object>> analyzeResult = analyzeResume(fileId, user, variableName, documentType, responseMode);
//
//            // 合并结果
//            Map<String, Object> finalResult = new HashMap<>();
//            finalResult.put("success", analyzeResult.getBody().get("success"));
//            finalResult.put("message", "简历上传和分析完成");
//
//            Map<String, Object> combinedData = new HashMap<>();
//            combinedData.put("upload", uploadData);
//            combinedData.put("analysis", analyzeResult.getBody().get("data"));
//
//            finalResult.put("data", combinedData);
//
//            return ResponseEntity.ok(finalResult);
//
//        } catch (Exception e) {
//            log.error("完整流程执行失败", e);
//            Map<String, Object> result = new HashMap<>();
//            result.put("success", false);
//            result.put("message", "完整流程执行失败: " + e.getMessage());
//            return ResponseEntity.ok(result);
//        }
//    }
//
//    /**
//     * 获取用户已上传的简历列表
//     */
//    @GetMapping("/resumes")
//    public ResponseEntity<Map<String, Object>> getUserResumes(HttpServletRequest request) {
//        try {
//            Long userId = getUserIdFromToken(request);
//            List<Resume> resumes = resumeService.getResumesByUserId(userId);
//
//            Map<String, Object> result = new HashMap<>();
//            result.put("success", true);
//            result.put("message", "获取简历列表成功");
//            result.put("data", resumes);
//
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            log.error("获取简历列表失败", e);
//            Map<String, Object> result = new HashMap<>();
//            result.put("success", false);
//            result.put("message", "获取简历列表失败: " + e.getMessage());
//            return ResponseEntity.ok(result);
//        }
//    }
//
//    /**
//     * 分析已上传的简历文件
//     */
//    @PostMapping("/analyze-existing")
//    public ResponseEntity<Map<String, Object>> analyzeExistingResume(
//            @RequestParam("resume_id") Long resumeId,
//            @RequestParam("user") String user,
//            @RequestParam(value = "variable_name", defaultValue = "Resume_analysis") String variableName,
//            @RequestParam(value = "document_type", defaultValue = "document") String documentType,
//            @RequestParam(value = "response_mode", defaultValue = "blocking") String responseMode,
//            HttpServletRequest request) {
//
//        log.info("开始分析已上传的简历: resumeId={}, user={}", resumeId, user);
//
//        try {
//            // 由于此接口不需要认证，我们需要从resumeId获取userId
//            // 或者直接使用resumeId查询，不验证用户权限
//
//            // 获取简历文件路径 - 不验证用户权限
//            String filePath = resumeService.getResumeFilePath(resumeId, null);
//            if (filePath == null) {
//                Map<String, Object> result = new HashMap<>();
//                result.put("success", false);
//                result.put("message", "简历文件不存在");
//                return ResponseEntity.ok(result);
//            }
//
//            File file = new File(filePath);
//            if (!file.exists()) {
//                Map<String, Object> result = new HashMap<>();
//                result.put("success", false);
//                result.put("message", "简历文件不存在: " + filePath);
//                return ResponseEntity.ok(result);
//            }
//
//            // 步骤1: 上传文件到外部API
//            log.info("步骤1: 上传简历文件到外部API...");
//            String fileId = uploadFileToExternalApi(file, user);
//
//            if (fileId == null) {
//                Map<String, Object> result = new HashMap<>();
//                result.put("success", false);
//                result.put("message", "文件上传到外部API失败");
//                return ResponseEntity.ok(result);
//            }
//
//            log.info("文件上传成功，文件ID: {}", fileId);
//
//            // 步骤2: 分析简历
//            log.info("步骤2: 分析简历...");
//            ResponseEntity<Map<String, Object>> analyzeResult = analyzeResume(fileId, user, variableName, documentType, responseMode);
//
//            // 构建与upload-and-analyze一致的响应结构
//            if (analyzeResult.getStatusCode() == HttpStatus.OK) {
//                Map<String, Object> analyzeData = analyzeResult.getBody();
//                if (analyzeData != null && (Boolean) analyzeData.get("success")) {
//                    // 创建包含upload和analysis的完整响应
//                    Map<String, Object> completeResult = new HashMap<>();
//                    Map<String, Object> data = new HashMap<>();
//
//                    // 添加upload信息（模拟结构，因为文件已经上传过了）
//                    Map<String, Object> uploadInfo = new HashMap<>();
//                    uploadInfo.put("id", fileId);
//                    uploadInfo.put("name", file.getName());
//                    uploadInfo.put("size", file.length());
//                    uploadInfo.put("extension", getFileExtension(file.getName()));
//                    uploadInfo.put("mime_type", "application/pdf");
//                    uploadInfo.put("created_at", System.currentTimeMillis() / 1000);
//
//                    data.put("upload", uploadInfo);
//                    data.put("analysis", analyzeData.get("data"));
//
//                    completeResult.put("success", true);
//                    completeResult.put("message", "简历分析成功");
//                    completeResult.put("data", data);
//
//                    return ResponseEntity.ok(completeResult);
//                }
//            }
//
//            return analyzeResult;
//
//        } catch (Exception e) {
//            log.error("分析已上传简历失败", e);
//            Map<String, Object> result = new HashMap<>();
//            result.put("success", false);
//            result.put("message", "分析已上传简历失败: " + e.getMessage());
//            return ResponseEntity.ok(result);
//        }
//    }
//
//    /**
//     * 上传文件到外部API的私有方法
//     */
//    public String uploadFileToExternalApi(File file, String user) {
//        try {
//            String uploadUrl = difyApiConfig.getFileUploadUrl();
//
//            // 设置请求头
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//            headers.set("Authorization", difyApiConfig.getResumeAuthorizationHeader());
//
//            // 创建文件资源
//            FileSystemResource fileResource = new FileSystemResource(file);
//
//            // 构建multipart请求体
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("file", fileResource);
//            body.add("user", user);
//
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//            log.info("调用外部文件上传接口: {}", uploadUrl);
//
//            // 发送请求
//            ResponseEntity<String> response = restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
//
//            log.info("外部API响应状态: {}", response.getStatusCode());
//            log.info("外部API响应内容: {}", response.getBody());
//
//            // 解析响应
//            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
//                JSONObject responseJson = JSON.parseObject(response.getBody());
//
//                // 检查是否有success字段的包装格式
//                if (responseJson.containsKey("success")) {
//                    if (responseJson.getBoolean("success")) {
//                        JSONObject data = responseJson.getJSONObject("data");
//                        if (data != null) {
//                            return data.getString("id");
//                        }
//                    }
//                } else {
//                    // 直接返回文件信息的格式（如当前情况）
//                    if (responseJson.containsKey("id")) {
//                        String fileId = responseJson.getString("id");
//                        log.info("成功获取文件ID: {}", fileId);
//                        return fileId;
//                    }
//                }
//            }
//
//            log.error("无法从响应中解析文件ID，响应状态: {}, 响应内容: {}",
//                     response.getStatusCode(), response.getBody());
//            return null;
//
//        } catch (Exception e) {
//            log.error("上传文件到外部API失败", e);
//            return null;
//        }
//    }
//
//    /**
//     * 获取文件扩展名
//     */
//    private String getFileExtension(String fileName) {
//        if (fileName == null || fileName.lastIndexOf('.') == -1) {
//            return "";
//        }
//        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
//    }
//
//    /**
//     * 从Token中获取用户ID
//     */
//    private Long getUserIdFromToken(HttpServletRequest request) {
//        try {
//            String token = request.getHeader("Authorization");
//            if (token == null || !token.startsWith("Bearer ")) {
//                throw new RuntimeException("未找到有效的认证信息");
//            }
//
//            token = token.substring(7);
//            return jwtUtils.getUserIdFromToken(token);
//        } catch (Exception e) {
//            log.error("获取用户ID失败: {}", e.getMessage());
//            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 健康检查接口
//     */
//    @GetMapping("/health")
//    public ResponseEntity<Map<String, Object>> health() {
//        Map<String, Object> result = new HashMap<>();
//        result.put("status", "OK");
//        result.put("message", "外部简历分析服务正常运行");
//        result.put("external_api", difyApiConfig.getBaseUrl());
//        result.put("resume_workflow_id", difyApiConfig.getResumeWorkflowId());
//        return ResponseEntity.ok(result);
//    }
//}
