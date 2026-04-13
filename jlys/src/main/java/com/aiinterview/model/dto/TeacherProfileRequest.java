package com.aiinterview.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 教师档案完善请求DTO
 */
@Data
public class TeacherProfileRequest {
    
    @NotBlank(message = "工号不能为空")
    private String teacherNo;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    private Long collegeId;
    
    @NotBlank(message = "教师角色不能为空")
    @Pattern(regexp = "^(counselor|advisor|admin|leader)$", message = "教师角色只能是counselor、advisor、admin或leader")
    private String roleType;
    
    private String phone;
    
    private String email;
    
    private String managedColleges;
    
    private String managedMajors;
}