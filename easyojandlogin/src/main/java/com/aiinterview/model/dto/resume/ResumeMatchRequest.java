package com.aiinterview.model.dto.resume;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 简历匹配分析请求DTO
 */
@Data
public class ResumeMatchRequest implements Serializable {
    
    /**
     * 简历文件
     */
    private MultipartFile updownCV;
    
    /**
     * 求职岗位
     */
    private String jobPosition;
    
    /**
     * 职位描述/要求
     */
    private String jobDescription;
}
