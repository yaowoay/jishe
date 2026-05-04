package com.aiinterview.model.dto.teacher;

import lombok.Data;

/**
 * 教师工作台DTO
 */
@Data
public class TeacherDashboardDTO {

    private Long totalStudents;
    private Long employedStudents;
    private Long pendingStudents;
    private Long warningStudents;
    private Long totalCompanies;
    private Long pendingCompanyVerifications;
    private Long totalActivities;
    private Long cooperationApplications;
}
