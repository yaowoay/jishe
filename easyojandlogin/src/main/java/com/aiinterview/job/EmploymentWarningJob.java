/*
package com.aiinterview.job;

import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.model.entity.student.EmploymentWarningRequest;
import com.aiinterview.model.entity.student.EmploymentWarningResponse;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.service.CompositeScoreService;
import com.aiinterview.service.EmploymentWarningService;
import com.aiinterview.service.RuleEngineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

// com/aiinterview/job/EmploymentWarningJob.java
@Slf4j
@Component
public class EmploymentWarningJob {

    @Autowired
    private RuleEngineService ruleEngineService;
    @Autowired
    private EmploymentWarningService employmentWarningService;
    @Autowired
    private CompositeScoreService compositeScoreService;
    @Autowired
    private StudentProfileMapper studentProfileMapper;

    */
/**
     * 每天凌晨2点执行
     *//*

    @Scheduled(cron = "0 0 2 * * ?")
    public void scanAllStudents() {
        log.info("开始每日就业预警扫描...");

        List<StudentProfile> students = studentProfileMapper.selectList(null);

        int ruleTriggered = 0;
        int modelWarning = 0;

        for (StudentProfile student : students) {
            try {
                Long studentId = student.getStudentId();

                // 第一层：规则引擎
                List<String> triggeredRules = ruleEngineService.runAllRules(studentId);
                if (!triggeredRules.isEmpty()) {
                    ruleEngineService.generateRuleWarning(studentId, triggeredRules);
                    ruleTriggered++;
                    continue; // 规则触发后跳到下一个学生
                }

                // 第二层：XGBoost模型
                EmploymentWarningRequest request = buildRequest(student);
                EmploymentWarningResponse.PredictionResult result =
                        employmentWarningService.evaluateStudent(request);

                // 第三层：综合评分
                int compositeScore = compositeScoreService.calcCompositeRisk(student);
                String riskLevel = compositeScoreService.getRiskLevel(compositeScore);

                // 只有中高风险才入库
                if (!"low".equals(riskLevel) || result.getSuccessProbability() < 0.4) {
                    saveWarning(studentId, result, compositeScore, riskLevel);
                    modelWarning++;
                }

            } catch (Exception e) {
                log.error("学生 {} 预警扫描失败: {}", student.getStudentId(), e.getMessage());
            }
        }

        log.info("扫描完成：规则触发{}人，模型预警{}人", ruleTriggered, modelWarning);
    }
}*/
