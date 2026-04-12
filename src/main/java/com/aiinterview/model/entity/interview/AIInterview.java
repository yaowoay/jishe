package com.aiinterview.model.entity.interview;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI面试实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_interviews")
public class AIInterview {
    
    @TableId(value = "interview_id", type = IdType.AUTO)
    private Long interviewId;
    
    @TableField(value = "application_id", insertStrategy = FieldStrategy.IGNORED)
    private Long applicationId;
    
    @TableField("start_time")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    private LocalDateTime endTime;
    
    @TableField("history")
    private String history;
    
    @TableField("evaluation")
    private String evaluation; // JSON格式的评估数据

    @TableField("overall_score")
    private Double overallScore;

    // 临时注释掉数据库中不存在的字段，避免SQL错误
    // @TableField("interview_video")
    // private String interviewVideo; // 面试视频文件路径

    // @TableField("video_analysis")
    // private String videoAnalysis; // 视频分析结果JSON

    // @TableField("audio_analysis")
    // private String audioAnalysis; // 音频分析结果JSON

    // @TableField("expression_score")
    // private Double expressionScore; // 表情评分

    // @TableField("audio_emotion_score")
    // private Double audioEmotionScore; // 音频情感评分

    // @TableField("background_score")
    // private Double backgroundScore; // 背景环境评分

    // 临时添加getter/setter方法以保持兼容性
    public String getVideoAnalysis() { return null; }
    public void setVideoAnalysis(String videoAnalysis) { /* 暂时不保存 */ }

    public String getAudioAnalysis() { return null; }
    public void setAudioAnalysis(String audioAnalysis) { /* 暂时不保存 */ }

    public Double getExpressionScore() { return 0.0; }
    public void setExpressionScore(Double expressionScore) { /* 暂时不保存 */ }

    public Double getAudioEmotionScore() { return 0.0; }
    public void setAudioEmotionScore(Double audioEmotionScore) { /* 暂时不保存 */ }

    public Double getBackgroundScore() { return 0.0; }
    public void setBackgroundScore(Double backgroundScore) { /* 暂时不保存 */ }

    public String getInterviewVideo() { return null; }
    public void setInterviewVideo(String interviewVideo) { /* 暂时不保存 */ }

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
