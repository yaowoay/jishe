package com.aiinterview.controller.video;

import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.video.VideoAnalysisRequest;
import com.aiinterview.model.dto.video.VideoAnalysisResponse;
import com.aiinterview.service.video.VideoAnalysisService;
// 暂时注释掉OpenAPI注解，避免依赖冲突
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/video-analysis")
// 暂时注释掉OpenAPI注解
// @Tag(name = "视频分析管理", description = "面试视频分析相关接口")
public class VideoAnalysisController {
    
    @Autowired
    private VideoAnalysisService videoAnalysisService;
    
    /**
     * 分析面试视频（同步）
     */
    @PostMapping("/analyze")
    // @Operation(summary = "分析面试视频", description = "同步分析上传的面试视频文件")
    public ResponseEntity<ApiResponse<VideoAnalysisResponse>> analyzeVideo(
            // @Parameter(description = "面试ID")
            @RequestParam Long interviewId,
            // @Parameter(description = "视频文件")
            @RequestParam("video") MultipartFile videoFile,
            // @Parameter(description = "转录文本")
            @RequestParam(value = "transcript", required = false) String transcript) {
        
        try {
            log.info("收到视频分析请求，面试ID: {}, 文件名: {}", interviewId, videoFile.getOriginalFilename());
            
            // 验证文件
            if (videoFile.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("视频文件不能为空"));
            }
            
            // 验证文件格式
            String filename = videoFile.getOriginalFilename();
            if (filename == null || !isValidVideoFormat(filename)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("仅支持 MP4, AVI, MOV, MKV 格式的视频文件"));
            }
            
            // 创建分析请求
            VideoAnalysisRequest request = new VideoAnalysisRequest();
            request.setInterviewId(interviewId);
            request.setVideoFile(videoFile);
            request.setTranscript(transcript);
            request.setAsync(false);
            
            // 执行分析
            VideoAnalysisResponse result = videoAnalysisService.analyzeVideo(request);
            
            if ("failed".equals(result.getStatus())) {
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("视频分析失败: " + result.getErrorMessage()));
            }
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            log.error("视频分析接口异常，面试ID: {}", interviewId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("视频分析失败: " + e.getMessage()));
        }
    }
    
    /**
     * 异步分析面试视频
     */
    @PostMapping("/analyze-async")
    // @Operation(summary = "异步分析面试视频", description = "异步分析上传的面试视频文件，返回任务ID")
    public ResponseEntity<ApiResponse<String>> analyzeVideoAsync(
            // @Parameter(description = "面试ID")
            @RequestParam Long interviewId,
            // @Parameter(description = "视频文件")
            @RequestParam("video") MultipartFile videoFile,
            // @Parameter(description = "转录文本")
            @RequestParam(value = "transcript", required = false) String transcript) {
        
        try {
            log.info("收到异步视频分析请求，面试ID: {}, 文件名: {}", interviewId, videoFile.getOriginalFilename());
            
            // 验证文件
            if (videoFile.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("视频文件不能为空"));
            }
            
            // 验证文件格式
            String filename = videoFile.getOriginalFilename();
            if (filename == null || !isValidVideoFormat(filename)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("仅支持 MP4, AVI, MOV, MKV 格式的视频文件"));
            }
            
            // 启动异步分析
            String analysisId = videoAnalysisService.analyzeVideoAsync(interviewId, videoFile, transcript);
            
            return ResponseEntity.ok(ApiResponse.success(analysisId, "分析任务已启动"));
            
        } catch (Exception e) {
            log.error("异步视频分析接口异常，面试ID: {}", interviewId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("启动视频分析失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取分析结果
     */
    @GetMapping("/result/{analysisId}")
    // @Operation(summary = "获取分析结果", description = "根据分析ID获取视频分析结果")
    public ResponseEntity<ApiResponse<VideoAnalysisResponse>> getAnalysisResult(
            // @Parameter(description = "分析ID")
            @PathVariable String analysisId) {
        
        try {
            VideoAnalysisResponse result = videoAnalysisService.getAnalysisResult(analysisId);
            
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            log.error("获取分析结果异常，分析ID: {}", analysisId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取分析结果失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取分析状态
     */
    @GetMapping("/status/{analysisId}")
    // @Operation(summary = "获取分析状态", description = "根据分析ID获取分析任务状态")
    public ResponseEntity<ApiResponse<String>> getAnalysisStatus(
            // @Parameter(description = "分析ID")
            @PathVariable String analysisId) {
        
        try {
            String status = videoAnalysisService.getAnalysisStatus(analysisId);
            return ResponseEntity.ok(ApiResponse.success(status));
            
        } catch (Exception e) {
            log.error("获取分析状态异常，分析ID: {}", analysisId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取分析状态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据面试ID获取分析结果
     */
    @GetMapping("/interview/{interviewId}")
    // @Operation(summary = "根据面试ID获取分析结果", description = "根据面试ID获取对应的视频分析结果")
    public ResponseEntity<ApiResponse<VideoAnalysisResponse>> getAnalysisByInterviewId(
            // @Parameter(description = "面试ID")
            @PathVariable Long interviewId) {

        try {
            log.info("获取面试分析结果，面试ID: {}", interviewId);

            VideoAnalysisResponse result = videoAnalysisService.getAnalysisByInterviewId(interviewId);

            if (result == null) {
                log.info("面试ID {} 暂无分析结果，返回空结果", interviewId);
                return ResponseEntity.ok(ApiResponse.<VideoAnalysisResponse>success("暂无分析结果", null));
            }

            log.info("成功获取面试ID {} 的分析结果", interviewId);
            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            log.error("根据面试ID获取分析结果异常，面试ID: {}", interviewId, e);

            // 如果是数据库字段不存在的错误，返回友好提示
            if (e.getMessage().contains("Unknown column")) {
                log.warn("数据库表结构需要更新，面试ID: {}", interviewId);
                return ResponseEntity.ok(ApiResponse.<VideoAnalysisResponse>success("数据库正在升级中，请稍后重试", null));
            }

            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取分析结果失败: " + e.getMessage()));
        }
    }

    /**
     * 保存非结构化分析数据
     */
    @PostMapping("/save-unstructured")
    public ResponseEntity<ApiResponse<String>> saveUnstructuredData(
            @RequestParam Long interviewId,
            @RequestBody String analysisData) {

        try {
            log.info("保存非结构化分析数据，面试ID: {}", interviewId);

            // 验证JSON格式
            try {
                // 简单验证是否为有效JSON
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.readTree(analysisData);
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("分析数据格式无效，请提供有效的JSON数据"));
            }

            // 保存数据
            String analysisId = videoAnalysisService.saveUnstructuredAnalysis(interviewId, analysisData);

            return ResponseEntity.ok(ApiResponse.success(analysisId, "非结构化数据保存成功"));

        } catch (Exception e) {
            log.error("保存非结构化分析数据失败，面试ID: {}", interviewId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("保存失败: " + e.getMessage()));
        }
    }
    
    /**
     * 验证视频文件格式
     */
    private boolean isValidVideoFormat(String filename) {
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return "mp4".equals(extension) || "avi".equals(extension) || 
               "mov".equals(extension) || "mkv".equals(extension);
    }
}
