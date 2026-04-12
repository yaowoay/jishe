package com.aiinterview.model.dto;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * 技能专长DTO
 */
@Data
public class SkillDTO {
    
    @Size(max = 100, message = "技能名称长度不能超过100个字符")
    private String skillName;
    
    @Min(value = 1, message = "熟练度最小为1")
    @Max(value = 5, message = "熟练度最大为5")
    private Integer proficiency;
    
    private String description;
} 