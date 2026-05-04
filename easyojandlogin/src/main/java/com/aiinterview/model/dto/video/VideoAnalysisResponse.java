package com.aiinterview.model.dto.video;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 视频分析响应DTO
 */
@Data
public class VideoAnalysisResponse {
    
    /**
     * 分析ID
     */
    private String analysisId;
    
    /**
     * 面试ID
     */
    private Long interviewId;
    
    /**
     * 分析状态
     */
    private String status; // processing, completed, failed
    
    /**
     * 综合评分
     */
    private ScoreDetails scores;
    
    /**
     * 音频分析结果
     */
    private AudioAnalysis audioAnalysis;
    
    /**
     * 视频帧分析结果
     */
    private VideoFrameAnalysis videoAnalysis;
    
    /**
     * AI反馈
     */
    private String aiFeedback;
    
    /**
     * 转录文本
     */
    private String transcript;
    
    /**
     * 生成时间
     */
    private LocalDateTime generatedAt;
    
    /**
     * 错误信息（如果有）
     */
    private String errorMessage;
    
    @Data
    public static class ScoreDetails {
        private Double expression;      // 表情评分
        private Double state;          // 状态评分
        private Double background;     // 背景评分
        private Double audio;          // 音频情感评分
        private Double overall;        // 综合评分
    }
    
    @Data
    public static class AudioAnalysis {
        private List<SegmentAnalysis> segmentAnalyses;
        private String summary;
        
        @Data
        public static class SegmentAnalysis {
            private Integer segmentId;
            private Double startTime;
            private Double endTime;
            private String analysis;
        }
    }
    
    @Data
    public static class VideoFrameAnalysis {
        private String multiFrameSummary;
        private List<FrameAnalysis> frameAnalyses;
        
        @Data
        public static class FrameAnalysis {
            private Integer frameIndex;
            private Integer timestamp;
            private String analysis;
        }
    }
}
