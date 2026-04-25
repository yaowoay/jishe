package com.aiinterview.service.impl.doris;

import com.aiinterview.model.dto.doris.CollegeMajorEmploymentStatusCountDTO;
import com.aiinterview.model.dto.doris.EmploymentStatusCountDTO;
import com.aiinterview.service.doris.CollegeMajorEmploymentStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollegeMajorEmploymentStatServiceImpl implements CollegeMajorEmploymentStatService {

    private static final String TABLE_NAME = "realtime.college_major_employment_stat";

    private final @Qualifier("dorisJdbcTemplate") JdbcTemplate dorisJdbcTemplate;

    @Override
    public List<EmploymentStatusCountDTO> getEmploymentStatusCount(Integer graduationYear) {
        String sql = "select employment_status, sum(student_count) as student_count "
                + "from " + TABLE_NAME
                + buildYearWhere(graduationYear)
                + " group by employment_status order by student_count desc";

        if (graduationYear == null) {
            return dorisJdbcTemplate.query(sql, (rs, rowNum) -> new EmploymentStatusCountDTO(
                    rs.getString("employment_status"),
                    rs.getLong("student_count")
            ));
        }
        return dorisJdbcTemplate.query(sql, (rs, rowNum) -> new EmploymentStatusCountDTO(
                rs.getString("employment_status"),
                rs.getLong("student_count")
        ), graduationYear);
    }

    @Override
    public List<CollegeMajorEmploymentStatusCountDTO> getCollegeMajorEmploymentStatusCount(Integer graduationYear) {
        String sql = "select college, major, employment_status, sum(student_count) as student_count "
                + "from " + TABLE_NAME
                + buildYearWhere(graduationYear)
                + " group by college, major, employment_status order by college asc, major asc, student_count desc";

        if (graduationYear == null) {
            return dorisJdbcTemplate.query(sql, (rs, rowNum) -> new CollegeMajorEmploymentStatusCountDTO(
                    rs.getString("college"),
                    rs.getString("major"),
                    rs.getString("employment_status"),
                    rs.getLong("student_count")
            ));
        }
        return dorisJdbcTemplate.query(sql, (rs, rowNum) -> new CollegeMajorEmploymentStatusCountDTO(
                rs.getString("college"),
                rs.getString("major"),
                rs.getString("employment_status"),
                rs.getLong("student_count")
        ), graduationYear);
    }

    private String buildYearWhere(Integer graduationYear) {
        if (graduationYear == null) {
            return "";
        }
        return " where graduation_year = ?";
    }
}