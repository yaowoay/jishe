package com.aiinterview.service.impl.doris;

import com.aiinterview.model.dto.doris.EmploymentScreenBigDataDTO;
import com.aiinterview.service.doris.EmploymentScreenBigDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmploymentScreenBigDataServiceImpl implements EmploymentScreenBigDataService {

    private static final String BASE_SQL = "select id, school_total, school_employed, school_employment_rate, signed_rate, postgraduate_rate, city_distribute, industry_distribute, salary_distribute, update_time from employment_screen_bigdata";

    private final @Qualifier("dorisJdbcTemplate") JdbcTemplate dorisJdbcTemplate;

    private final BeanPropertyRowMapper<EmploymentScreenBigDataDTO> rowMapper =
            BeanPropertyRowMapper.newInstance(EmploymentScreenBigDataDTO.class);

    @Override
    public EmploymentScreenBigDataDTO getSingle() {
        try {
            return dorisJdbcTemplate.queryForObject(
                    BASE_SQL + " order by update_time desc limit 1",
                    rowMapper
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}