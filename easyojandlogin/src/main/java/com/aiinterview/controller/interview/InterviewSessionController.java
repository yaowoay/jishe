package com.aiinterview.controller.interview;

import com.aiinterview.model.entity.interview.InterviewSession;
import com.aiinterview.service.InterviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 面试会话控制器
 */
@Slf4j
@RestController
@RequestMapping("/interview/session")
public class InterviewSessionController {

    @Autowired
    private InterviewService interviewService;

    /**
     * 创建面试会话
     * 上传简历并创建新的面试会话
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createSession(
            @RequestPart("resume") MultipartFile resume,
            @RequestParam("jobPosition") String jobPosition,
            @RequestParam(value = "style", defaultValue = "专业严谨") String style) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("创建面试会话: jobPosition={}, style={}", jobPosition, style);
            
            if (resume.isEmpty()) {
                response.put("success", false);
                response.put("message", "简历文件不能为空");
                return ResponseEntity.ok(response);
            }
            
            // 生成会话ID
            String sessionId = "session_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
            
            // 保存简历文件
            String uploadDir = "D:\\MyFiles\\ResumeUploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String fileName = sessionId + "_" + resume.getOriginalFilename();
            String filePath = uploadDir + File.separator + fileName;
            File file = new File(filePath);
            resume.transferTo(file);
            
            log.info("简历文件保存成功: {}", filePath);
            
            // 创建面试会话
            InterviewSession session = new InterviewSession();
            session.setSessionId(sessionId);
            session.setUserId(1L); // 临时使用固定用户ID，实际应从token获取
            session.setResumePath(filePath);
            session.setJobPosition(jobPosition);
            session.setStyle(style);
            session.setStatus("ACTIVE");
            session.setCreatedAt(java.time.LocalDateTime.now());
            session.setUpdatedAt(java.time.LocalDateTime.now());

            // 直接保存到数据库（如果有 repository）
            // 或者返回给前端

            response.put("success", true);
            response.put("message", "面试会话创建成功");
            response.put("sessionId", sessionId);
            response.put("data", session);
            
            log.info("面试会话创建成功: sessionId={}", sessionId);
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("创建面试会话失败", e);
            response.put("success", false);
            response.put("message", "创建面试会话失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
