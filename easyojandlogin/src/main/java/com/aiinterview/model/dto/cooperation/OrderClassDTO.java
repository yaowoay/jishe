package com.aiinterview.model.dto.cooperation;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单班DTO
 */
@Data
public class OrderClassDTO {

    private Long id;

    private Long projectId;

    private String projectName;

    private String className;

    private Long companyId;

    private String companyName;

    private String major;

    private String grade;

    private Integer plannedCount;

    private Integer enrolledCount;

    private String trainingPlan;

    private String curriculum;

    private BigDecimal employmentRate;

    private LocalDate startDate;

    private LocalDate graduationDate;

    private String classStatus;

    private String instructor;

    private String companyMentor;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
