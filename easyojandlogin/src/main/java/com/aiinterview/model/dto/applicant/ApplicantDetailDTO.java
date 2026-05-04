package com.aiinterview.model.dto.applicant;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 应聘者详情DTO
 */
@Data
public class ApplicantDetailDTO {

    // 申请信息
    private Long applicationId;
    private String status;
    private LocalDateTime applyTime;
    private Float aiEvaluationScore;
    private String feedback;
    private LocalDateTime interviewTime;
    private String rejectionReason;

    // 职位信息
    private Long jobId;
    private String jobTitle;
    private String jobType;
    private String jobLocation;

    // 用户信息
    private Long userId;
    private String email;
    private String fullName;
    private String phone;

    // 简历信息
    private Long resumeId;
    private String resumeFilename;
    private String resumeFileUrl;
    private LocalDateTime resumeUploadDate;

    // 申请者详细信息
    private Long applicantId;
    private String gender;
    private LocalDate birthdate;
    private String educationLevel;
    private Integer workYears;
    private String expectedPosition;
    private Integer expectedSalary;
    private String resumeUrl;

    // 兼容旧字段
    private String education;
    private String experience;
    private String skills;
    private String availability;
}
