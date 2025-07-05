// ResumeService.java (接口)
package com.coldwind.easyoj.service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {
    String uploadResume(MultipartFile file) throws Exception;
}