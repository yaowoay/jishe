package com.aiinterview.model.entity.applicant;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 求职者实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("applicants")
public class Applicant {
    
    @TableId(value = "applicant_id", type = IdType.AUTO)
    private Long applicantId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("full_name")
    private String fullName;
    
    @TableField("phone")
    private String phone;
    
    @TableField("resume_url")
    private String resumeUrl;
    
    @TableField("gender")
    private String gender; // 'male', 'female', 'other'
    
    @TableField("birthdate")
    private LocalDate birthdate;
    
    @TableField("education_level")
    private String educationLevel; // '高中', '大专', '本科', '硕士', '博士'
    
    @TableField("work_years")
    private Integer workYears;
    
    @TableField("expected_position")
    private String expectedPosition;
    
    @TableField("expected_salary")
    private Integer expectedSalary;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
