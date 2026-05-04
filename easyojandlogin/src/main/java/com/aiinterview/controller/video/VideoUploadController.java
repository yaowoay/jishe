package com.aiinterview.controller.video;

import com.aiinterview.model.dto.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 视频上传Controller
 */
@Slf4j
@RestController
@RequestMapping("/video")
public class VideoUploadController {

    // 视频保存路径
    private static final String VIDEO_UPLOAD_DIR = "E:/Projects/hautproject/2026/li/easyojandlogin/src/main/resources/videos/";
    
    /**
     * 上传面试视频
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<VideoUploadResponse>> uploadVideo(
            @RequestParam("videos") MultipartFile videoFile,
            @RequestParam("interviewId") Long interviewId) {

        try {
            log.info("接收到视频上传请求，面试ID: {}, 文件大小: {} MB",
                interviewId, videoFile.getSize() / 1024.0 / 1024.0);
            log.info("文件名: {}, 内容类型: {}", videoFile.getOriginalFilename(), videoFile.getContentType());
            
            // 验证文件
            if (videoFile.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("视频文件不能为空"));
            }
            
            // 检查文件类型
            String contentType = videoFile.getContentType();
            if (contentType == null || !contentType.startsWith("videos/")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("只能上传视频文件"));
            }
            
            // 检查文件大小（限制为100MB）
            long maxSize = 100 * 1024 * 1024; // 100MB
            if (videoFile.getSize() > maxSize) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("视频文件大小不能超过100MB"));
            }
            
            // 创建上传目录
            File uploadDir = new File(VIDEO_UPLOAD_DIR);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    log.error("创建视频上传目录失败: {}", VIDEO_UPLOAD_DIR);
                    return ResponseEntity.internalServerError()
                        .body(ApiResponse.error("创建上传目录失败"));
                }
                log.info("创建视频上传目录: {}", VIDEO_UPLOAD_DIR);
            }
            
            // 生成文件名
            String originalFilename = videoFile.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("interview_%d_%s%s", interviewId, timestamp, fileExtension);
            
            // 保存文件
            Path filePath = Paths.get(VIDEO_UPLOAD_DIR, fileName);
            Files.copy(videoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("视频文件保存成功: {}", filePath.toString());
            
            // 构建响应
            VideoUploadResponse response = new VideoUploadResponse();
            response.setFileName(fileName);
            response.setVideoPath(filePath.toString());
            response.setFileSize(videoFile.getSize());
            response.setUploadTime(LocalDateTime.now().toString());
            
            return ResponseEntity.ok(ApiResponse.success("视频上传成功", response));
            
        } catch (IOException e) {
            log.error("保存视频文件失败，面试ID: {}", interviewId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("保存视频文件失败: " + e.getMessage()));
        } catch (Exception e) {
            log.error("视频上传处理失败，面试ID: {}", interviewId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("视频上传失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取视频文件信息
     */
    @GetMapping("/info/{interviewId}")
    public ResponseEntity<ApiResponse<VideoUploadResponse>> getVideoInfo(@PathVariable Long interviewId) {
        try {
            log.info("查询视频信息，面试ID: {}", interviewId);
            
            // 查找对应的视频文件
            File uploadDir = new File(VIDEO_UPLOAD_DIR);
            if (!uploadDir.exists()) {
                return ResponseEntity.ok(ApiResponse.<VideoUploadResponse>success("未找到视频文件", null));
            }
            
            File[] files = uploadDir.listFiles((dir, name) -> 
                name.startsWith("interview_" + interviewId + "_") && 
                (name.endsWith(".webm") || name.endsWith(".mp4") || name.endsWith(".avi")));
            
            if (files == null || files.length == 0) {
                return ResponseEntity.ok(ApiResponse.<VideoUploadResponse>success("未找到视频文件", null));
            }
            
            // 返回最新的文件
            File latestFile = files[0];
            for (File file : files) {
                if (file.lastModified() > latestFile.lastModified()) {
                    latestFile = file;
                }
            }
            
            VideoUploadResponse response = new VideoUploadResponse();
            response.setFileName(latestFile.getName());
            response.setVideoPath(latestFile.getAbsolutePath());
            response.setFileSize(latestFile.length());
            response.setUploadTime(LocalDateTime.ofEpochSecond(
                latestFile.lastModified() / 1000, 0, 
                java.time.ZoneOffset.systemDefault().getRules().getOffset(java.time.Instant.now())
            ).toString());
            
            return ResponseEntity.ok(ApiResponse.success("查询成功", response));
            
        } catch (Exception e) {
            log.error("查询视频信息失败，面试ID: {}", interviewId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查询视频信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 视频上传响应DTO
     */
    public static class VideoUploadResponse {
        private String fileName;
        private String videoPath;
        private Long fileSize;
        private String uploadTime;
        
        // Getters and Setters
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getVideoPath() { return videoPath; }
        public void setVideoPath(String videoPath) { this.videoPath = videoPath; }
        
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        
        public String getUploadTime() { return uploadTime; }
        public void setUploadTime(String uploadTime) { this.uploadTime = uploadTime; }
    }
}
