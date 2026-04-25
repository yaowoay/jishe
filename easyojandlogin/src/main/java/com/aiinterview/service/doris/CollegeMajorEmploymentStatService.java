package com.aiinterview.service.doris;

import com.aiinterview.model.dto.doris.CollegeMajorEmploymentStatusCountDTO;
import com.aiinterview.model.dto.doris.EmploymentStatusCountDTO;

import java.util.List;

public interface CollegeMajorEmploymentStatService {

    List<EmploymentStatusCountDTO> getEmploymentStatusCount(Integer graduationYear);

    List<CollegeMajorEmploymentStatusCountDTO> getCollegeMajorEmploymentStatusCount(Integer graduationYear);
}