package com.coldwind.easyoj.service;

import com.coldwind.easyoj.model.dto.request.ProblemRunRequest;
import com.coldwind.easyoj.model.dto.request.ProblemSubmitRequest;
import com.coldwind.easyoj.model.dto.response.ProblemRunResponse;
import com.coldwind.easyoj.model.dto.response.ProblemSubmitResponse;

/**
 * OJ判题服务接口
 */
public interface OjService {
    
    /**
     * 运行代码
     */
    ProblemRunResponse runCode(ProblemRunRequest request);
    
    /**
     * 提交代码判题
     */
    ProblemSubmitResponse submitCode(ProblemSubmitRequest request);
} 