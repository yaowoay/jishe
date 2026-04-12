package com.aiinterview.service.video;

import com.aiinterview.model.dto.video.VideoAnalysisRequest;
import com.aiinterview.model.dto.video.VideoAnalysisResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频分析服务接口
 */
public interface VideoAnalysisService {
    
    /**
     * 分析面试视频
     * @param request 分析请求
     * @return 分析结果
     */
    VideoAnalysisResponse analyzeVideo(VideoAnalysisRequest request);
    
    /**
     * 异步分析面试视频
     * @param interviewId 面试ID
     * @param videoFile 视频文件
     * @param transcript 转录文本
     * @return 分析任务ID
     */
    String analyzeVideoAsync(Long interviewId, MultipartFile videoFile, String transcript);
    
    /**
     * 获取分析结果
     * @param analysisId 分析ID
     * @return 分析结果
     */
    VideoAnalysisResponse getAnalysisResult(String analysisId);
    
    /**
     * 获取分析状态
     * @param analysisId 分析ID
     * @return 分析状态
     */
    String getAnalysisStatus(String analysisId);
    
    /**
     * 保存分析结果到数据库
     * @param interviewId 面试ID
     * @param analysisResult 分析结果
     */
    void saveAnalysisResult(Long interviewId, VideoAnalysisResponse analysisResult);
    
    /**
     * 根据面试ID获取分析结果
     * @param interviewId 面试ID
     * @return 分析结果
     */
    VideoAnalysisResponse getAnalysisByInterviewId(Long interviewId);

    String saveUnstructuredAnalysis(Long interviewId, String analysisData);
}
