-- 添加视频分析相关字段到ai_interviews表
-- 执行时间: 2025-08-13

-- 检查表是否存在，如果不存在则创建
CREATE TABLE IF NOT EXISTS ai_interviews (
    interview_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT,
    start_time DATETIME,
    end_time DATETIME,
    history TEXT,
    evaluation TEXT,
    overall_score DOUBLE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 添加视频分析相关字段（如果不存在）
ALTER TABLE ai_interviews 
ADD COLUMN IF NOT EXISTS interview_video VARCHAR(500) COMMENT '面试视频文件路径';

ALTER TABLE ai_interviews 
ADD COLUMN IF NOT EXISTS video_analysis TEXT COMMENT '视频分析结果JSON';

ALTER TABLE ai_interviews 
ADD COLUMN IF NOT EXISTS audio_analysis TEXT COMMENT '音频分析结果JSON';

ALTER TABLE ai_interviews 
ADD COLUMN IF NOT EXISTS expression_score DOUBLE COMMENT '表情评分';

ALTER TABLE ai_interviews 
ADD COLUMN IF NOT EXISTS audio_emotion_score DOUBLE COMMENT '音频情感评分';

ALTER TABLE ai_interviews 
ADD COLUMN IF NOT EXISTS background_score DOUBLE COMMENT '背景环境评分';

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_ai_interviews_application_id ON ai_interviews(application_id);
CREATE INDEX IF NOT EXISTS idx_ai_interviews_start_time ON ai_interviews(start_time);
CREATE INDEX IF NOT EXISTS idx_ai_interviews_created_at ON ai_interviews(created_at);
