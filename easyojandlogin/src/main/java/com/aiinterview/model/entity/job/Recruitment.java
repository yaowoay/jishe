package com.aiinterview.model.entity.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 招聘职位信息
 * 对应数据库表 recruitment  //这个表不是已经废弃了吗
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recruitment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long jobId;                    // 职位ID
    private String positionName;           // 职位名称
    private String companyName;            // 公司名称
    private String experienceReq;          // 经验要求
    private String educationReq;           // 学历要求
    private String workLocation;           // 工作地点
    private String salary;                 // 薪资（原始）
    private Double salaryLower;            // 薪资下限
    private Double salaryUpper;            // 薪资上限
    private Double avgSalary;              // 平均薪资
    private String jobDescription;         // 职位描述
    private String companyWelfare;         // 公司福利
    private String companyScale;           // 公司规模
    private String companyType;            // 公司类型
    private String companyTags;            // 公司标签
    private String skillsProfession;       // 专业技能
    private String skillsSoft;             // 软技能
    private String city;                   // 城市
    private String district;               // 区县
    private String province;               // 省份
    private LocalDateTime publishTime;     // 发布时间
    private LocalDateTime createTime;      // 创建时间
    private LocalDateTime updateTime;      // 更新时间
    private Integer status;                // 状态 0-下架 1-上架
}
