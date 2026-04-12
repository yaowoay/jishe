package com.aiinterview.model.dto.resume;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 简历上传请求DTO
 */
@Data
public class ResumeUploadRequest {
    
    @NotNull(message = "文件不能为空")
    private MultipartFile file;
    
    @NotBlank(message = "存储名称不能为空")
    private String storageName;
}
