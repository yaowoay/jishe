// ResumeService.java (接口)
package org.iflyproject.springdemos.service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {
    String uploadResume(MultipartFile file) throws Exception;
}