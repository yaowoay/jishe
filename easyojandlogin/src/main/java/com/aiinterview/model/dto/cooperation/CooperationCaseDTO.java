package com.aiinterview.model.dto.cooperation;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合作案例DTO
 */
@Data
public class CooperationCaseDTO {

    private Long id;

    private String caseTitle;

    private String companyName;

    private String cooperationType;

    private String cooperationDuration;

    private Integer studentCount;

    private String caseSummary;

    private String achievements;

    private String successFactors;

    private String images;

    private String documents;

    private Boolean isFeatured;

    private Integer viewCount;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
