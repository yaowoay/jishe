package com.coldwind.easyoj.controller;
//已完成测试

import com.coldwind.easyoj.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam(name="file") MultipartFile file) {
        try {
            // 调用服务层处理简历上传
            return ResponseEntity.ok(resumeService.uploadResume(file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("简历上传失败: " + e.getMessage());
        }
    }
}