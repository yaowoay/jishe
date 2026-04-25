package com.aiinterview.model.dto.doris;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollegeMajorEmploymentStatusCountDTO implements Serializable {

    private String college;

    private String major;

    private String employmentStatus;

    private Long studentCount;
}