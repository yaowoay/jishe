package com.aiinterview.model.dto.resume;

import lombok.Data;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 教育经历DTO
 */
@Data
public class EducationDTO {
    
    @Size(max = 100, message = "学校名称长度不能超过100个字符")
    private String school;
    
    @Size(max = 100, message = "专业长度不能超过100个字符")
    private String major;
    
    @Size(max = 50, message = "学历长度不能超过50个字符")
    private String degree;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    private String description;
} 