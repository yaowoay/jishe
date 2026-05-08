/*
package com.aiinterview.service;

import com.aiinterview.model.entity.student.StudentProfile;
import org.springframework.stereotype.Service;

// com/aiinterview/service/CompositeScoreService.java
@Service
public class CompositeScoreService {

    */
/**
     * 计算学业风险 (100 - GPA折算分)
     *//*

    private double calcAcademicRisk(StudentProfile student) {
        // GPA满分4.0
        double gpa = student.getGpa() != null ? student.getGpa() : 3.0;
        return 100 - Math.min(gpa / 4.0 * 100, 100);
    }

    */
/**
     * 计算能力风险 (100 - 技能分)
     *//*

    private double calcAbilityRisk(Long studentId) {
        int skillCount = getSkillCount(studentId);
        int projectCount = getProjectCount(studentId);
        int score = Math.min(skillCount * 8 + projectCount * 4, 100);
        return 100 - score;
    }

    */
/**
     * 计算行为风险
     *//*

    private double calcBehaviorRisk(Long studentId) {
        // 30天内活跃度
        int activeDays = getActiveDays(studentId, 30);
        int applicationCount = getApplicationCount(studentId, 30);

        // 活跃度得分 (满分50)
        double activityScore = Math.min(activeDays / 30.0 * 50, 50);
        // 转化率得分 (满分50)
        double conversionScore = applicationCount > 0 ?
                Math.min((double) applicationCount / 20 * 50, 50) : 0;

        return 100 - (activityScore + conversionScore);
    }

    */
/**
     * 计算结果风险
     *//*

    private double calcResultRisk(Long studentId) {
        double avgInterviewScore = getAvgInterviewScore(studentId);
        double employmentStatus = getEmploymentStatusScore(studentId);
        return (100 - avgInterviewScore + employmentStatus) / 2;
    }

    */
/**
     * 综合加权得分
     *//*

    public int calcCompositeRisk(StudentProfile student) {
        double academic = calcAcademicRisk(student);
        double ability = calcAbilityRisk(student.getStudentId());
        double behavior = calcBehaviorRisk(student.getStudentId());
        double result = calcResultRisk(student.getStudentId());

        double total = academic * 0.2 + ability * 0.3 + behavior * 0.3 + result * 0.2;
        return (int) Math.round(total);
    }

    */
/**
     * 判断风险等级
     *//*

    public String getRiskLevel(int score) {
        if (score >= 80) return "high";
        if (score >= 50) return "medium";
        return "low";
    }
}*/
