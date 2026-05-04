package com.aiinterview.model.dto.teacher;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 教师信息DTO
 */
@Data
public class TeacherProfileDTO {

    private Long teacherId;
    private Long userId;
    private Long collegeId;

    @NotBlank(message = "工号不能为空")
    @Size(max = 50, message = "工号长度不能超过50个字符")
    private String teacherNo;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;

    @NotBlank(message = "教师角色不能为空")
    @Pattern(regexp = "^(counselor|advisor|admin|leader)$", message = "教师角色类型不正确")
    private String roleType;

//    @NotBlank(message = "手机号不能为空")
    private String phone;

//    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    private String managedColleges;
    private String managedMajors;
    private String status;
}
