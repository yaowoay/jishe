package com.aiinterview.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.Year;

/**
 * 学生档案完善请求DTO
 */
@Data
public class StudentProfileRequest {
    
    @NotBlank(message = "学号不能为空")
    private String studentNo;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    @Pattern(regexp = "^(male|female|other)$", message = "性别只能是male、female或other")
    private String gender;
    
    private LocalDate birthDate;
    
    @NotBlank(message = "学院不能为空")
    private String college;
    
    @NotBlank(message = "专业不能为空")
    private String major;
    
    @NotBlank(message = "班级不能为空")
    private String className;
    
    private String grade;
    
    @Pattern(regexp = "^(专科|本科|硕士|博士)$", message = "学历只能是专科、本科、硕士或博士")
    private String educationLevel;
    
    @NotNull(message = "毕业年份不能为空")
    private Year graduationYear;
}