package com.aiinterview.model.dto.resume;

import lombok.Data;

/**
 * 简历上传响应DTO
 */
@Data
public class ResumeUploadResponse {
    
    private Boolean success;
    private String message;
    private String fileUrl;
    private Object resumeData; // 解析后的简历数据
    
    public static ResumeUploadResponse success(String fileUrl, Object resumeData) {
        ResumeUploadResponse response = new ResumeUploadResponse();
        response.setSuccess(true);
        response.setMessage("文件上传成功");
        response.setFileUrl(fileUrl);
        response.setResumeData(resumeData);
        return response;
    }
    
    public static ResumeUploadResponse error(String message) {
        ResumeUploadResponse response = new ResumeUploadResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
