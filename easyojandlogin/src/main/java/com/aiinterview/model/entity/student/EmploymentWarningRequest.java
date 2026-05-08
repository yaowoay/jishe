// com/aiinterview/entity/EmploymentWarningRequest.java
package com.aiinterview.model.entity.student;

import lombok.Data;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class EmploymentWarningRequest {
    private String studentId;        // 学号
    private String name;             // 姓名
    private String education;        // 学历
    private String hasInternship;    // 是否有实习经历
    private String gender;           // 性别
    private BigDecimal expectedSalary; // 期望薪资
    private Double satisfaction;     // 满意度评分
    private String major;           // 专业
    private String workCity;        // 工作城市
    private String industry;        // 就业行业

    // 转换为Python服务需要的Map格式
    public Map<String, Object> toPythonMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("学历", education);
        map.put("是否有实习经历", hasInternship);
        map.put("性别", gender);
        map.put("期望薪资", expectedSalary);
        map.put("满意度评分", satisfaction);
        map.put("专业", major);
        map.put("工作城市", workCity);
        map.put("就业行业", industry);
        return map;
    }
}