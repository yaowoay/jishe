package com.aiinterview.service;

import cn.hutool.json.JSONObject;
import com.aiinterview.mapper.ActivityRegistrationMapper;
import com.aiinterview.mapper.ApplicationMapper;
import com.aiinterview.mapper.EarlyWarningResultMapper;
import com.aiinterview.mapper.EmploymentLedgerMapper;
import com.aiinterview.mapper.MockInterviewAppointmentMapper;
import com.aiinterview.mapper.ResumerMapper;
import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.model.entity.teacher.ActivityRegistration;
import com.aiinterview.model.entity.teacher.EarlyWarningResult;
import com.aiinterview.model.entity.teacher.EmploymentLedger;
import com.aiinterview.model.entity.teacher.MockInterviewAppointment;
import com.aiinterview.model.entity.resumer;
import com.aiinterview.model.entity.student.EmploymentWarningResponse;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.repository.interview.InterviewAnswerRepository;
import com.aiinterview.repository.professionalTest.TestResultRepository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 就业预警驾驶舱：红 / 黄 / 蓝分级、四维风险分、规则 + 模型可解释结果，写入 early_warning_results（warning_type=cockpit）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentCockpitScanService {

    public static final String WARNING_TYPE_COCKPIT = "cockpit";

    private static final int MARKET_REF_SALARY = 7500;

    private final StudentProfileMapper studentProfileMapper;
    private final ApplicationMapper applicationMapper;
    private final InterviewAnswerRepository interviewAnswerRepository;
    private final EmploymentLedgerMapper employmentLedgerMapper;
    private final MockInterviewAppointmentMapper mockInterviewAppointmentMapper;
    private final ResumerMapper resumerMapper;
    private final TestResultRepository testResultRepository;
    private final ActivityRegistrationMapper activityRegistrationMapper;
    private final EarlyWarningResultMapper earlyWarningResultMapper;
    private final EmploymentWarningService employmentWarningService;

    // 在 EmploymentCockpitScanService 里加
    public void scanRecentActiveStudents() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<StudentProfile> activeStudents = studentProfileMapper.selectList(
                new QueryWrapper<StudentProfile>().ge("updated_at", yesterday)
        );

        log.info("增量扫描: {} 人", activeStudents.size());
        for (StudentProfile s : activeStudents) {
            try {
                evaluateAndUpsert(s, false, false);
            } catch (Exception e) {
                log.warn("增量扫描失败: studentId={}", s.getStudentId());
            }
        }
    }
    public void scanAllStudents() {
        List<StudentProfile> students = studentProfileMapper.selectList(new QueryWrapper<StudentProfile>().last("LIMIT 365"));
        for (StudentProfile s : students) {
            try {
                evaluateAndUpsert(s, false, false);
            } catch (Exception e) {
                log.warn("驾驶舱扫描 studentId={} 失败: {}", s.getStudentId(), e.getMessage());
            }
        }
        log.info("就业驾驶舱扫描完成，共 {} 名学生", students.size());
    }

    /**
     * 教师端「重算」：先同步写入规则+综合分（不调用 Python，接口快速返回），再在后台合并模型概率并二次落库。
     */
    public void evaluateOne(Long studentId) {
        StudentProfile s = studentProfileMapper.selectById(studentId);
        if (s == null) {
            throw new IllegalArgumentException("学生不存在");
        }
        if (s.getUserId() == null) {
            throw new IllegalArgumentException("学生未绑定平台用户，无法根据投递、面试与档案数据重算驾驶舱预警");
        }
        evaluateAndUpsert(s, true, true);
        CompletableFuture.runAsync(() -> {
            try {
                evaluateAndUpsert(s, true, false);
            } catch (Exception e) {
                log.warn("驾驶舱异步模型评估失败 studentId={}: {}", studentId, e.getMessage());
            }
        });
    }

    /**
     * @param skipModelCall 为 true 时不调用就业模型 HTTP，用于快速首屏；定时全量扫描传 false。
     */
    private void evaluateAndUpsert(StudentProfile student, boolean preserveHandleOnUpdate, boolean skipModelCall) {
        Long sid = student.getStudentId();
        Long uid = student.getUserId();
        if (uid == null) {
            return;
        }

        LocalDateTime evaluatedAt = LocalDateTime.now();
        LocalDateTime t30 = LocalDateTime.now().minusDays(30);
        LocalDateTime t60 = LocalDateTime.now().minusDays(60);

        long apps30 = Optional.ofNullable(applicationMapper.countApplicationsSince(uid, t30)).orElse(0L);
        long postIvReject = Optional.ofNullable(applicationMapper.countRejectedAfterInterview(uid)).orElse(0L);
        Double avgInterview = interviewAnswerRepository.avgScoreByUserId(uid);
        double avgIv = avgInterview != null ? avgInterview : 55.0;

        int profileComp = student.getProfileCompletion() != null ? student.getProfileCompletion() : 0;
        double academicRisk = 100.0 - Math.min(profileComp, 100);

        Integer maxTest = testResultRepository.findMaxScoreByUserId(uid.intValue());
        double abilityRisk;
        if (maxTest == null) {
            abilityRisk = 72.0;
        } else {
            abilityRisk = 100.0 - Math.min(maxTest, 100);
        }

        long signIns60 = activityRegistrationMapper.selectCount(
                new QueryWrapper<ActivityRegistration>()
                        .eq("student_id", sid)
                        .isNotNull("sign_in_time")
                        .ge("sign_in_time", t60));
        double appPart = Math.min(apps30 / 12.0 * 50.0, 50.0);
        double trainPart = Math.min(signIns60 / 3.0 * 50.0, 50.0);
        double behaviorScore = appPart + trainPart;
        double behaviorRisk = 100.0 - behaviorScore;

        EmploymentLedger ledger = employmentLedgerMapper.selectOne(
                new QueryWrapper<EmploymentLedger>().eq("student_id", sid).orderByDesc("updated_at").last("LIMIT 1"));
        double employmentStatusPart = 50.0;
        if (ledger != null && ledger.getEmploymentStatus() != null) {
            String es = ledger.getEmploymentStatus();
            if (es.contains("已就业") || es.contains("就业")) {
                employmentStatusPart = 0;
            } else if (es.contains("升学")) {
                employmentStatusPart = 20;
            }
        }
        double resultRisk = (100.0 - avgIv + employmentStatusPart) / 2.0;
        resultRisk = clamp(resultRisk, 0, 100);

        academicRisk = clamp(academicRisk, 0, 100);
        abilityRisk = clamp(abilityRisk, 0, 100);
        behaviorRisk = clamp(behaviorRisk, 0, 100);

        int composite = (int) Math.round(
                academicRisk * 0.2 + abilityRisk * 0.3 + behaviorRisk * 0.3 + resultRisk * 0.2);

        MockInterviewAppointment mock = mockInterviewAppointmentMapper.selectOne(
                new QueryWrapper<MockInterviewAppointment>()
                        .eq("student_id", sid)
                        .eq("status", "completed")
                        .isNotNull("performance_score")
                        .orderByDesc("updated_at")
                        .last("LIMIT 1"));
        Integer mockScore = mock != null ? mock.getPerformanceScore() : null;

        int maxExpected = maxExpectedSalaryMin(uid);
        boolean yellowSalary = maxExpected > Math.round(MARKET_REF_SALARY * 1.35);

        long accepted30 = Optional.ofNullable(applicationMapper.countAcceptedSince(uid, t30)).orElse(0L);
        double hitRate = apps30 > 0 ? accepted30 * 1.0 / apps30 : 0;
        boolean blueLowHit = apps30 >= 8 && hitRate < 0.2;
        long profTests = testResultRepository.countByUserId(uid.intValue());
        // 略放宽：便于在真实数据中命中「蓝色机会」档（仍低于红/黄规则优先级）
        boolean blueTraining = signIns60 >= 1 || profTests >= 2;

        boolean redZero = apps30 == 0;
        boolean redIvFail = postIvReject >= 5;
        boolean yellowMock = mockScore != null && mockScore < 60;

        Double modelProb = null;
        if (!skipModelCall) {
            try {
                EmploymentWarningResponse.PredictionResult pr =
                        employmentWarningService.evaluateStudent(employmentWarningService.buildRequestFromStudent(student));
                if (pr != null) {
                    modelProb = pr.getSuccessProbability();
                }
            } catch (Exception e) {
                log.debug("模型预测跳过: {}", e.getMessage());
            }
        }

        List<String> ruleTags = new ArrayList<>();
        if (redZero) {
            ruleTags.add("zero_apply_30d");
        }
        if (redIvFail) {
            ruleTags.add("interview_reject_ge_5");
        }
        if (yellowMock) {
            ruleTags.add("mock_lt_60");
        }
        if (yellowSalary) {
            ruleTags.add("expected_salary_high");
        }
        if (blueLowHit) {
            ruleTags.add("active_low_hit");
        }
        if (blueTraining) {
            ruleTags.add("training_active");
        }

        // 蓝色「机会」须在「综合分黄档」之前判断，否则 composite>=50 会吃掉绝大多数行，蓝档永远不出现
        String tier = null;
        String source = null;
        if (redZero || redIvFail) {
            tier = "red";
            source = "rule";
        } else if (yellowMock || yellowSalary) {
            tier = "yellow";
            source = "rule";
        } else if (modelProb != null && modelProb < 0.4) {
            tier = "yellow";
            source = "model";
        } else if (blueLowHit || blueTraining) {
            tier = "blue";
            source = "opportunity";
        } else if (composite >= 50) {
            tier = "yellow";
            source = "composite";
        }

        if (tier == null) {
            earlyWarningResultMapper.delete(
                    new QueryWrapper<EarlyWarningResult>()
                            .eq("student_id", sid)
                            .eq("warning_type", WARNING_TYPE_COCKPIT));
            return;
        }

        JSONObject analysis = new JSONObject();
        analysis.set("academicRisk", round1(academicRisk));
        analysis.set("abilityRisk", round1(abilityRisk));
        analysis.set("behaviorRisk", round1(behaviorRisk));
        analysis.set("resultRisk", round1(resultRisk));
        analysis.set("compositeScore", composite);
        analysis.set("applications30d", apps30);
        analysis.set("postInterviewRejections", postIvReject);
        analysis.set("avgInterviewScore", round1(avgIv));
        analysis.set("mockInterviewScore", mockScore);
        analysis.set("modelSuccessProbability", modelProb);
        analysis.set("maxExpectedSalaryMin", maxExpected > 0 ? maxExpected : null);
        analysis.set("hitRate30d", apps30 > 0 ? round1(hitRate * 100) : null);
        analysis.set("ruleTags", ruleTags);
        analysis.set("majorAvgComposite", null);
        analysis.set("lastEvaluatedAt", evaluatedAt.toString());

        String reason = buildTriggerReason(redZero, redIvFail, yellowMock, yellowSalary, blueLowHit, blueTraining, composite, modelProb);
        String script = buildSuggestedScript(tier, redZero, redIvFail, yellowMock, yellowSalary, mockScore, modelProb, composite);
        String level = "yellow".equals(tier) ? "medium" : ("red".equals(tier) ? "urgent" : "low");

        upsertCockpitRow(sid, tier, source, level, composite, reason, script, analysis.toString(), preserveHandleOnUpdate);
    }

    private int maxExpectedSalaryMin(Long userId) {
        List<resumer> list = resumerMapper.selectList(new QueryWrapper<resumer>().eq("user_id", userId));
        return list.stream()
                .map(resumer::getExpectedSalaryMin)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    private void upsertCockpitRow(Long studentId, String tier, String source, String level, int score,
                                  String reason, String script, String analysisJson, boolean preserveHandleOnUpdate) {
        EarlyWarningResult existing = earlyWarningResultMapper.selectOne(
                new QueryWrapper<EarlyWarningResult>()
                        .eq("student_id", studentId)
                        .eq("warning_type", WARNING_TYPE_COCKPIT));
        LocalDateTime now = LocalDateTime.now();
        if (existing != null) {
            existing.setAlertTier(tier);
            existing.setAlertSource(source);
            existing.setWarningLevel(level);
            existing.setWarningScore(score);
            existing.setTriggerReason(reason);
            existing.setSuggestedScript(script);
            existing.setAnalysisJson(analysisJson);
            existing.setDetectionTime(now);
            if (!preserveHandleOnUpdate) {
                existing.setHandleStatus("pending");
            }
            existing.setUpdatedAt(now);
            earlyWarningResultMapper.updateById(existing);
        } else {
            EarlyWarningResult w = new EarlyWarningResult();
            w.setStudentId(studentId);
            w.setWarningType(WARNING_TYPE_COCKPIT);
            w.setAlertTier(tier);
            w.setAlertSource(source);
            w.setWarningLevel(level);
            w.setWarningScore(score);
            w.setTriggerReason(reason);
            w.setSuggestedScript(script);
            w.setAnalysisJson(analysisJson);
            w.setDetectionTime(now);
            w.setHandleStatus("pending");
            w.setStudentViewed(false);
            w.setCreatedAt(now);
            w.setUpdatedAt(now);
            earlyWarningResultMapper.insert(w);
        }
    }

    private static String buildTriggerReason(boolean redZero, boolean redIvFail, boolean yellowMock, boolean yellowSalary,
                                             boolean blueLowHit, boolean blueTraining, int composite, Double modelProb) {
        List<String> parts = new ArrayList<>();
        if (redZero) {
            parts.add("近30天零投递(规则)");
        }
        if (redIvFail) {
            parts.add("面试后未通过次数较多(规则)");
        }
        if (yellowMock) {
            parts.add("模拟面试分偏低(规则)");
        }
        if (yellowSalary) {
            parts.add("期望薪资显著高于市场参考(规则)");
        }
        if (blueLowHit) {
            parts.add("投递活跃但命中率低(机会)");
        }
        if (blueTraining) {
            parts.add("实训/活动参与积极(机会)");
        }
        if (parts.isEmpty()) {
            parts.add("综合风险分 " + composite);
            if (modelProb != null) {
                parts.add(String.format("模型就业概率 %.0f%%", modelProb * 100));
            }
        }
        return String.join(" + ", parts);
    }

    private static String buildSuggestedScript(String tier, boolean redZero, boolean redIvFail,
                                               boolean yellowMock, boolean yellowSalary,
                                               Integer mockScore, Double modelProb, int composite) {
        if ("red".equals(tier)) {
            if (redZero) {
                return "【需立即面谈】同学你好，老师注意到你近期没有投递记录。是否遇到岗位信息获取困难或信心不足？本周方便来办公室面谈一次吗？";
            }
            if (redIvFail) {
                return "【需立即面谈】你近期多次进入面试但未通过，我们一对一梳理面试表现与岗位匹配度，约个时间面谈好吗？";
            }
            return "【需立即面谈】你的求职状态需要重点关注，老师希望本周与你面谈，一起制定改进计划。";
        }
        if ("yellow".equals(tier)) {
            if (yellowMock && mockScore != null) {
                return "【辅导建议】模拟面试得分 " + mockScore + "，建议参加 AI 面试专项训练或预约一对一模拟面试，提升表达与答题结构。";
            }
            if (yellowSalary) {
                return "【岗位匹配】期望薪资与市场差距较大，建议做一次「岗位匹配度分析」，调整期望区间与目标岗位层级。";
            }
            if (modelProb != null && modelProb < 0.4) {
                return "【模型提示】综合预测显示就业成功概率偏低，建议加强投递量并完善简历关键词，可预约简历指导。";
            }
            return "【关注】综合风险分 " + composite + "（中高风险区间），建议参加就业指导活动并完成至少一次模拟面试。";
        }
        return "【发展机会】投递很积极！建议参加高质量就业讲座或简历精修服务，提高命中率与面试通过率。";
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
