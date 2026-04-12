package com.aiinterview.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.Data;

import java.io.File;

/**
 * 文件上传配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig implements WebMvcConfigurer {
    
    private String path = "./uploads/";
    private String maxSize = "50MB";
    private String allowedTypes = "document";
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // 配置静态资源访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + new File(path).getAbsolutePath() + "/");
    }
    
    /**
     * 检查文件类型是否允许
     */
    public boolean isAllowedType(String fileExtension) {
        if (fileExtension == null) {
            return false;
        }
        String[] types = allowedTypes.split(",");
        for (String type : types) {
            if (type.trim().equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取完整的文件路径
     */
    public String getFullPath(String fileName) {
        return path + fileName;
    }
}
