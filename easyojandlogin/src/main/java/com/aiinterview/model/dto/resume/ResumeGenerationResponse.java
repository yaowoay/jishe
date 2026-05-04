package com.aiinterview.model.dto.resume;

import lombok.Data;

/**
 * 简历生成响应DTO
 */
@Data
public class ResumeGenerationResponse {
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 生成的简历内容
     */
    private String resumeContent;
    
    /**
     * 简历格式 (html, markdown, text)
     */
    private String format;
    
    /**
     * 生成时间戳
     */
    private Long timestamp;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    public static ResumeGenerationResponse success(String resumeContent, String format) {
        ResumeGenerationResponse response = new ResumeGenerationResponse();
        response.setSuccess(true);
        response.setMessage("简历生成成功");
        response.setResumeContent(resumeContent);
        response.setFormat(format);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
    
    public static ResumeGenerationResponse error(String message) {
        ResumeGenerationResponse response = new ResumeGenerationResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
