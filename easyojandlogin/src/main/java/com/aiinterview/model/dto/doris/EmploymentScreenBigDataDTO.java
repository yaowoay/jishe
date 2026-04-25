package com.aiinterview.model.dto.doris;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentScreenBigDataDTO implements Serializable {

    private Long id;

    private Integer schoolTotal;

    private Integer schoolEmployed;

    private BigDecimal schoolEmploymentRate;

    private BigDecimal signedRate;

    private BigDecimal postgraduateRate;

    private String cityDistribute;

    private String industryDistribute;

    private String salaryDistribute;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}