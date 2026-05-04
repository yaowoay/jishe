package com.aiinterview.controller.resume;

import com.aiinterview.model.entity.resume.Resume;
import com.aiinterview.service.resume.ResumeService;
import com.aiinterview.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外部简历分析控制器
 * 使用模拟数据实现简历上传和分析功能
 */
@Slf4j
@RestController
@RequestMapping("/external-resume")
@CrossOrigin(origins = "*")
public class ExternalResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 简历上传接口
     * 使用模拟数据替代外部API调用
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("user") String user) {

        log.info("开始上传简历文件: 文件名={}, 用户={}", file.getOriginalFilename(), user);

        try {
            // 模拟上传处理时间
            Thread.sleep(1000);

            // 生成模拟的文件上传响应
            Map<String, Object> mockUploadData = generateMockUploadResponse(file);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "文件上传成功");
            result.put("data", mockUploadData);

            log.info("文件上传成功，返回模拟数据");
            return ResponseEntity.ok(result);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("上传过程被中断", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "上传过程被中断");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 生成模拟的文件上传响应数据
     */
    private Map<String, Object> generateMockUploadResponse(MultipartFile file) {
        Map<String, Object> uploadData = new HashMap<>();
        
        // 生成模拟的文件ID
        String mockFileId = "file_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        
        uploadData.put("id", mockFileId);
        uploadData.put("name", file.getOriginalFilename());
        uploadData.put("size", file.getSize());
        uploadData.put("extension", getFileExtension(file.getOriginalFilename()));
        uploadData.put("mime_type", file.getContentType());
        uploadData.put("created_at", System.currentTimeMillis() / 1000);
        uploadData.put("created_by", "system");
        
        return uploadData;
    }

    /**
     * 简历分析接口
     * 使用模拟数据替代外部API调用
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeResume(
            @RequestParam("file_id") String fileId,
            @RequestParam("user") String user,
            @RequestParam(value = "variable_name", defaultValue = "Resume_analysis") String variableName,
            @RequestParam(value = "document_type", defaultValue = "document") String documentType,
            @RequestParam(value = "response_mode", defaultValue = "blocking") String responseMode) {

        log.info("开始分析简历: 文件ID={}, 用户={}", fileId, user);

        try {
            // 模拟分析延迟，增加真实感
            Thread.sleep(3000);

            // 生成模拟的前端简历分析数据
            Map<String, Object> mockAnalysisData = generateMockFrontendResumeAnalysis();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "简历分析成功");
            result.put("data", mockAnalysisData);

            log.info("简历分析完成，返回模拟数据");
            return ResponseEntity.ok(result);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("分析过程被中断", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "分析过程被中断");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("简历分析失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "简历分析失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 生成模拟的前端简历分析数据
     */
    private Map<String, Object> generateMockFrontendResumeAnalysis() {
        Map<String, Object> analysisData = new HashMap<>();
        
        // 基本信息
        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("name", "张小明");
        basicInfo.put("position", "前端开发工程师");
        basicInfo.put("experience", "3年");
        basicInfo.put("education", "本科");
        basicInfo.put("phone", "138****8888");
        basicInfo.put("email", "zhangxiaoming@example.com");
        basicInfo.put("location", "北京市");
        
        // 技能评估
        Map<String, Object> skillsAssessment = new HashMap<>();
        List<Map<String, Object>> technicalSkills = new ArrayList<>();
        
        Map<String, Object> skill1 = new HashMap<>();
        skill1.put("skill", "Vue.js");
        skill1.put("level", "熟练");
        skill1.put("score", 85);
        skill1.put("description", "具备3年Vue.js开发经验，熟悉组件化开发、状态管理");
        technicalSkills.add(skill1);
        
        Map<String, Object> skill2 = new HashMap<>();
        skill2.put("skill", "React");
        skill2.put("level", "良好");
        skill2.put("score", 75);
        skill2.put("description", "有2年React开发经验，熟悉Hooks、Redux状态管理");
        technicalSkills.add(skill2);
        
        Map<String, Object> skill3 = new HashMap<>();
        skill3.put("skill", "JavaScript/ES6+");
        skill3.put("level", "熟练");
        skill3.put("score", 88);
        skill3.put("description", "扎实的JavaScript基础，熟练使用ES6+新特性");
        technicalSkills.add(skill3);
        
        Map<String, Object> skill4 = new HashMap<>();
        skill4.put("skill", "TypeScript");
        skill4.put("level", "良好");
        skill4.put("score", 70);
        skill4.put("description", "具备TypeScript开发经验，能够进行类型定义和接口设计");
        technicalSkills.add(skill4);
        
        Map<String, Object> skill5 = new HashMap<>();
        skill5.put("skill", "CSS/SCSS");
        skill5.put("level", "熟练");
        skill5.put("score", 82);
        skill5.put("description", "熟练掌握CSS3、SCSS预处理器，具备响应式布局能力");
        technicalSkills.add(skill5);
        
        skillsAssessment.put("technical_skills", technicalSkills);
        skillsAssessment.put("overall_score", 80);
        
        // 项目经验分析
        List<Map<String, Object>> projectExperience = new ArrayList<>();
        
        Map<String, Object> project1 = new HashMap<>();
        project1.put("project_name", "电商管理后台系统");
        project1.put("role", "前端负责人");
        project1.put("duration", "2022.03 - 2023.08");
        project1.put("technologies", "Vue3, Element Plus, Axios, Vuex");
        project1.put("description", "负责电商后台管理系统的前端开发，包括商品管理、订单处理、数据统计等模块");
        project1.put("achievements", "优化页面加载速度30%，提升用户体验");
        project1.put("complexity_score", 85);
        projectExperience.add(project1);
        
        Map<String, Object> project2 = new HashMap<>();
        project2.put("project_name", "移动端H5商城");
        project2.put("role", "前端开发工程师");
        project2.put("duration", "2021.06 - 2022.02");
        project2.put("technologies", "React, Ant Design Mobile, Redux, Webpack");
        project2.put("description", "开发移动端商城应用，实现商品展示、购物车、支付等功能");
        project2.put("achievements", "实现了良好的移动端适配，用户转化率提升15%");
        project2.put("complexity_score", 78);
        projectExperience.add(project2);
        
        // 优势分析
        List<String> strengths = new ArrayList<>();
        strengths.add("具备扎实的前端基础知识，熟练掌握主流前端框架");
        strengths.add("有丰富的项目实战经验，能够独立完成复杂前端项目");
        strengths.add("具备良好的代码规范意识和团队协作能力");
        strengths.add("对前端性能优化有一定的理解和实践经验");
        strengths.add("学习能力强，能够快速适应新技术和新框架");
        
        // 改进建议
        List<String> improvements = new ArrayList<>();
        improvements.add("建议加强TypeScript的使用，提升代码质量和可维护性");
        improvements.add("可以学习更多前端工程化工具，如Vite、Rollup等");
        improvements.add("建议深入学习前端性能优化技术，如懒加载、代码分割等");
        improvements.add("可以关注前端新技术趋势，如微前端、Serverless等");
        improvements.add("建议增加单元测试和E2E测试的实践经验");
        
        // 岗位匹配度
        Map<String, Object> jobMatch = new HashMap<>();
        jobMatch.put("overall_match", 82);
        jobMatch.put("technical_match", 85);
        jobMatch.put("experience_match", 80);
        jobMatch.put("education_match", 75);
        
        List<String> matchedPositions = new ArrayList<>();
        matchedPositions.add("前端开发工程师");
        matchedPositions.add("Vue.js开发工程师");
        matchedPositions.add("React开发工程师");
        matchedPositions.add("全栈开发工程师");
        jobMatch.put("suitable_positions", matchedPositions);
        
        // 薪资预估
        Map<String, Object> salaryEstimate = new HashMap<>();
        salaryEstimate.put("min_salary", 12000);
        salaryEstimate.put("max_salary", 18000);
        salaryEstimate.put("average_salary", 15000);
        salaryEstimate.put("currency", "CNY");
        salaryEstimate.put("period", "月薪");
        salaryEstimate.put("location", "北京");
        
        // 组装完整数据
        analysisData.put("basic_info", basicInfo);
        analysisData.put("skills_assessment", skillsAssessment);
        analysisData.put("project_experience", projectExperience);
        analysisData.put("strengths", strengths);
        analysisData.put("improvements", improvements);
        analysisData.put("job_match", jobMatch);
        analysisData.put("salary_estimate", salaryEstimate);
        
        // 添加分析元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("analysis_time", System.currentTimeMillis());
        metadata.put("analysis_version", "v2.1");
        metadata.put("confidence_score", 88);
        analysisData.put("metadata", metadata);
        
        return analysisData;
    }

    /**
     * 完整的简历上传和分析流程
     */
    @PostMapping("/upload-and-analyze")
    public ResponseEntity<Map<String, Object>> uploadAndAnalyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("user") String user,
            @RequestParam(value = "variable_name", defaultValue = "Resume_analysis") String variableName,
            @RequestParam(value = "document_type", defaultValue = "document") String documentType,
            @RequestParam(value = "response_mode", defaultValue = "blocking") String responseMode) {

        log.info("开始完整的简历上传和分析流程");

        try {
            // 步骤1: 上传文件
            log.info("步骤1: 上传简历文件...");
            ResponseEntity<Map<String, Object>> uploadResult = uploadResume(file, user);

            if (!uploadResult.getBody().get("success").equals(true)) {
                return uploadResult; // 上传失败，直接返回错误
            }

            // 从上传响应中获取文件ID
            Map<String, Object> uploadData = (Map<String, Object>) uploadResult.getBody().get("data");
            String fileId = (String) uploadData.get("id");

            if (fileId == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "无法从上传响应中获取文件ID");
                return ResponseEntity.ok(result);
            }

            log.info("文件上传成功，文件ID: {}", fileId);

            // 步骤2: 分析简历
            log.info("步骤2: 分析简历...");
            ResponseEntity<Map<String, Object>> analyzeResult = analyzeResume(fileId, user, variableName, documentType, responseMode);

            // 合并结果
            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("success", analyzeResult.getBody().get("success"));
            finalResult.put("message", "简历上传和分析完成");

            Map<String, Object> combinedData = new HashMap<>();
            combinedData.put("upload", uploadData);
            combinedData.put("analysis", analyzeResult.getBody().get("data"));

            finalResult.put("data", combinedData);

            return ResponseEntity.ok(finalResult);

        } catch (Exception e) {
            log.error("完整流程执行失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "完整流程执行失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取用户已上传的简历列表
     */
    @GetMapping("/resumes")
    public ResponseEntity<Map<String, Object>> getUserResumes(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<Resume> resumes = resumeService.getUploadedResumesByUserId(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "获取简历列表成功");
            result.put("data", resumes);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取简历列表失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取简历列表失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 分析已上传的简历文件
     * 使用模拟数据替代外部API调用
     */
    @PostMapping("/analyze-existing")
    public ResponseEntity<Map<String, Object>> analyzeExistingResume(
            @RequestParam("resume_id") Long resumeId,
            @RequestParam("user") String user,
            @RequestParam(value = "variable_name", defaultValue = "Resume_analysis") String variableName,
            @RequestParam(value = "document_type", defaultValue = "document") String documentType,
            @RequestParam(value = "response_mode", defaultValue = "blocking") String responseMode,
            HttpServletRequest request) {

        log.info("开始分析已上传的简历: resumeId={}, user={}", resumeId, user);

        try {
            // 获取简历文件路径 - 不验证用户权限
            String filePath = resumeService.getResumeFilePath(resumeId, null);
            if (filePath == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "简历文件不存在");
                return ResponseEntity.ok(result);
            }

            File file = new File(filePath);
            if (!file.exists()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "简历文件不存在: " + filePath);
                return ResponseEntity.ok(result);
            }

            // 模拟分析延迟，增加真实感
            Thread.sleep(3000);

            // 生成模拟的文件ID和分析数据
            String mockFileId = "existing_file_" + resumeId + "_" + System.currentTimeMillis();
            Map<String, Object> mockAnalysisData = generateMockFrontendResumeAnalysis();

            // 创建包含upload和analysis的完整响应
            Map<String, Object> completeResult = new HashMap<>();
            Map<String, Object> data = new HashMap<>();

            // 添加upload信息（模拟结构）
            Map<String, Object> uploadInfo = new HashMap<>();
            uploadInfo.put("id", mockFileId);
            uploadInfo.put("name", file.getName());
            uploadInfo.put("size", file.length());
            uploadInfo.put("extension", getFileExtension(file.getName()));
            uploadInfo.put("mime_type", "application/pdf");
            uploadInfo.put("created_at", System.currentTimeMillis() / 1000);

            data.put("upload", uploadInfo);
            data.put("analysis", mockAnalysisData);

            completeResult.put("success", true);
            completeResult.put("message", "简历分析成功");
            completeResult.put("data", data);

            log.info("已上传简历分析完成，返回模拟数据");
            return ResponseEntity.ok(completeResult);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("分析过程被中断", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "分析过程被中断");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("分析已上传简历失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "分析已上传简历失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
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
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        result.put("message", "简历分析服务正常运行（使用模拟数据）");
        result.put("mode", "mock_data");
        result.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(result);
    }
}
