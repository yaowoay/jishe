package com.aiinterview.service;

import com.aiinterview.config.AliOssConfig;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云OSS服务类
 * 提供视频文件的上传和临时访问链接生成功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliOssService {

    private final AliOssConfig aliOssConfig;

    /**
     * 上传视频文件到OSS
     *
     * @param file        视频文件
     * @param interviewId 面试ID（用于生成文件名）
     * @return OSS文件Key（用于后续访问）
     */
    public String uploadVideo(MultipartFile file, Long interviewId) {
        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成唯一的文件名：videos/interview_{面试ID}_{UUID}.mp4
        String objectName = String.format("videos/interview_%d_%s%s",
                interviewId, UUID.randomUUID().toString().replace("-", ""), fileExtension);

        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(
                    aliOssConfig.getEndpoint(),
                    aliOssConfig.getAccessKeyId(),
                    aliOssConfig.getAccessKeySecret()
            );

            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        aliOssConfig.getBucketName(),
                        objectName,
                        inputStream
                );
                ossClient.putObject(putObjectRequest);
                log.info("视频上传OSS成功: {}, 面试ID: {}, 文件大小: {} MB",
                        objectName, interviewId, file.getSize() / 1024.0 / 1024.0);
                return objectName;
            }
        } catch (Exception e) {
            log.error("视频上传OSS失败, 面试ID: {}", interviewId, e);
            throw new RuntimeException("视频上传到OSS失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从OSS下载视频文件到本地临时目录
     *
     * @param ossFileKey OSS文件Key
     * @param tempDir    临时目录路径
     * @return 本地临时文件路径
     */
    public String downloadVideoToTemp(String ossFileKey, String tempDir) {
        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(
                    aliOssConfig.getEndpoint(),
                    aliOssConfig.getAccessKeyId(),
                    aliOssConfig.getAccessKeySecret()
            );

            // 生成临时目录
            java.nio.file.Path tempDirPath = java.nio.file.Paths.get(tempDir);
            if (!java.nio.file.Files.exists(tempDirPath)) {
                java.nio.file.Files.createDirectories(tempDirPath);
            }

            // 生成本地临时文件名
            String fileName = ossFileKey.substring(ossFileKey.lastIndexOf("/") + 1);
            java.nio.file.Path tempFile = tempDirPath.resolve(
                    System.currentTimeMillis() + "_" + fileName
            );

            // 下载文件
            ossClient.getObject(
                    new com.aliyun.oss.model.GetObjectRequest(aliOssConfig.getBucketName(), ossFileKey),
                    tempFile.toFile()
            );

            log.info("从OSS下载视频成功: {} -> {}", ossFileKey, tempFile.toString());
            return tempFile.toString();

        } catch (Exception e) {
            log.error("从OSS下载视频失败: {}", ossFileKey, e);
            throw new RuntimeException("从OSS下载视频失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 生成临时访问链接（默认1小时有效）
     *
     * @param objectName OSS文件Key
     * @return 临时访问URL
     */
    public String generatePresignedUrl(String objectName) {
        return generatePresignedUrl(objectName, 1);
    }

    /**
     * 生成临时访问链接
     *
     * @param objectName  OSS文件Key
     * @param expireHours 有效期（小时）
     * @return 临时访问URL
     */
    public String generatePresignedUrl(String objectName, int expireHours) {
        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(
                    aliOssConfig.getEndpoint(),
                    aliOssConfig.getAccessKeyId(),
                    aliOssConfig.getAccessKeySecret()
            );

            // 设置URL过期时间
            Date expiration = Date.from(
                    LocalDateTime.now().plusHours(expireHours)
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
            );

            // 生成临时URL
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    aliOssConfig.getBucketName(),
                    objectName
            );
            request.setExpiration(expiration);
            URL url = ossClient.generatePresignedUrl(request);

            log.debug("生成临时访问链接: {}, 有效期: {}小时", objectName, expireHours);
            return url.toString();

        } catch (Exception e) {
            log.error("生成临时链接失败: {}", objectName, e);
            throw new RuntimeException("生成视频链接失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 删除OSS中的文件
     *
     * @param objectName OSS文件Key
     * @return 是否删除成功
     */
    public boolean deleteVideo(String objectName) {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(
                    aliOssConfig.getEndpoint(),
                    aliOssConfig.getAccessKeyId(),
                    aliOssConfig.getAccessKeySecret()
            );

            ossClient.deleteObject(aliOssConfig.getBucketName(), objectName);
            log.info("删除OSS视频成功: {}", objectName);
            return true;

        } catch (Exception e) {
            log.error("删除OSS视频失败: {}", objectName, e);
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param objectName OSS文件Key
     * @return 是否存在
     */
    public boolean doesObjectExist(String objectName) {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(
                    aliOssConfig.getEndpoint(),
                    aliOssConfig.getAccessKeyId(),
                    aliOssConfig.getAccessKeySecret()
            );

            return ossClient.doesObjectExist(aliOssConfig.getBucketName(), objectName);

        } catch (Exception e) {
            log.error("检查OSS文件是否存在失败: {}", objectName, e);
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}