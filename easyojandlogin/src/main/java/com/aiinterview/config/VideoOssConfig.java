package com.aiinterview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "video.analysis.oss")
public class VideoOssConfig {
    private String tempDownloadPath;  // 临时下载目录
    private Long tempFileTtl;         // 临时文件保留时间
}