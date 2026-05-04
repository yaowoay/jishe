package com.aiinterview.model.dto.cooperation;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实训基地DTO
 */
@Data
public class TrainingBaseDTO {

    private Long id;

    private Long projectId;

    private String projectName;

    private String baseName;

    private String baseAddress;

    private BigDecimal baseArea;

    private BigDecimal equipmentValue;

    private Integer capacity;

    private String trainingFields;

    private String facilities;

    private String constructionStatus;

    private LocalDateTime completionDate;

    private Integer annualTrainingCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
