package com.aiinterview.model.dto.resume;

import lombok.Data;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 其他信息DTO
 */
@Data
public class AdditionalInfoDTO {
    
    @Size(max = 50, message = "信息类型长度不能超过50个字符")
    private String type;
    
    @Size(max = 100, message = "名称长度不能超过100个字符")
    private String name;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    private String description;
} 