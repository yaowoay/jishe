// com/aiinterview/service/EmploymentWarningService.java
package com.aiinterview.service;

import cn.hutool.json.JSONUtil;
import com.aiinterview.config.EmploymentWarningConfig;
import com.aiinterview.mapper.EarlyWarningResultMapper;
import com.aiinterview.mapper.ResumerMapper;
import com.aiinterview.model.entity.student.EmploymentWarningRequest;
import com.aiinterview.model.entity.student.EmploymentWarningResponse;
import com.aiinterview.model.entity.teacher.EarlyWarningResult;
import com.aiinterview.service.student.StudentWarningService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
// 在现有 import 后添加
import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.model.entity.resumer;
import java.math.BigDecimal;
@Slf4j
@Service
public class EmploymentWarningService {

    @Autowired
    private EmploymentWarningConfig config;

    private OkHttpClient httpClient;
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Autowired
    private EarlyWarningResultMapper earlyWarningResultMapper;

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Autowired
    private ResumerMapper resumerMapper;

    @Autowired
    private StudentWarningService studentWarningService;
    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 扫描所有学生，生成就业预警
     */
    public void scanAllStudentsAndGenerateWarnings() {
        List<StudentProfile> allStudents = studentProfileMapper.selectList(null);

        int warningCount = 0;
        for (StudentProfile student : allStudents) {
            try {
                evaluateAndSaveWarning(buildRequestFromStudent(student));
                warningCount++;
            } catch (Exception e) {
                log.error("学生 {} 评估失败: {}", student.getStudentId(), e.getMessage());
            }
        }
        log.info("扫描完成：共{}人，生成{}条预警", allStudents.size(), warningCount);
    }

    /**
     * 由学生档案构造就业模型入参（与 XGBoost 特征一致，缺失项使用文档约定默认值）
     */
    public EmploymentWarningRequest buildRequestFromStudent(StudentProfile student) {
        EmploymentWarningRequest request = new EmploymentWarningRequest();
        request.setStudentId(String.valueOf(student.getStudentId()));
        request.setName(student.getRealName());
        request.setEducation(student.getEducationLevel() != null ? student.getEducationLevel() : "本科");
        request.setGender("male".equals(student.getGender()) ? "男" : "女");
        request.setHasInternship(hasText(student.getInternshipExperience()) ? "是" : "否");
        request.setMajor(student.getMajor() != null ? student.getMajor() : "未知");
        enrichRequestFromProfileAndResume(student, request);
        return request;
    }

    /**
     * 用档案完善度、简历期望城市/行业/薪资等填充模型特征，避免全员相同默认值导致预测雷同。
     */
    private void enrichRequestFromProfileAndResume(StudentProfile student, EmploymentWarningRequest request) {
        int pc = student.getProfileCompletion() != null ? student.getProfileCompletion() : 0;
        double satisfaction = 3.0 + (Math.min(Math.max(pc, 0), 100) / 100.0) * 6.5;
        request.setSatisfaction(Math.round(satisfaction * 10) / 10.0);

        if (student.getUserId() == null) {
            request.setExpectedSalary(BigDecimal.valueOf(6000));
            request.setWorkCity("待完善");
            request.setIndustry("待完善");
            return;
        }

        List<resumer> resumes = resumerMapper.selectList(
                new QueryWrapper<resumer>().eq("user_id", student.getUserId()));
        int bestMinSalary = 0;
        String workCity = null;
        String industry = null;
        for (resumer r : resumes) {
            if (r.getExpectedSalaryMin() != null && r.getExpectedSalaryMin() > bestMinSalary) {
                bestMinSalary = r.getExpectedSalaryMin();
            }
            if (workCity == null && hasText(r.getExpectedCity())) {
                workCity = r.getExpectedCity().trim();
            }
            if (industry == null && hasText(r.getExpectedIndustry())) {
                industry = r.getExpectedIndustry().trim();
            }
        }

        if (bestMinSalary > 0) {
            request.setExpectedSalary(BigDecimal.valueOf(bestMinSalary));
        } else {
            request.setExpectedSalary(BigDecimal.valueOf(6000));
        }
        if (workCity != null) {
            request.setWorkCity(workCity);
        } else if (hasText(student.getCollege())) {
            request.setWorkCity(student.getCollege().trim());
        } else {
            request.setWorkCity("待完善");
        }
        if (industry != null) {
            request.setIndustry(industry);
        } else {
            request.setIndustry("待完善");
        }
    }

    /** Java 8 兼容：等价于 String.isBlank()（JDK 11+） */
    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
    /**
     * 单个学生就业风险评估
     */
    public EmploymentWarningResponse.PredictionResult evaluateStudent(
            EmploymentWarningRequest request) {

        List<EmploymentWarningResponse.PredictionResult> results =
                batchEvaluate(Arrays.asList(request));

        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 批量学生就业风险评估
     */
    public List<EmploymentWarningResponse.PredictionResult> batchEvaluate(
            List<EmploymentWarningRequest> requests) {

        // 转换为Python服务需要的格式
        List<Map<String, Object>> pythonData = requests.stream()
                .map(EmploymentWarningRequest::toPythonMap)
                .collect(Collectors.toList());

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("students", pythonData);

        String json = JSONUtil.toJsonStr(requestBody);

        // 使用OkHttp发送请求
        Request httpRequest = new Request.Builder()
                .url(config.getUrl() + "/predict")
                .post(RequestBody.create(json, JSON))
                .build();

        // 重试机制
        for (int i = 0; i <= config.getRetryCount(); i++) {
            try {
                Response response = httpClient.newCall(httpRequest).execute();
                String responseBody = response.body().string();

                EmploymentWarningResponse warningResponse =
                        JSONUtil.toBean(responseBody, EmploymentWarningResponse.class);

                if (warningResponse.isSuccess()) {
                    return warningResponse.getPredictions();
                } else {
                    throw new RuntimeException("模型预测失败: " + warningResponse.getError());
                }

            } catch (IOException e) {
                if (i == config.getRetryCount()) {
                    throw new RuntimeException("调用模型服务失败，已重试" +
                            config.getRetryCount() + "次", e);
                }
                try {
                    Thread.sleep(config.getRetryDelay());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * 健康检查
     */
    public boolean healthCheck() {
        try {
            Request request = new Request.Builder()
                    .url(config.getUrl() + "/health")
                    .get()
                    .build();

            Response response = httpClient.newCall(request).execute();
            return response.isSuccessful();
        } catch (Exception e) {
            return false;
        }

    }


    /**
     * 对单个学生进行就业风险评估，并存入预警表
     */
    public void evaluateAndSaveWarning(EmploymentWarningRequest request) {
        EmploymentWarningResponse.PredictionResult result = evaluateStudent(request);
        if (result == null) {
            throw new RuntimeException("模型未返回预测结果");
        }
        String warningLevel = convertRiskLevel(result.getRiskLevel());
        Long studentId = Long.valueOf(request.getStudentId());

        // 先查该学生是否已有就业预警
        EarlyWarningResult existing = earlyWarningResultMapper.selectOne(
                new QueryWrapper<EarlyWarningResult>()
                        .eq("student_id", studentId)
                        .eq("warning_type", "employment")
        );

        if (existing != null) {
            // 已有记录，更新
            existing.setWarningLevel(warningLevel);
            existing.setWarningScore(result.getRiskScore().intValue());
            existing.setTriggerReason("就业风险评估：" + result.getRecommendedAction() +
                    "，就业成功概率：" + String.format("%.1f", result.getSuccessProbability() * 100) + "%");
            existing.setDetectionTime(LocalDateTime.now());
            existing.setHandleStatus("pending");
            existing.setUpdatedAt(LocalDateTime.now());
            earlyWarningResultMapper.updateById(existing);
        } else {
            // 没有记录，新增
            EarlyWarningResult warning = new EarlyWarningResult();
            warning.setStudentId(studentId);
            warning.setWarningType("employment");
            warning.setWarningLevel(warningLevel);
            warning.setWarningScore(result.getRiskScore().intValue());
            warning.setTriggerReason("就业风险评估：" + result.getRecommendedAction() +
                    "，就业成功概率：" + String.format("%.1f", result.getSuccessProbability() * 100) + "%");
            warning.setDetectionTime(LocalDateTime.now());
            warning.setHandleStatus("pending");
            warning.setStudentViewed(false);
            warning.setCreatedAt(LocalDateTime.now());
            warning.setUpdatedAt(LocalDateTime.now());
            earlyWarningResultMapper.insert(warning);
        }
    }

    /**
     * 批量评估并入库
     */
    public void batchEvaluateAndSave(List<EmploymentWarningRequest> requests) {
        for (EmploymentWarningRequest request : requests) {
            try {
                evaluateAndSaveWarning(request);
            } catch (Exception e) {
                log.error("学生 {} 就业预警评估失败: {}", request.getStudentId(), e.getMessage());
            }
        }
    }

    /**
     * 转换风险等级：高风险→urgent/high, 中风险→medium, 低风险→low
     */
    private String convertRiskLevel(String riskLevel) {
        switch (riskLevel) {
            case "高风险":
                return "high";
            case "中风险":
                return "medium";
            case "低风险":
                return "low";
            default:
                return "low";
        }
    }


}