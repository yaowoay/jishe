package com.aiinterview.model.dto.applicant;

import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * 求职者个人信息DTO
 */
@Data
public class ApplicantProfileDTO {
    // 来自 student_profile
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String fullName;      // 对应 student_profile.real_name

    @Pattern(regexp = "^(male|female|other)$", message = "性别值不正确")
    private String gender;        // 对应 student_profile.gender

    @Past(message = "出生日期必须是过去的日期")
    private LocalDate birthdate;

    @Pattern(regexp = "^(高中|大专|本科|硕士|博士)$", message = "学历值不正确")
    private String educationLevel;// 对应 student_profile.education_level

    // 来自 resume
    @Min(value = 0, message = "工作年限不能为负数")
    @Max(value = 50, message = "工作年限不能超过50年")
    private Integer workYears;
    
    @Size(max = 50, message = "期望职位长度不能超过50个字符")
    private String expectedPosition;
    
    @Min(value = 0, message = "期望薪资不能为负数")
    @Max(value = 999999, message = "期望薪资不能超过999999")
    private Integer expectedSalary;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone; // 来自 resume.phone 或 users.phone
    private String resumeUrl; // 来自 resumes.file_url
}
