// ResumeServiceImpl.java (实现)
package org.iflyproject.springdemos.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.iflyproject.springdemos.service.ResumeService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String uploadResume(MultipartFile file) throws Exception {
        // 确保上传目录存在
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 保存文件
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());

        return "简历上传成功: " + filename;
    }
}