package com.aiinterview.model.dto;

import lombok.Data;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 工作经历DTO
 */
@Data
public class WorkExperienceDTO {
    
    @Size(max = 100, message = "公司名称长度不能超过100个字符")
    private String company;
    
    @Size(max = 100, message = "职位长度不能超过100个字符")
    private String position;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    private String responsibility;
    private String achievement;
} 