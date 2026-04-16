package com.aiinterview.service.dify;

import com.aiinterview.service.dify.vo.FileInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

/**
 * Dify 文件上传服务
 */
@Service
public class DifyFileUploadService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://api.dify.ai/v1";
    private static final String UPLOAD_URI = "/files/upload";

    public DifyFileUploadService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
        // 设置超时时间
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory())
                .setConnectTimeout((int) Duration.ofSeconds(30).toMillis());
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory())
                .setReadTimeout((int) Duration.ofSeconds(60).toMillis());
    }

    /**
     * 同步上传文件
     *
     * @param multipartFile 要上传的文件
     * @param user          用户标识
     * @param apiKey        API密钥
     * @return 上传成功后的文件信息实体
     * @throws IOException  文件处理异常
     */
    public FileInfo uploadFile(MultipartFile multipartFile, String user, String apiKey) throws IOException {
        // 准备请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 准备 multipart/form-data 格式的请求体
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // 处理文件参数
        Resource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
        
        // 设置文件的 Content-Type
        String contentType = multipartFile.getContentType();
        if (contentType == null) {
            contentType = guessContentType(multipartFile.getOriginalFilename());
        }
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.parseMediaType(contentType));
        body.add("file", new HttpEntity<>(fileResource, fileHeaders));

        // 添加用户标识参数
        body.add("user", user);

        // 构建请求实体
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送 POST 请求
        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + UPLOAD_URI,
                requestEntity,
                String.class
        );

        // 检查响应状态并转换为 FileInfo 实体
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return objectMapper.readValue(response.getBody(), FileInfo.class);
        } else {
            throw new RuntimeException("文件上传失败，状态码：" + response.getStatusCodeValue());
        }
    }

    /**
     * 同步上传文件的重载方法（使用默认API Key）
     *
     * @param multipartFile 要上传的文件
     * @param user          用户标识
     * @return 上传成功后的文件信息实体
     * @throws IOException  文件处理异常
     */
    public FileInfo uploadFile(MultipartFile multipartFile, String user) throws IOException {
        return uploadFile(multipartFile, user, "app-zycdQrBx17QVDhynWelO9H8Q");
    }

    /**
     * 根据文件名猜测 MIME 类型
     */
    private String guessContentType(String filename) {
        if (filename == null) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        if (filename.endsWith(".pdf")) {
            return "application/pdf";
        } else if (filename.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (filename.endsWith(".doc")) {
            return "application/msword";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".jpeg") || filename.endsWith(".jpg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".webp")) {
            return "image/webp";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".wav")) {
            return "audio/wav";
        } else if (filename.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (filename.endsWith(".m4a")) {
            return "audio/mp4";
        } else if (filename.endsWith(".webm")) {
            return "audio/webm";
        } else if (filename.endsWith(".amr")) {
            return "audio/amr";
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }
}
