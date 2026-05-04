package com.aiinterview.model.dto.teacher;

import lombok.Data;

/**
 * 就业审核DTO
 */
@Data
public class EmploymentAuditDTO {

    private Long ledgerId;
    private String verifyStatus;
    private String employmentStatus;
    private String companyName;
    private String position;
    private String salaryRange;
    private String employmentCity;
    private String furtherStudySchool;
}
