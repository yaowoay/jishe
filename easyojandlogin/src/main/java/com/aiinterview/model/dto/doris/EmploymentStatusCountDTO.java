package com.aiinterview.model.dto.doris;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentStatusCountDTO implements Serializable {

    private String employmentStatus;

    private Long studentCount;
}