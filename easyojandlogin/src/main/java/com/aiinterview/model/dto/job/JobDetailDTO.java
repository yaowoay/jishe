package com.aiinterview.model.dto.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 职位详情DTO
 * 整合 jobs 和 companies 表的数据，用于前端展示
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 职位基本信息
    private Long jobId;
    private String title;                  // 职位名称
    private String jobType;                // 职位类型 (全职/兼职/实习)
    private String description;            // 岗位职责 (对应 job_duty)
    private String requirements;           // 岗位要求 (对应 job_requirements)
    private String experience;             // 经验要求
    private String education;              // 学历要求
    private String skills;                 // 职位技能
    
    // 薪资信息
    private Integer minSalary;             // 最低薪资
    private Integer maxSalary;             // 最高薪资
    
    // 地点信息
    private String location;               // 工作地点
    
    // 职位状态
    private Boolean isActive;              // 是否激活
    private String verifyStatus;           // 审核状态
    private String verifyRemark;           // 审核备注
    
    // 时间信息
    private LocalDate postDate;            // 发布日期
    private LocalDate expirationDate;      // 截止日期
    private LocalDateTime createdAt;       // 创建时间
    private LocalDateTime updatedAt;       // 更新时间
    
    // 公司信息（关联查询）
    private Long companyId;
    private String companyName;            // 公司名称
    private String industry;               // 所属行业
    private String companyScale;           // 公司规模
    private String companyAddress;         // 公司地址
    private String companyWebsite;         // 公司网站
    private String companyDescription;     // 公司描述
    private String logoUrl;                // 公司Logo
}
