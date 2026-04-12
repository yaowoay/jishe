package com.aiinterview.service.impl.api;

import com.aiinterview.service.api.ExternalApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 外部API服务实现类
 */
@Slf4j
@Service
public class ExternalApiServiceImpl implements ExternalApiService {

    private static final String EXTERNAL_API_BASE_URL = "http://81.70.20.30/v1";
    
    private final RestTemplate restTemplate;
    
    public ExternalApiServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String uploadFileToExternal(MultipartFile file, String user, String apiKey) {
        try {
            String url = EXTERNAL_API_BASE_URL + "/files/upload";
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + apiKey);
            
            // 创建文件资源
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            
            // 构建multipart请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("user", user);
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            log.info("调用外部文件上传接口: {}", url);
            log.info("文件名: {}, 用户: {}", file.getOriginalFilename(), user);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            
            log.info("外部API响应状态: {}", response.getStatusCode());
            log.info("外部API响应内容: {}", response.getBody());
            
            return response.getBody();
            
        } catch (IOException e) {
            log.error("读取文件内容失败", e);
            throw new RuntimeException("读取文件内容失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("调用外部文件上传接口失败", e);
            throw new RuntimeException("调用外部API失败: " + e.getMessage());
        }
    }

    @Override
    public String runWorkflowExternal(String requestBody, String apiKey) {
        try {
            String url = EXTERNAL_API_BASE_URL + "/workflows/run";
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            
            log.info("调用外部工作流接口: {}", url);
            log.info("请求体: {}", requestBody);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            
            log.info("外部工作流API响应状态: {}", response.getStatusCode());
            log.info("外部工作流API响应内容: {}", response.getBody());
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("调用外部工作流接口失败", e);
            throw new RuntimeException("调用外部工作流API失败: " + e.getMessage());
        }
    }
}
