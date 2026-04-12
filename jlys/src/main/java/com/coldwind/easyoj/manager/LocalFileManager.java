package com.coldwind.easyoj.manager;

import com.coldwind.easyoj.common.ErrorCode;
import com.coldwind.easyoj.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalFileManager {

    @Value("${local.storage.base-path}")
    private String basePath;  // 本地存储根路径（如D:/easy-oj-uploads/）

    @Value("${local.storage.access-prefix}")
    private String accessPrefix;  // 访问前缀（如/api/file/download/）

    /**
     * 上传文件到本地存储
     * @param relativePath 相对存储路径（如/user/avatar/123）
     * @param file 上传的文件
     * @return 文件访问URL
     */
    public String upload(String relativePath, MultipartFile file) {
        // 1. 构造完整存储路径
        File targetDir = new File(Paths.get(basePath, relativePath).toString());
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建存储目录失败");
        }

        // 2. 生成唯一文件名（避免覆盖）
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + ext;
        File targetFile = new File(targetDir, newFileName);

        // 3. 保存文件到本地
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件保存失败", e);
        }

        // 4. 返回访问URL（格式：accessPrefix + relativePath + newFileName）
        return accessPrefix + relativePath + "/" + newFileName;
    }
}