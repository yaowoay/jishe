package com.aiinterview.model.dto.teacher;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 帮扶记录DTO
 */
@Data
public class AssistanceRecordDTO {

    private Long recordId;

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotBlank(message = "困难类型不能为空")
    private String difficultyType;

    private String difficultyLevel;
    private String description;
    private String assistancePlan;
    private String assistanceActions;
    private String result;
}
