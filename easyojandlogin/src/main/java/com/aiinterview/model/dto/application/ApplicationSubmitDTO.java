package com.aiinterview.model.dto.application;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 简历投递请求DTO
 */
@Data
public class ApplicationSubmitDTO {
    
    @NotNull(message = "职位ID不能为空")
    private Long jobId;
    
    @NotNull(message = "简历ID不能为空")
    private Long resumeId;
    private String coverLetter;  // 可选
}
