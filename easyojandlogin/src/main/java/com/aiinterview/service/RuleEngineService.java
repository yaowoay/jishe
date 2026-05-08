/*
package com.aiinterview.service;

import com.aiinterview.mapper.EarlyWarningResultMapper;
import com.aiinterview.mapper.ResumeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// com/aiinterview/service/RuleEngineService.java
@Service
public class RuleEngineService {

    @Autowired
    private UserJobApplicationMapper jobAppMapper;
    @Autowired
    private ResumeMapper resumeMapper;
    @Autowired
    private EarlyWarningResultMapper warningMapper;
    @Autowired
    private AiInterviewMapper interviewMapper;

    */
/**
     * 规则1：长期零投递（30天无投递记录）
     *//*

    public boolean checkZeroApplication(Long studentId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        // 简历已发布但30天无投递
        Integer count = jobAppMapper.selectCount(
                new QueryWrapper<UserJobApplication>()
                        .eq("student_id", studentId)
                        .ge("created_at", thirtyDaysAgo)
        );
        return count == 0; // true = 触发预警
    }

    */
/**
     * 规则2：投递频率骤降（两周投递<2，且环比下降>70%）
     *//*

    public boolean checkApplicationDrop(Long studentId) {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        LocalDateTime fourWeeksAgo = LocalDateTime.now().minusWeeks(4);

        int recent = jobAppMapper.selectCount(
                new QueryWrapper<UserJobApplication>()
                        .eq("student_id", studentId)
                        .ge("created_at", twoWeeksAgo)
        );
        int previous = jobAppMapper.selectCount(
                new QueryWrapper<UserJobApplication>()
                        .eq("student_id", studentId)
                        .ge("created_at", fourWeeksAgo)
                        .lt("created_at", twoWeeksAgo)
        );

        if (previous == 0) return false;
        double dropRate = 1.0 - (double) recent / previous;
        return recent < 2 && dropRate > 0.7;
    }

    */
/**
     * 规则3：面试成功率归零（5次面试，0次通过）
     *//*

    public boolean checkInterviewAllFailed(Long studentId) {
        int interviewCount = jobAppMapper.selectCount(
                new QueryWrapper<UserJobApplication>()
                        .eq("student_id", studentId)
                        .eq("status", "interview")
        );
        int acceptedCount = jobAppMapper.selectCount(
                new QueryWrapper<UserJobApplication>()
                        .eq("student_id", studentId)
                        .eq("status", "accepted")
        );
        return interviewCount >= 5 && acceptedCount == 0;
    }

    */
/**
     * 规则4：测评崩盘（AI面试连续3次 < 50分）
     *//*

    public boolean checkAiScoreCollapse(Long studentId) {
        List<AiInterview> recent = interviewMapper.selectList(
                new QueryWrapper<AiInterview>()
                        .eq("student_id", studentId)
                        .lt("overall_score", 50)
                        .orderByDesc("created_at")
                        .last("limit 3")
        );
        return recent.size() >= 3;
    }

    */
/**
     * 运行所有规则，返回触发的规则列表
     *//*

    public List<String> runAllRules(Long studentId) {
        List<String> triggered = new ArrayList<>();

        if (checkZeroApplication(studentId)) {
            triggered.add("长期零投递：30天无投递记录");
        }
        if (checkApplicationDrop(studentId)) {
            triggered.add("投递频率骤降：近两周投递<2次，环比下降>70%");
        }
        if (checkInterviewAllFailed(studentId)) {
            triggered.add("面试成功率归零：5次面试无通过");
        }
        if (checkAiScoreCollapse(studentId)) {
            triggered.add("测评崩盘：AI面试连续3次<50分");
        }

        return triggered;
    }

    */
/**
     * 生成规则引擎预警
     *//*

    public void generateRuleWarning(Long studentId, List<String> rules) {
        EarlyWarningResult warning = new EarlyWarningResult();
        warning.setStudentId(studentId);
        warning.setWarningType("employment");
        warning.setWarningLevel("high"); // 规则触发一律high
        warning.setWarningScore(85);     // 规则触发给高分数
        warning.setTriggerReason("规则引擎触发：" + String.join("；", rules));
        warning.setDetectionTime(LocalDateTime.now());
        warning.setHandleStatus("pending");
        warning.setStudentViewed(false);
        warningMapper.insert(warning);
    }
}*/
