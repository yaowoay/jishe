package com.coldwind.easyoj.service.impl;

import com.coldwind.easyoj.mapper.ExampleMapper;
import com.coldwind.easyoj.mapper.SolutionMapper;
import com.coldwind.easyoj.model.dto.request.ProblemRunRequest;
import com.coldwind.easyoj.model.dto.request.ProblemSubmitRequest;
import com.coldwind.easyoj.model.dto.response.ProblemRunResponse;
import com.coldwind.easyoj.model.dto.response.ProblemSubmitResponse;
import com.coldwind.easyoj.model.entity.Example;
import com.coldwind.easyoj.model.entity.Solution;
import com.coldwind.easyoj.service.OjService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * OJ判题服务实现类
 */
@Slf4j
@Service
public class OjServiceImpl implements OjService {
    
    @Resource
    private ExampleMapper exampleMapper;
    
    @Resource
    private SolutionMapper solutionMapper;
    
    @Override
    public ProblemRunResponse runCode(ProblemRunRequest request) {
        ProblemRunResponse response = new ProblemRunResponse();
        
        try {
            // 这里应该调用代码沙箱执行代码
            // 由于你提到判题不是重点，这里简单模拟运行结果
            String output = simulateCodeExecution(request.getCode(), request.getLanguage());
            
            response.setSuccess(true);
            response.setOutput(output);
            response.setTime(100L); // 模拟运行时间
            response.setMemory(1024L); // 模拟内存使用
            
        } catch (Exception e) {
            log.error("代码运行失败", e);
            response.setSuccess(false);
            response.setError("代码运行失败: " + e.getMessage());
        }
        
        return response;
    }
    
    @Override
    public ProblemSubmitResponse submitCode(ProblemSubmitRequest request) {
        ProblemSubmitResponse response = new ProblemSubmitResponse();
        
        try {
            // 获取题目的标准答案
            Solution standardSolution = getStandardSolution(request.getProblemId(), request.getLanguage());
            if (standardSolution == null) {
                response.setResult("未通过");
                response.setMessage("未找到标准答案");
                return response;
            }
            
            // 获取测试用例
            List<Example> examples = exampleMapper.selectByProblemId(request.getProblemId());
            if (examples.isEmpty()) {
                response.setResult("未通过");
                response.setMessage("未找到测试用例");
                return response;
            }
            
            // 简单的代码对比判题（实际应该运行代码进行对比）
            boolean isCorrect = compareCode(request.getCode(), standardSolution.getCode());
            
            if (isCorrect) {
                response.setResult("通过");
                response.setMessage("恭喜！代码通过所有测试用例");
                response.setPassedCases(examples.size());
                response.setTotalCases(examples.size());
            } else {
                response.setResult("未通过");
                response.setMessage("代码未通过测试，请检查逻辑");
                response.setPassedCases(0);
                response.setTotalCases(examples.size());
            }
            
            response.setTime(150L); // 模拟判题时间
            response.setMemory(2048L); // 模拟内存使用
            
        } catch (Exception e) {
            log.error("代码判题失败", e);
            response.setResult("未通过");
            response.setMessage("判题失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 获取标准答案
     */
    private Solution getStandardSolution(Integer problemId, String language) {
        Integer languageId = getLanguageId(language);
        if (languageId == null) {
            return null;
        }
        return solutionMapper.selectByProblemAndLanguage(problemId, languageId);
    }
    
    /**
     * 根据语言名称获取语言ID
     */
    private Integer getLanguageId(String language) {
        switch (language.toLowerCase()) {
            case "python":
                return 1;
            case "java":
                return 2;
            case "c":
                return 3;
            default:
                return null;
        }
    }
    
    /**
     * 模拟代码执行
     */
    private String simulateCodeExecution(String code, String language) {
        // 这里应该调用真实的代码执行环境
        // 现在简单返回一些模拟输出
        if (code.contains("Hello World") || code.contains("print")) {
            return "Hello World";
        }
        return "代码执行完成";
    }
    
    /**
     * 简单的代码对比（实际应该运行代码进行对比）
     */
    private boolean compareCode(String userCode, String standardCode) {
        // 这里应该运行用户代码和标准代码，比较输出结果
        // 现在简单比较代码相似度
        if (userCode == null || standardCode == null) {
            return false;
        }
        
        // 简单的相似度检查（实际应该更复杂）
        String cleanUserCode = userCode.replaceAll("\\s+", "").toLowerCase();
        String cleanStandardCode = standardCode.replaceAll("\\s+", "").toLowerCase();
        
        // 如果代码相似度超过70%，认为通过
        double similarity = calculateSimilarity(cleanUserCode, cleanStandardCode);
        return similarity > 0.7;
    }
    
    /**
     * 计算字符串相似度
     */
    private double calculateSimilarity(String s1, String s2) {
        if (s1.equals(s2)) {
            return 1.0;
        }
        
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) {
            return 1.0;
        }
        
        int distance = levenshteinDistance(s1, s2);
        return 1.0 - (double) distance / maxLength;
    }
    
    /**
     * 计算编辑距离
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + 1, 
                                      Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
} 