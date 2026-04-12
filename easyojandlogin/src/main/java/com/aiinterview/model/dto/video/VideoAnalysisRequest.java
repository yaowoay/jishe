package com.aiinterview.model.dto.video;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频分析请求DTO
 */
@Data
public class VideoAnalysisRequest {
    
    /**
     * 面试ID
     */
    private Long interviewId;
    
    /**
     * 视频文件
     */
    private MultipartFile videoFile;
    
    /**
     * 转录文本（可选）
     */
    private String transcript;
    
    /**
     * 分析类型（可选，默认为完整分析）
     */
    private String analysisType = "full";
    
    /**
     * 是否异步处理（默认为true）
     */
    private Boolean async = true;
}
