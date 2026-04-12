package com.aiinterview.model.dto.response;

import com.aiinterview.model.dto.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 简历响应DTO
 */
@Data
public class ResumeResponse {
    
    private Long id;
    private String name;
    private String fullName;
    private String phone;
    private String email;
    private String position;
    private Integer workYears;
    private String location;
    private String profile;
    
    /**
     * 简历模板类型
     */
    private String template;
    
    private Boolean isDefault;
    private String shareUrl;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 详细信息列表
    private List<WorkExperienceDTO> workExperiences;
    private List<EducationDTO> educations;
    private List<ProjectExperienceDTO> projectExperiences;
    private List<SkillDTO> skills;
    private List<AdditionalInfoDTO> additionalInfos;
} 