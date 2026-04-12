package com.aiinterview.model.dto;

import lombok.Data;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 项目经验DTO
 */
@Data
public class ProjectExperienceDTO {
    
    @Size(max = 100, message = "项目名称长度不能超过100个字符")
    private String projectName;
    
    @Size(max = 100, message = "担任角色长度不能超过100个字符")
    private String role;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    private String description;
} 