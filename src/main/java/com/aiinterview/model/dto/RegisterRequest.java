package com.aiinterview.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(student|company|teacher)$", message = "角色只能是student、company或teacher")
    private String role;

    // 求职者相关字段（对应 student_profile 表）
    private String studentNo;
    private String realName;
    private Long collegeId;
    private Long majorId;
    private String className;
    private String educationLevel;
    private Integer graduationYear;
    private BigDecimal gpa;
    private String skills;
    private String expectedCity;
    private Integer expectedSalaryMin;
    private String phone;

    // 公司相关字段
    private String companyName;
    private String industry;
    private String scale;
    private String contactPhone;

    // 教师相关字段
    private String teacherNo;
    private String teacherName;

    
//    @Pattern(regexp = "^(counselor|advisor|admin|leader)$", message = "教师角色类型只能是counselor、advisor、admin或leader")
    private String roleType;
}
