package com.aiinterview.model.dto.teacher;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 就业统计DTO
 */
@Data
public class EmploymentStatsDTO {
    
    // 总体统计
    private Integer totalStudents;
    private Integer employedCount;
    private Integer furtherStudyCount;
    private Integer unemployedCount;
    private Double employmentRate;
    
    // 按学院统计
    private List<CollegeEmploymentStats> collegeStats;
    
    // 按专业统计
    private List<MajorEmploymentStats> majorStats;
    
    // 薪资统计
    private SalaryStats salaryStats;
    
    // 就业城市分布
    private Map<String, Integer> cityDistribution;
    
    // 就业行业分布
    private Map<String, Integer> industryDistribution;
    
    // 月度就业趋势
    private List<MonthlyTrend> monthlyTrends;
    
    @Data
    public static class CollegeEmploymentStats {
        private Long collegeId;
        private String collegeName;
        private Integer totalStudents;
        private Integer employedCount;
        private Double employmentRate;
    }
    
    @Data
    public static class MajorEmploymentStats {
        private Long majorId;
        private String majorName;
        private Integer totalStudents;
        private Integer employedCount;
        private Double employmentRate;
    }
    
    @Data
    public static class SalaryStats {
        private String avgSalary;
        private String maxSalary;
        private String minSalary;
        private Map<String, Integer> salaryRangeDistribution;
    }
    
    @Data
    public static class MonthlyTrend {
        private String month;
        private Integer employedCount;
        private Integer furtherStudyCount;
    }
}
