package com.aiinterview.model.dto.request;

import com.aiinterview.model.dto.*;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 简历创建请求DTO
 */
@Data
public class ResumeCreateRequest {
    
    @NotBlank(message = "简历名称不能为空")
    @Size(max = 100, message = "简历名称长度不能超过100个字符")
    private String name;
    
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String fullName;
    
    @Size(max = 20, message = "电话长度不能超过20个字符")
    private String phone;
    
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @Size(max = 100, message = "期望职位长度不能超过100个字符")
    private String position;
    
    private Integer workYears;
    
    @Size(max = 100, message = "所在地长度不能超过100个字符")
    private String location;
    
    private String profile;
    
    /**
     * 简历模板类型
     */
    private String template;
    
    // 详细信息列表
    private List<WorkExperienceDTO> workExperiences;
    private List<EducationDTO> educations;
    private List<ProjectExperienceDTO> projectExperiences;
    private List<SkillDTO> skills;
    private List<AdditionalInfoDTO> additionalInfos;

    // AI生成简历的文件链接
    private String imgUrl;
    private String wordUrl;
    private String pdfUrl;
} 