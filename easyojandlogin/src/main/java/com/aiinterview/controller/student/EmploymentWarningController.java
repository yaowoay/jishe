package com.aiinterview.controller.student;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.mapper.StudentProfileMapper;
import com.aiinterview.model.entity.student.EmploymentWarningRequest;
import com.aiinterview.model.entity.student.EmploymentWarningResponse;
import com.aiinterview.model.entity.student.StudentProfile;
import com.aiinterview.service.EarlyWarningEvaluationService;
import com.aiinterview.service.EmploymentCockpitScanService;
import com.aiinterview.service.EmploymentWarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/employment-warning")
public class EmploymentWarningController {

    @Autowired
    private EmploymentWarningService warningService;

    @Autowired
    private EarlyWarningEvaluationService evaluationService;
    @Autowired
    private EarlyWarningEvaluationService earlyWarningEvaluationService;

    @Autowired
    private EmploymentCockpitScanService employmentCockpitScanService;

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @PostMapping("/evaluate")
    public BaseResponse<?> evaluateStudent(@RequestBody EmploymentWarningRequest request) {
        try {
            EmploymentWarningResponse.PredictionResult result =
                    warningService.evaluateStudent(request);
            if (result == null) {
                return ResultUtils.error(500, "模型未返回预测结果");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("studentId", request.getStudentId());
            response.put("riskLevel", result.getRiskLevel());
            response.put("riskScore", result.getRiskScore());
            response.put("successProbability", result.getSuccessProbability());
            response.put("needWarning", result.isNeedWarning());
            response.put("recommendedAction", result.getRecommendedAction());

            return ResultUtils.success(response);
        } catch (Exception e) {
            return ResultUtils.error(500, "就业风险评估失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch-evaluate")
    public BaseResponse<?> batchEvaluate(@RequestBody List<EmploymentWarningRequest> requests) {
        try {
            List<EmploymentWarningResponse.PredictionResult> results =
                    warningService.batchEvaluate(requests);

            return ResultUtils.success(results);
        } catch (Exception e) {
            return ResultUtils.error(500, "批量评估失败: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public BaseResponse<?> healthCheck() {
        boolean available = warningService.healthCheck();
        if (available) {
            return ResultUtils.success("模型服务正常");
        } else {
            return ResultUtils.error(500, "模型服务不可用");
        }
    }

    @PostMapping("/generate-warning")
    public BaseResponse<?> generateWarning(@RequestBody EmploymentWarningRequest request) {
        try {
            warningService.evaluateAndSaveWarning(request);
            return ResultUtils.success("就业预警已生成");
        } catch (Exception e) {
            return ResultUtils.error(500, "生成预警失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch-generate-warning")
    public BaseResponse<?> batchGenerateWarning(@RequestBody List<EmploymentWarningRequest> requests) {
        try {
            warningService.batchEvaluateAndSave(requests);
            return ResultUtils.success("批量就业预警已生成");
        } catch (Exception e) {
            return ResultUtils.error(500, "批量生成失败: " + e.getMessage());
        }
    }

    /**
     * 扫描全部学生：就业 + 技能 + 简历 + 面试 四类预警均生成/更新
     */
    /**
     * 增量扫描（替换原来的全量扫描）
     */
    @PostMapping("/scan-active")
    public BaseResponse scanActive() {
        try {
            evaluationService.scanRecentActiveStudents();
            return ResultUtils.success("增量扫描完成");
        } catch (Exception e) {
            return ResultUtils.error(500, "扫描失败: " + e.getMessage());
        }
    }

    @PostMapping("/evaluate-and-save")
    public BaseResponse<?> evaluateAndSave(@RequestBody EmploymentWarningRequest request) {
        warningService.evaluateAndSaveWarning(request);
        return ResultUtils.success("评估完成");
    }

    /**
     * 辅导员按学生重算预警。{@code warningType} 传 cockpit / employment / skill / resume / interview 则只跑对应通道；
     * 不传或空字符串则对该学生<strong>全套</strong>重算（五类依次执行）；未知类型同样走全套，避免库里新增枚举无法重算。
     */
    @PostMapping("/re-evaluate")
    public ApiResponse<Map<String, Object>> reEvaluate(@RequestBody Map<String, Object> params) {
        try {
            Long studentId = parseLong(params.get("studentId"));
            if (studentId == null) {
                return ApiResponse.error("缺少 studentId", 400);
            }

            Object wtObj = params.get("warningType");
            String warningType = wtObj != null ? wtObj.toString().trim() : null;

            StudentProfile student = studentProfileMapper.selectById(studentId);
            if (student == null) {
                return ApiResponse.error("学生不存在", 404);
            }

            earlyWarningEvaluationService.reEvaluateStudent(studentId, warningType);

            Map<String, Object> response = new HashMap<>();
            response.put("studentId", studentId);
            response.put("studentName", student.getRealName());
            response.put("warningType", warningType);
            return ApiResponse.success("评估完成", response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage(), 400);
        } catch (Exception e) {
            log.error("re-evaluate failed", e);
            return ApiResponse.error("评估失败: " + e.getMessage(), 500);
        }
    }

    private static Long parseLong(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        try {
            return Long.parseLong(v.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
