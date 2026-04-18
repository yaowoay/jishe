package com.aiinterview.service;

import com.aiinterview.model.entity.SalaryPredictionResult;
import java.util.List;

/**
 * 薪资预测Service接口
 */
public interface SalaryPredictionService {

    /**
     * 添加预测结果
     */
    boolean addPrediction(SalaryPredictionResult result);

    /**
     * 查询用户最新预测结果
     */
    SalaryPredictionResult getLatestPrediction(Integer userId);

    /**
     * 查询用户所有预测结果
     */
    List<SalaryPredictionResult> getUserPredictions(Integer userId);

    /**
     * 分页查询用户预测结果
     */
    List<SalaryPredictionResult> getUserPredictionsByPage(Integer userId, int pageNum, int pageSize);

    /**
     * 获取用户预测总数
     */
    long getPredictionCount(Integer userId);

    /**
     * 根据预测ID查询
     */
    SalaryPredictionResult getPredictionById(Integer predId);

    /**
     * 删除预测结果
     */
    boolean deletePrediction(Integer predId);

    /**
     * 更新预测结果
     */
    boolean updatePrediction(SalaryPredictionResult result);
}
