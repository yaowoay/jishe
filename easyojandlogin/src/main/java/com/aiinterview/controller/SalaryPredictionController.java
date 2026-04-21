package com.aiinterview.controller;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.model.entity.SalaryPredictionResult;
import com.aiinterview.service.SalaryPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 薪资预测Controller
 */
@RestController
@RequestMapping("/api/salary-prediction")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SalaryPredictionController {

    @Autowired
    private SalaryPredictionService salaryPredictionService;

    /**
     * 查询用户最新预测结果
     */
    @GetMapping("/user/{userId}/latest")
    public BaseResponse<SalaryPredictionResult> getLatestPrediction(
            @PathVariable Integer userId) {
        try {
            SalaryPredictionResult result = salaryPredictionService.getLatestPrediction(userId);
            return ResultUtils.success(result);
        } catch (Exception e) {
            return ResultUtils.<SalaryPredictionResult>error(500, "获取最新预测结果失败");
        }
    }

    /**
     * 查询用户所有预测结果
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<List<SalaryPredictionResult>> getUserPredictions(
            @PathVariable Integer userId) {
        try {
            List<SalaryPredictionResult> results = salaryPredictionService.getUserPredictions(userId);
            return ResultUtils.success(results);
        } catch (Exception e) {
            return ResultUtils.<List<SalaryPredictionResult>>error(500, "查询用户预测结果失败");
        }
    }

    /**
     * 分页查询用户预测结果
     */
    @GetMapping("/user/{userId}/page")
    public BaseResponse<List<SalaryPredictionResult>> getUserPredictionsByPage(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<SalaryPredictionResult> results = salaryPredictionService.getUserPredictionsByPage(userId, pageNum, pageSize);
            return ResultUtils.success(results);
        } catch (Exception e) {
            return ResultUtils.<List<SalaryPredictionResult>>error(500, "分页查询预测结果失败");
        }
    }

    /**
     * 根据预测ID查询
     */
    @GetMapping("/{predId}")
    public BaseResponse<SalaryPredictionResult> getPredictionById(
            @PathVariable Integer predId) {
        try {
            SalaryPredictionResult result = salaryPredictionService.getPredictionById(predId);
            return ResultUtils.success(result);
        } catch (Exception e) {
            return ResultUtils.error(500, "查询预测结果失败");
        }
    }

    /**
     * 添加预测结果
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addPrediction(
            @RequestBody SalaryPredictionResult result) {
        try {
            boolean success = salaryPredictionService.addPrediction(result);
            return ResultUtils.success(success);
        } catch (Exception e) {
            return ResultUtils.error(500, "添加预测结果失败");
        }
    }

    /**
     * 更新预测结果
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> updatePrediction(
            @RequestBody SalaryPredictionResult result) {
        try {
            boolean success = salaryPredictionService.updatePrediction(result);
            return ResultUtils.success(success);
        } catch (Exception e) {
            return ResultUtils.error(500, "更新预测结果失败");
        }
    }

    /**
     * 删除预测结果
     */
    @DeleteMapping("/{predId}")
    public BaseResponse<Boolean> deletePrediction(
            @PathVariable Integer predId) {
        try {
            boolean success = salaryPredictionService.deletePrediction(predId);
            return ResultUtils.success(success);
        } catch (Exception e) {
            return ResultUtils.error(500, "删除预测结果失败");
        }
    }

    /**
     * 获取用户预测总数
     */
    @GetMapping("/user/{userId}/count")
    public BaseResponse<Long> getPredictionCount(
            @PathVariable Integer userId) {
        try {
            long count = salaryPredictionService.getPredictionCount(userId);
            return ResultUtils.success(count);
        } catch (Exception e) {
            return ResultUtils.error(500, "获取预测总数失败");
        }
    }
}

