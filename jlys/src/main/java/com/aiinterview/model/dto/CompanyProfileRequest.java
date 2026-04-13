package com.aiinterview.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 企业档案完善请求DTO
 */
@Data
public class CompanyProfileRequest {
    
    @NotBlank(message = "公司名称不能为空")
    private String companyName;
    
    @NotBlank(message = "所属行业不能为空")
    private String industry;
    
    private String address;
    
    @NotBlank(message = "公司规模不能为空")
    @Pattern(regexp = "^(1-50人|51-100人|101-500人|500人以上)$", message = "公司规模只能是1-50人、51-100人、101-500人或500人以上")
    private String scale;
    
    private String website;
    
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;
    
    private String logoUrl;
    
    private String description;
}