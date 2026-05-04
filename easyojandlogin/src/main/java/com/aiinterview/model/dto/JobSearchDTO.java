package com.aiinterview.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 职位搜索结果DTO
 * 整合 jobs 和 companies 表的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 职位信息
    private Integer jobId;
    private String positionName;           // 职位名称 (对应 jobs.title)
    private String jobType;                // 职位类型 (全职/兼职/实习)
    private String jobDescription;         // 职位描述 (对应 jobs.job_duty)
    private String jobRequirements;        // 职位要求 (对应 jobs.job_requirements)
    private Integer minSalary;             // 最低薪资
    private Integer maxSalary;             // 最高薪资
    private Integer avgSalary;             // 平均薪资
    private String location;               // 工作地点
    private String city;                   // 城市
    private String district;               // 区县
    private String educationReq;           // 学历要求 (对应 jobs.education_requirement)
    private String experienceReq;          // 经验要求 (对应 jobs.experience_requirement)
    private String jobSkills;              // 技能要求
    private LocalDate postDate;            // 发布日期
    private LocalDate expirationDate;      // 过期日期
    
    // 公司信息
    private Integer companyId;
    private String companyName;            // 公司名称
    private String industry;               // 所属行业
    private String companyScale;           // 公司规模 (对应 companies.scale)
    private String companyType;            // 公司类型
    private String companyWelfare;         // 公司福利
    private String companyTags;            // 公司标签
    private String logoUrl;                // 公司Logo
    private String verifyStatus;           // 审核状态
}
