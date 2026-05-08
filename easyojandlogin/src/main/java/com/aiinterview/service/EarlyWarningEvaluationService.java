package com.aiinterview.service;

import com.aiinterview.mapper.EarlyWarningResultMapper;
import com.aiinterview.mapper.MockInterviewAppointmentMapper;
import com.aiinterview.mapper.ResumeMapper;
import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.model.entity.resume.Resume;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.model.entity.teacher.EarlyWarningResult;
import com.aiinterview.model.entity.teacher.MockInterviewAppointment;
import com.aiinterview.repository.professionalTest.TestResultRepository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * 多类型预警统一评估：就业（XGBoost 模型）+ 技能 / 简历 / 面试（平台行为数据规则，与前端四类预警一致）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EarlyWarningEvaluationService {

    private final EmploymentWarningService employmentWarningService;
    private final EarlyWarningResultMapper earlyWarningResultMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final TestResultRepository testResultRepository;
    private final ResumeMapper resumeMapper;
    private final MockInterviewAppointmentMapper mockInterviewAppointmentMapper;
    private final EmploymentCockpitScanService employmentCockpitScanService;

    /**
     * 按预警类型重算；类型为空则对该学生执行全套通道（技能/简历/面试/就业模型/驾驶舱）。
     * 未知类型同样走全套，避免库中出现新枚举时无法重算。
     */
    public void reEvaluateStudent(Long studentId, String warningType) {
        StudentProfile student = studentProfileMapper.selectById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("学生不存在");
        }
        String normalized = normalizeWarningType(warningType);
        if (normalized.isEmpty()) {
            reEvaluateAllChannels(student);
            return;
        }
        switch (normalized) {
            case "employment":
                employmentWarningService.evaluateAndSaveWarning(employmentWarningService.buildRequestFromStudent(student));
                break;
            case "skill":
                evaluateSkillAndSave(student);
                break;
            case "resume":
                evaluateResumeAndSave(student);
                break;
            case "interview":
                evaluateInterviewAndSave(student);
                break;
            case "cockpit":
                employmentCockpitScanService.evaluateOne(studentId);
                break;
            default:
                log.info("预警类型 [{}] 未单独定义规则，为该学生执行全套预警重算", normalized);
                reEvaluateAllChannels(student);
                break;
        }
    }

    private static String normalizeWarningType(String warningType) {
        if (warningType == null) {
            return "";
        }
        return warningType.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 技能 / 简历 / 面试 / 就业模型 / 驾驶舱 各跑一次；单步失败不影响其余步骤。
     */
    private void reEvaluateAllChannels(StudentProfile student) {
        Long sid = student.getStudentId();
        try {
            evaluateSkillAndSave(student);
        } catch (Exception e) {
            log.warn("重算技能预警失败 studentId={}: {}", sid, e.getMessage());
        }
        try {
            evaluateResumeAndSave(student);
        } catch (Exception e) {
            log.warn("重算简历预警失败 studentId={}: {}", sid, e.getMessage());
        }
        try {
            evaluateInterviewAndSave(student);
        } catch (Exception e) {
            log.warn("重算面试预警失败 studentId={}: {}", sid, e.getMessage());
        }
        try {
            employmentWarningService.evaluateAndSaveWarning(employmentWarningService.buildRequestFromStudent(student));
        } catch (Exception e) {
            log.warn("重算就业模型预警失败 studentId={}: {}", sid, e.getMessage());
        }
        try {
            employmentCockpitScanService.evaluateOne(sid);
        } catch (Exception e) {
            log.warn("重算驾驶舱预警失败 studentId={}: {}", sid, e.getMessage());
        }
    }

    /**
     * 扫描全校学生，四类预警各生成/更新一条记录（与辅导员端看板类型一致）
     */
    public void scanAllStudentsAllWarningTypes() {
        List<StudentProfile> allStudents = studentProfileMapper.selectList(null);
        for (StudentProfile student : allStudents) {
            try {
                evaluateSkillAndSave(student);
                evaluateResumeAndSave(student);
                evaluateInterviewAndSave(student);
                employmentWarningService.evaluateAndSaveWarning(employmentWarningService.buildRequestFromStudent(student));
            } catch (Exception e) {
                log.error("学生 {} 多类型预警扫描失败: {}", student.getStudentId(), e.getMessage());
            }
        }
        log.info("多类型预警扫描完成，共处理 {} 名学生", allStudents.size());
    }

    private void evaluateSkillAndSave(StudentProfile student) {
        Long sid = student.getStudentId();
        if (student.getUserId() == null) {
            upsertWarning(sid, "skill", "high", 80,
                    "技能预警：学生档案未关联平台用户，无法读取职业能力测评数据，请核对账号绑定");
            return;
        }
        int uid = student.getUserId().intValue();
        Integer maxScore = testResultRepository.findMaxScoreByUserId(uid);
        long testCount = testResultRepository.countByUserId(uid);
        if (testCount == 0 || maxScore == null) {
            upsertWarning(sid, "skill", "high", 85,
                    "技能预警：未完成职业能力测评或尚无成绩，建议尽快完成平台专业测评以识别能力短板");
            return;
        }
        if (maxScore < 45) {
            upsertWarning(sid, "skill", "urgent", 92,
                    "技能预警：职业能力测评最高分低于45分，技能储备不足，建议安排专项辅导与练习");
        } else if (maxScore < 60) {
            upsertWarning(sid, "skill", "high", 78,
                    "技能预警：职业能力测评表现偏弱（最高分" + maxScore + "），建议加强岗位相关技能训练");
        } else if (maxScore < 75) {
            upsertWarning(sid, "skill", "medium", 55,
                    "技能预警：职业能力测评中等（最高分" + maxScore + "），仍有提升空间");
        } else {
            upsertWarning(sid, "skill", "low", 25,
                    "技能预警：职业能力测评表现良好（最高分" + maxScore + "），请保持学习节奏");
        }
    }

    private void evaluateResumeAndSave(StudentProfile student) {
        Long sid = student.getStudentId();
        if (student.getUserId() == null) {
            upsertWarning(sid, "resume", "high", 82,
                    "简历预警：未关联用户账号，无法检测简历上传情况");
            return;
        }
        long resumeCount = resumeMapper.selectCount(new QueryWrapper<Resume>()
                .eq("user_id", student.getUserId())
                .eq("is_deleted", 0));
        int profileCompletion = student.getProfileCompletion() != null ? student.getProfileCompletion() : 0;
        Integer resumeFlag = student.getResumeCompletionStatus();

        if (resumeCount == 0) {
            upsertWarning(sid, "resume", "high", 88,
                    "简历预警：尚未在平台上传简历，影响岗位投递与匹配，请尽快完善");
            return;
        }
        if (profileCompletion < 50 || (resumeFlag != null && resumeFlag == 0)) {
            upsertWarning(sid, "resume", "medium", 60,
                    "简历预警：档案完善度较低（" + profileCompletion + "%）或简历标记为未完善，建议补充经历与求职意向");
        } else if (profileCompletion < 80) {
            upsertWarning(sid, "resume", "low", 35,
                    "简历预警：档案与简历基本完整，仍可优化亮点描述与岗位针对性");
        } else {
            upsertWarning(sid, "resume", "low", 18,
                    "简历预警：简历与档案完善度良好，建议定期根据目标岗位微调");
        }
    }

    private void evaluateInterviewAndSave(StudentProfile student) {
        Long sid = student.getStudentId();
        MockInterviewAppointment latest = mockInterviewAppointmentMapper.selectOne(
                new QueryWrapper<MockInterviewAppointment>()
                        .eq("student_id", sid)
                        .eq("status", "completed")
                        .isNotNull("performance_score")
                        .orderByDesc("updated_at")
                        .last("LIMIT 1"));
        if (latest == null || latest.getPerformanceScore() == null) {
            upsertWarning(sid, "interview", "medium", 58,
                    "面试预警：暂无已完成的模拟面试评分记录，建议预约并完成一次模拟面试以评估表达能力");
            return;
        }
        int score = latest.getPerformanceScore();
        if (score < 50) {
            upsertWarning(sid, "interview", "high", 86,
                    "面试预警：最近一次模拟面试成绩偏低（" + score + "分），建议加强表达与答题训练");
        } else if (score < 70) {
            upsertWarning(sid, "interview", "medium", 62,
                    "面试预警：模拟面试成绩中等（" + score + "分），存在可改进空间");
        } else {
            upsertWarning(sid, "interview", "low", 28,
                    "面试预警：模拟面试表现良好（" + score + "分），可继续积累实战机会");
        }
    }

    private void upsertWarning(Long studentId, String warningType, String level, int score, String reason) {
        EarlyWarningResult existing = earlyWarningResultMapper.selectOne(
                new QueryWrapper<EarlyWarningResult>()
                        .eq("student_id", studentId)
                        .eq("warning_type", warningType));
        LocalDateTime now = LocalDateTime.now();
        if (existing != null) {
            existing.setWarningLevel(level);
            existing.setWarningScore(score);
            existing.setTriggerReason(reason);
            existing.setDetectionTime(now);
            existing.setHandleStatus("pending");
            existing.setUpdatedAt(now);
            earlyWarningResultMapper.updateById(existing);
        } else {
            EarlyWarningResult w = new EarlyWarningResult();
            w.setStudentId(studentId);
            w.setWarningType(warningType);
            w.setWarningLevel(level);
            w.setWarningScore(score);
            w.setTriggerReason(reason);
            w.setDetectionTime(now);
            w.setHandleStatus("pending");
            w.setStudentViewed(false);
            w.setCreatedAt(now);
            w.setUpdatedAt(now);
            earlyWarningResultMapper.insert(w);
        }
    }
    /**
     * 增量扫描：只处理最近24小时有更新的学生
     */
    public void scanRecentActiveStudents() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        List<StudentProfile> activeStudents = studentProfileMapper.selectList(
                new QueryWrapper<StudentProfile>()
                        .ge("updated_at", yesterday)
        );

        log.info("增量扫描开始，活跃学生数: {}", activeStudents.size());

        for (StudentProfile student : activeStudents) {
            try {
                employmentWarningService.evaluateAndSaveWarning(
                        employmentWarningService.buildRequestFromStudent(student)
                );
            } catch (Exception e) {
                log.error("增量评估失败: studentId={}", student.getStudentId(), e);
            }
        }

        log.info("增量扫描完成");
    }
}
