package com.coldwind.easyoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coldwind.easyoj.mapper.ExampleMapper;
import com.coldwind.easyoj.mapper.ProblemMapper;
import com.coldwind.easyoj.mapper.SolutionMapper;
import com.coldwind.easyoj.model.entity.Example;
import com.coldwind.easyoj.model.entity.Problem;
import com.coldwind.easyoj.model.entity.Solution;
import com.coldwind.easyoj.model.vo.ExampleVO;
import com.coldwind.easyoj.model.vo.ProblemVO;
import com.coldwind.easyoj.service.ProblemService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目服务实现类
 */
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {
    
    @Resource
    private ExampleMapper exampleMapper;
    
    @Resource
    private SolutionMapper solutionMapper;
    
    @Override
    public List<Problem> getProblemList() {
        return this.list(new QueryWrapper<Problem>().orderByAsc("id"));
    }
    
    @Override
    public ProblemVO getProblemDetail(Integer problemId) {
        // 获取题目信息
        Problem problem = this.getById(problemId);
        if (problem == null) {
            return null;
        }
        
        // 转换为VO
        ProblemVO problemVO = new ProblemVO();
        BeanUtils.copyProperties(problem, problemVO);
        
        // 获取示例信息
        List<Example> examples = exampleMapper.selectByProblemId(problemId);
        List<ExampleVO> exampleVOs = examples.stream().map(example -> {
            ExampleVO exampleVO = new ExampleVO();
            BeanUtils.copyProperties(example, exampleVO);
            return exampleVO;
        }).collect(Collectors.toList());
        problemVO.setExamples(exampleVOs);
        
        return problemVO;
    }
    
    @Override
    public void favoriteProblem(Integer problemId) {
        baseMapper.updateFavoriteStatus(problemId, true);
    }
    
    @Override
    public void unfavoriteProblem(Integer problemId) {
        baseMapper.updateFavoriteStatus(problemId, false);
    }
    
    @Override
    public String getCodeTemplate(Integer problemId, String language) {
        // 根据语言名称获取语言ID
        Integer languageId = getLanguageId(language);
        if (languageId == null) {
            return getDefaultTemplate(language);
        }
        
        // 查询代码模板
        Solution solution = solutionMapper.selectByProblemAndLanguage(problemId, languageId);
        if (solution != null) {
            return solution.getCode();
        }
        
        return getDefaultTemplate(language);
    }
    
    @Override
    public void updateProblemStatus(Integer problemId, String status) {
        baseMapper.updateStatus(problemId, status);
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
     * 获取默认代码模板
     */
    private String getDefaultTemplate(String language) {
        switch (language.toLowerCase()) {
            case "python":
                return "def solution():\n    # 在这里实现你的解决方案\n    pass\n\n# 测试代码\nif __name__ == \"__main__\":\n    result = solution()\n    print(result)";
            case "java":
                return "public class Solution {\n    public static void main(String[] args) {\n        // 在这里实现你的解决方案\n        System.out.println(\"Hello World\");\n    }\n}";
            case "c":
                return "#include <stdio.h>\n\nint main() {\n    // 在这里实现你的解决方案\n    printf(\"Hello World\\n\");\n    return 0;\n}";
            default:
                return "// 请在此输入你的代码";
        }
    }
} 