package com.aiinterview.service.impl.video;

import com.aiinterview.model.dto.video.VideoAnalysisRequest;
import com.aiinterview.model.dto.video.VideoAnalysisResponse;
import com.aiinterview.model.entity.interview.AIInterview;
import com.aiinterview.mapper.AIInterviewMapper;
import com.aiinterview.service.video.VideoAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 视频分析服务实现类
 */
@Slf4j
@Service
public class VideoAnalysisServiceImpl implements VideoAnalysisService {
    
    @Autowired
    private AIInterviewMapper aiInterviewMapper;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${video.analysis.python.service.url:http://localhost:5000}")
    private String pythonServiceUrl;
    
    @Value("${video.upload.path:/uploads/videos/}")
    private String videoUploadPath;
    
    // 存储异步分析任务的状态
    private final Map<String, VideoAnalysisResponse> analysisResults = new ConcurrentHashMap<>();
    private final Map<String, String> analysisStatus = new ConcurrentHashMap<>();
    
    @Override
    public VideoAnalysisResponse analyzeVideo(VideoAnalysisRequest request) {
        try {
            log.info("开始分析视频，面试ID: {}", request.getInterviewId());
            
            // 保存视频文件
            String videoPath = saveVideoFile(request.getVideoFile());
            
            // 调用Python分析服务
            VideoAnalysisResponse result = callPythonAnalysisService(videoPath, request.getTranscript());
            result.setInterviewId(request.getInterviewId());
            result.setGeneratedAt(LocalDateTime.now());
            
            // 保存分析结果到数据库
            saveAnalysisResult(request.getInterviewId(), result);
            
            log.info("视频分析完成，面试ID: {}", request.getInterviewId());
            return result;
            
        } catch (Exception e) {
            log.error("视频分析失败，面试ID: {}", request.getInterviewId(), e);
            VideoAnalysisResponse errorResponse = new VideoAnalysisResponse();
            errorResponse.setInterviewId(request.getInterviewId());
            errorResponse.setStatus("failed");
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setGeneratedAt(LocalDateTime.now());
            return errorResponse;
        }
    }
    
    @Override
    public String analyzeVideoAsync(Long interviewId, MultipartFile videoFile, String transcript) {
        String analysisId = UUID.randomUUID().toString();
        
        // 设置初始状态
        analysisStatus.put(analysisId, "processing");
        
        // 异步执行分析
        CompletableFuture.runAsync(() -> {
            try {
                VideoAnalysisRequest request = new VideoAnalysisRequest();
                request.setInterviewId(interviewId);
                request.setVideoFile(videoFile);
                request.setTranscript(transcript);
                
                VideoAnalysisResponse result = analyzeVideo(request);
                result.setAnalysisId(analysisId);
                
                analysisResults.put(analysisId, result);
                analysisStatus.put(analysisId, "completed");
                
            } catch (Exception e) {
                log.error("异步视频分析失败，分析ID: {}", analysisId, e);
                analysisStatus.put(analysisId, "failed");
                
                VideoAnalysisResponse errorResponse = new VideoAnalysisResponse();
                errorResponse.setAnalysisId(analysisId);
                errorResponse.setInterviewId(interviewId);
                errorResponse.setStatus("failed");
                errorResponse.setErrorMessage(e.getMessage());
                errorResponse.setGeneratedAt(LocalDateTime.now());
                
                analysisResults.put(analysisId, errorResponse);
            }
        });
        
        return analysisId;
    }
    
    @Override
    public VideoAnalysisResponse getAnalysisResult(String analysisId) {
        return analysisResults.get(analysisId);
    }
    
    @Override
    public String getAnalysisStatus(String analysisId) {
        return analysisStatus.getOrDefault(analysisId, "not_found");
    }
    
    /**
     * 保存视频文件
     */
    private String saveVideoFile(MultipartFile videoFile) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();
        String filePath = videoUploadPath + fileName;
        
        File directory = new File(videoUploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        File file = new File(filePath);
        videoFile.transferTo(file);
        
        return file.getAbsolutePath();
    }
    
    /**
     * 调用Python分析服务
     */
    private VideoAnalysisResponse callPythonAnalysisService(String videoPath, String transcript) {
        try {
            // 准备请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("video", new org.springframework.core.io.FileSystemResource(videoPath));
            if (transcript != null && !transcript.trim().isEmpty()) {
                body.add("transcript", transcript);
            }
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // 调用Python服务
            String url = pythonServiceUrl + "/api/analyze";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Boolean success = (Boolean) responseBody.get("success");
                
                if (success) {
                    // 解析分析结果
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    return parseAnalysisResult(data);
                } else {
                    throw new RuntimeException("Python服务分析失败: " + responseBody.get("error"));
                }
            } else {
                throw new RuntimeException("调用Python服务失败，状态码: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("调用Python分析服务失败", e);
            throw new RuntimeException("调用Python分析服务失败: " + e.getMessage());
        }
    }
    
    /**
     * 解析Python服务返回的分析结果
     */
    private VideoAnalysisResponse parseAnalysisResult(Map<String, Object> data) {
        try {
            VideoAnalysisResponse response = new VideoAnalysisResponse();
            response.setStatus("completed");
            
            // 解析评分
            Map<String, Object> scores = (Map<String, Object>) data.get("scores");
            if (scores != null) {
                VideoAnalysisResponse.ScoreDetails scoreDetails = new VideoAnalysisResponse.ScoreDetails();
                scoreDetails.setExpression(getDoubleValue(scores.get("expression")));
                scoreDetails.setState(getDoubleValue(scores.get("state")));
                scoreDetails.setBackground(getDoubleValue(scores.get("background")));
                scoreDetails.setAudio(getDoubleValue(scores.get("audio")));
                scoreDetails.setOverall(getDoubleValue(scores.get("overall")));
                response.setScores(scoreDetails);
            }
            
            // 解析音频分析
            Map<String, Object> audioAnalysis = (Map<String, Object>) data.get("audio_analysis");
            if (audioAnalysis != null) {
                VideoAnalysisResponse.AudioAnalysis audio = new VideoAnalysisResponse.AudioAnalysis();
                audio.setSummary((String) audioAnalysis.get("summary"));
                response.setAudioAnalysis(audio);
            }
            
            // 设置AI反馈和转录文本
            response.setAiFeedback((String) data.get("ai_feedback"));
            response.setTranscript((String) data.get("transcript"));
            
            return response;
            
        } catch (Exception e) {
            log.error("解析分析结果失败", e);
            throw new RuntimeException("解析分析结果失败: " + e.getMessage());
        }
    }
    
    private Double getDoubleValue(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public void saveAnalysisResult(Long interviewId, VideoAnalysisResponse analysisResult) {
        try {
            AIInterview interview = aiInterviewMapper.selectById(interviewId);
            if (interview == null) {
                throw new RuntimeException("面试记录不存在，ID: " + interviewId);
            }

            // 更新面试记录
            interview.setVideoAnalysis(objectMapper.writeValueAsString(analysisResult.getVideoAnalysis()));
            interview.setAudioAnalysis(objectMapper.writeValueAsString(analysisResult.getAudioAnalysis()));

            if (analysisResult.getScores() != null) {
                interview.setExpressionScore(analysisResult.getScores().getExpression());
                interview.setAudioEmotionScore(analysisResult.getScores().getAudio());
                interview.setBackgroundScore(analysisResult.getScores().getBackground());
                interview.setOverallScore(analysisResult.getScores().getOverall());
            }

            // 更新评估信息
            Map<String, Object> evaluation = new HashMap<>();
            evaluation.put("ai_feedback", analysisResult.getAiFeedback());
            evaluation.put("transcript", analysisResult.getTranscript());
            evaluation.put("analysis_time", analysisResult.getGeneratedAt());
            interview.setEvaluation(objectMapper.writeValueAsString(evaluation));

            aiInterviewMapper.updateById(interview);
            log.info("保存分析结果成功，面试ID: {}", interviewId);

        } catch (Exception e) {
            log.error("保存分析结果失败，面试ID: {}", interviewId, e);
            throw new RuntimeException("保存分析结果失败: " + e.getMessage());
        }
    }

    @Override
    public VideoAnalysisResponse getAnalysisByInterviewId(Long interviewId) {
        try {
            AIInterview interview = aiInterviewMapper.selectById(interviewId);
            if (interview == null) {
                return null;
            }

            VideoAnalysisResponse response = new VideoAnalysisResponse();
            response.setInterviewId(interviewId);
            response.setStatus("completed");

            // 解析评分
            VideoAnalysisResponse.ScoreDetails scores = new VideoAnalysisResponse.ScoreDetails();
            scores.setExpression(interview.getExpressionScore());
            scores.setAudio(interview.getAudioEmotionScore());
            scores.setBackground(interview.getBackgroundScore());
            scores.setOverall(interview.getOverallScore());
            response.setScores(scores);

            // 解析分析结果
            if (interview.getVideoAnalysis() != null) {
                VideoAnalysisResponse.VideoFrameAnalysis videoAnalysis =
                    objectMapper.readValue(interview.getVideoAnalysis(), VideoAnalysisResponse.VideoFrameAnalysis.class);
                response.setVideoAnalysis(videoAnalysis);
            }

            if (interview.getAudioAnalysis() != null) {
                VideoAnalysisResponse.AudioAnalysis audioAnalysis =
                    objectMapper.readValue(interview.getAudioAnalysis(), VideoAnalysisResponse.AudioAnalysis.class);
                response.setAudioAnalysis(audioAnalysis);
            }

            // 解析评估信息
            if (interview.getEvaluation() != null) {
                Map<String, Object> evaluation = objectMapper.readValue(interview.getEvaluation(), Map.class);
                response.setAiFeedback((String) evaluation.get("ai_feedback"));
                response.setTranscript((String) evaluation.get("transcript"));
            }

            response.setGeneratedAt(interview.getUpdatedAt());

            return response;

        } catch (Exception e) {
            log.error("获取分析结果失败，面试ID: {}", interviewId, e);

            // 如果是数据库字段不存在的错误，返回null让前端使用模拟数据
            if (e.getMessage() != null && e.getMessage().contains("Unknown column")) {
                log.warn("数据库表结构需要更新，面试ID: {}，返回null使用模拟数据", interviewId);
                return null;
            }

            return null;
        }
    }

    @Override
    public String saveUnstructuredAnalysis(Long interviewId, String analysisData) {
        try {
            log.info("保存非结构化分析数据，面试ID: {}", interviewId);

            // 生成分析ID
            String analysisId = "ANALYSIS_" + System.currentTimeMillis() + "_" + interviewId;

            // 这里可以选择保存到数据库或文件系统
            // 由于当前数据库表结构问题，暂时记录日志
            log.info("非结构化分析数据已接收，分析ID: {}, 数据长度: {}", analysisId, analysisData.length());

            // 可以在这里添加实际的保存逻辑
            // 例如：保存到Redis、文件系统或专门的分析结果表

            return analysisId;

        } catch (Exception e) {
            log.error("保存非结构化分析数据失败，面试ID: {}", interviewId, e);
            throw new RuntimeException("保存失败: " + e.getMessage());
        }
    }
}
