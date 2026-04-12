package com.aiinterview.model.dto.workflow.JD;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 职位描述生成请求DTO
 */
@Data
public class JobDescriptionRequest {

    /**
     * 工作职位名称
     */
    @NotBlank(message = "工作职位不能为空")
    private String job;

    /**
     * 工作类型
     */
    @NotBlank(message = "工作类型不能为空")
    private String jobType;

    /**
     * 薪资范围
     */
    @NotBlank(message = "薪资不能为空")
    private String salary;

    /**
     * 学历要求
     */
    @NotBlank(message = "学历要求不能为空")
    private String education;

    /**
     * 经验要求
     */
    @NotBlank(message = "经验要求不能为空")
    private String experience;

    /**
     * 工作地点
     */
    @NotBlank(message = "工作地点不能为空")
    private String place;

    /**
     * 职位描述
     */
    @NotBlank(message = "职位描述不能为空")
    private String description;

    /**
     * 职位要求
     */
    @NotBlank(message = "职位要求不能为空")
    private String requirement;

    /**
     * 用户标识
     */
    @NotBlank(message = "用户标识不能为空")
    private String user;
}
