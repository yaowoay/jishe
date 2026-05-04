package com.aiinterview.model.dto.application;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 投递记录详情DTO - 用于返回求职者的投递记录详细信息
 */
@Data
public class ApplicationDetailDTO {

    // 申请信息
    private Long applicationId;
    private String status;
    private String statusDisplayName; // 状态的中文显示名称
    private LocalDateTime applyTime;
    private Float aiEvaluationScore;
    private String feedback;
    private LocalDateTime interviewTime;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 职位信息
    private Long jobId;
    private String jobTitle;
    private String jobType;
    private String jobLocation;
    private String jobDescription; // 岗位职责
    private String jobRequirements; // 岗位要求
    private String experienceRequirement; // 经验要求
    private String educationRequirement; // 学历要求
    private String jobSkills; // 职位技能
    private Integer minSalary;
    private Integer maxSalary;
    private LocalDate postDate;
    private LocalDate expirationDate;

    // 公司信息
    private Long companyId;
    private String companyName;
    private String companyDescription;
    private String companyLocation;

    // 简历信息
    private Long resumeId;
    private String resumeFilename;
    private String resumeFileUrl;
    private LocalDateTime resumeUploadDate;
    private String resumeParsedData;
}
