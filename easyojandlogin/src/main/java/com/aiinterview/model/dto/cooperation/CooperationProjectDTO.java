package com.aiinterview.model.dto.cooperation;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合作项目DTO
 */
@Data
public class CooperationProjectDTO {

    private Long id;

    private Long companyId;

    private String companyName;

    private String projectName;

    private String projectType;

    private String cooperationMode;

    private String projectDesc;

    private String targetMajor;

    private Integer studentCount;

    private BigDecimal investmentAmount;

    private LocalDate startDate;

    private LocalDate endDate;

    private String contactPerson;

    private String contactPhone;

    private String status;

    private LocalDateTime submitTime;

    private LocalDateTime reviewTime;

    private Long reviewedBy;

    private String reviewerName;

    private String reviewComment;

    private String attachments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
