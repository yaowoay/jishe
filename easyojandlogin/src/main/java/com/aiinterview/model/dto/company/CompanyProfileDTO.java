package com.aiinterview.model.dto.company;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 公司信息DTO
 */
@Data
public class CompanyProfileDTO {
    
    private Long companyId;
    
    @NotBlank(message = "公司名称不能为空")
    @Size(max = 100, message = "公司名称长度不能超过100个字符")
    private String companyName;
    
    @NotBlank(message = "所属行业不能为空")
    @Size(max = 50, message = "行业长度不能超过50个字符")
    private String industry;
    
    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address;
    
    @NotBlank(message = "公司规模不能为空")
    @Pattern(regexp = "^(1-50人|51-100人|101-500人|500人以上)$", message = "公司规模值不正确")
    private String scale;
    
    @Size(max = 100, message = "网站长度不能超过100个字符")
    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$",
             message = "网站格式不正确")
    private String website;
    
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", message = "联系电话格式不正确")
    private String contactPhone;
    
    private String logoUrl;
    
    @Size(max = 1000, message = "公司描述长度不能超过1000个字符")
    private String description;

    private String verifyStatus;

    private Integer creditScore;

    private Integer profileCompletion;

    private String companyType;

    private String companyWelfare;

    private String companyTags;
}
