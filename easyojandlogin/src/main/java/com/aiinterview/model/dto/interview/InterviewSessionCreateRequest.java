package com.aiinterview.model.dto.interview;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class InterviewSessionCreateRequest {
    private String resumePath;
    private String jobPosition;
    private String style;
    private MultipartFile resume;
}
