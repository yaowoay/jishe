package com.aiinterview.model.dto.request;

import com.aiinterview.model.dto.*;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 简历创建请求 DTO（完整字段，与 resume 表一一对应）
 */
@Data
public class ResumeCreateRequest {

    /* ================= 基础信息 ================= */

    @NotBlank(message = "简历名称不能为空")
    @Size(max = 100, message = "简历名称长度不能超过100个字符")
    private String name;

    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String fullName;

    @Size(max = 20, message = "电话长度不能超过20个字符")
    private String phone;

    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Size(max = 255, message = "头像地址长度不能超过255个字符")
    private String avatarUrl;

    @Size(max = 100, message = "期望职位长度不能超过100个字符")
    private String position;

    private Integer workYears;

    @Size(max = 50, message = "期望城市长度不能超过50个字符")
    private String expectedCity;

    private BigDecimal expectedSalaryMin;
    private BigDecimal expectedSalaryMax;

    @Size(max = 100, message = "期望行业长度不能超过100个字符")
    private String expectedIndustry;

    private String profile;

    /* ================= 模板相关 ================= */

    private Long templateId;

    @Size(max = 50, message = "模板类型长度不能超过50个字符")
    private String templateType;

    private Map<String, Object> templateConfig;

    /* ================= 状态 & 统计 ================= */

    @Size(max = 20, message = "简历状态长度不能超过20个字符")
    private String status;

    private Boolean isDefault;
    private Boolean isDeleted;

    private Integer viewCount;
    private Integer downloadCount;

    @Size(max = 50, message = "分享码长度不能超过50个字符")
    private String shareCode;

    /* ================= 详细信息列表 ================= */

    private List<WorkExperienceDTO> workExperiences;
    private List<EducationDTO> educations;
    private List<ProjectExperienceDTO> projectExperiences;
    private List<SkillDTO> skills;
    private List<AdditionalInfoDTO> additionalInfos;

    /* ================= AI 生成文件 ================= */

    private String imgUrl;
    private String wordUrl;
    private String pdfUrl;
}