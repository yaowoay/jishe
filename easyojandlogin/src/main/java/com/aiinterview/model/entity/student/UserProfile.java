package com.aiinterview.model.entity.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户画像信息
 * 对应数据库表 user_profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long profileId;                // 画像ID
    private Long userId;                   // 用户ID
    private String experience;             // 工作经验
    private String education;              // 学历
    private String jobType;                // 职位类型
    private String skills;                 // 技能（JSON格式）
    private String preferredCities;        // 偏好城市（JSON格式）
    private Double expectedSalaryMin;      // 期望薪资最小值
    private Double expectedSalaryMax;      // 期望薪资最大值
    private String industries;             // 行业偏好（JSON格式）
    private String languages;              // 语言能力（JSON格式）
    private Integer skillLevel;            // 技能等级 1-5
    private String companySize;            // 公司规模偏好
    private String workType;               // 工作性质
    private LocalDateTime createTime;      // 创建时间
    private LocalDateTime updateTime;      // 更新时间
}
