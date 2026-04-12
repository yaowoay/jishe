package com.aiinterview.model.dto.teacher;

import lombok.Data;

/**
 * 学生查询DTO
 */
@Data
public class StudentQueryDTO {

    private Long collegeId;
    private Long majorId;
    private String className;
    private Integer graduationYear;
    private String keyword;
}
