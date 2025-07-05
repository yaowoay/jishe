package com.coldwind.easyoj.controller;

import com.coldwind.easyoj.common.BaseResponse;
import com.coldwind.easyoj.model.entity.Problem;
import com.coldwind.easyoj.model.vo.ProblemVO;

import com.coldwind.easyoj.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 题目控制器
 */
@Slf4j
@RestController
@RequestMapping("/problems")
public class ProblemController {
    
    @Resource
    private ProblemService problemService;
    
    /**
     * 获取题目列表
     */
    @GetMapping
    public BaseResponse<List<Problem>> getProblemList() {
        try {
            List<Problem> problems = problemService.getProblemList();
            return BaseResponse.success(problems);
        } catch (Exception e) {
            log.error("获取题目列表失败", e);
            return BaseResponse.error("获取题目列表失败");
        }
    }
    
    /**
     * 获取题目详情
     */
    @GetMapping("/{problemId}")
    public BaseResponse<ProblemVO> getProblemDetail(@PathVariable Integer problemId) {
        try {
            ProblemVO problemVO = problemService.getProblemDetail(problemId);
            if (problemVO == null) {
                return BaseResponse.error("题目不存在");
            }
            return BaseResponse.success(problemVO);
        } catch (Exception e) {
            log.error("获取题目详情失败", e);
            return BaseResponse.error("获取题目详情失败");
        }
    }
    
    /**
     * 收藏题目
     */
    @PostMapping("/{problemId}/favorite")
    public BaseResponse<String> favoriteProblem(@PathVariable Integer problemId) {
        try {
            problemService.favoriteProblem(problemId);
            return BaseResponse.success("收藏成功");
        } catch (Exception e) {
            log.error("收藏题目失败", e);
            return BaseResponse.error("收藏失败");
        }
    }
    
    /**
     * 取消收藏题目
     */
    @PostMapping("/{problemId}/unfavorite")
    public BaseResponse<String> unfavoriteProblem(@PathVariable Integer problemId) {
        try {
            problemService.unfavoriteProblem(problemId);
            return BaseResponse.success("取消收藏成功");
        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return BaseResponse.error("取消收藏失败");
        }
    }
    
    /**
     * 获取代码模板
     */
    @GetMapping("/{problemId}/template")
    public BaseResponse<String> getCodeTemplate(@PathVariable Integer problemId, 
                                               @RequestParam String language) {
        try {
            String template = problemService.getCodeTemplate(problemId, language);
            return BaseResponse.success(template);
        } catch (Exception e) {
            log.error("获取代码模板失败", e);
            return BaseResponse.error("获取代码模板失败");
        }
    }
} 