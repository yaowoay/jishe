package com.coldwind.easyoj.controller;

import com.coldwind.easyoj.model.dto.request.ProblemRunRequest;
import com.coldwind.easyoj.model.dto.request.ProblemSubmitRequest;
import com.coldwind.easyoj.model.dto.response.ProblemRunResponse;
import com.coldwind.easyoj.model.dto.response.ProblemSubmitResponse;
import com.coldwind.easyoj.service.OjService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * OJ判题控制器
 */
@Slf4j
@RestController
@RequestMapping("/oj")
public class OjController {
    
    @Resource
    private OjService ojService;
    
    /**
     * 运行代码
     */
    @PostMapping("/run")
    public ProblemRunResponse runCode(@Valid @RequestBody ProblemRunRequest request) {
        log.info("运行代码请求: problemId={}, language={}", request.getProblemId(), request.getLanguage());
        return ojService.runCode(request);
    }
    
    /**
     * 提交代码判题
     */
    @PostMapping("/submit")
    public ProblemSubmitResponse submitCode(@Valid @RequestBody ProblemSubmitRequest request) {
        log.info("提交代码请求: problemId={}, language={}", request.getProblemId(), request.getLanguage());
        return ojService.submitCode(request);
    }
} 