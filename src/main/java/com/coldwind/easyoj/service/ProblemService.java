package com.coldwind.easyoj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coldwind.easyoj.model.entity.Problem;
import com.coldwind.easyoj.model.vo.ProblemVO;

import java.util.List;

/**
 * 题目服务接口
 */
public interface ProblemService extends IService<Problem> {
    
    /**
     * 获取题目列表
     */
    List<Problem> getProblemList();
    
    /**
     * 获取题目详情
     */
    ProblemVO getProblemDetail(Integer problemId);
    
    /**
     * 收藏题目
     */
    void favoriteProblem(Integer problemId);
    
    /**
     * 取消收藏题目
     */
    void unfavoriteProblem(Integer problemId);
    
    /**
     * 获取代码模板
     */
    String getCodeTemplate(Integer problemId, String language);
    
    /**
     * 更新题目状态
     */
    void updateProblemStatus(Integer problemId, String status);
}