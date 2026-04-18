package com.aiinterview.service.impl;

import com.aiinterview.model.entity.SalaryPredictionResult;
import com.aiinterview.mapper.SalaryPredictionResultMapper;
import com.aiinterview.service.SalaryPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 薪资预测Service实现类
 */
@Service
public class SalaryPredictionServiceImpl implements SalaryPredictionService {

    @Autowired
    private SalaryPredictionResultMapper salaryPredictionResultMapper;

    @Override
    public boolean addPrediction(SalaryPredictionResult result) {
        return salaryPredictionResultMapper.insert(result) > 0;
    }

    @Override
    public SalaryPredictionResult getLatestPrediction(Integer userId) {
        return salaryPredictionResultMapper.selectLatestByUserId(userId);
    }

    @Override
    public List<SalaryPredictionResult> getUserPredictions(Integer userId) {
        return salaryPredictionResultMapper.selectByUserId(userId);
    }

    @Override
    public List<SalaryPredictionResult> getUserPredictionsByPage(Integer userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return salaryPredictionResultMapper.selectPageByUserId(userId, offset, pageSize);
    }

    @Override
    public long getPredictionCount(Integer userId) {
        return salaryPredictionResultMapper.countByUserId(userId);
    }

    @Override
    public SalaryPredictionResult getPredictionById(Integer predId) {
        return salaryPredictionResultMapper.selectById(predId);
    }

    @Override
    public boolean deletePrediction(Integer predId) {
        return salaryPredictionResultMapper.delete(predId) > 0;
    }

    @Override
    public boolean updatePrediction(SalaryPredictionResult result) {
        return salaryPredictionResultMapper.update(result) > 0;
    }
}
