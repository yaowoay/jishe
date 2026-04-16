package com.aiinterview.controller.resume;

import com.aiinterview.common.BaseResponse;
import com.aiinterview.common.ResultUtils;
import com.aiinterview.model.dto.resume.ResumeMatchRequest;
import com.aiinterview.service.resume.ResumeMatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 简历匹配分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/match")
@CrossOrigin(origins = "*")
public class ResumeMatchController {

    private final ResumeMatchService resumeMatchService;

    public ResumeMatchController(ResumeMatchService resumeMatchService) {
        this.resumeMatchService = resumeMatchService;
    }

    /**
     * 简历匹配分析接口
     * @param updownCV 简历文件
     * @param jobPosition 求职岗位
     * @param jobDescription 求职信息/描述
     * @return 匹配分析结果
     */
    @PostMapping("/analyze")
    public Mono<BaseResponse<Map<String, Object>>> analyzeResumeMatch(
            @RequestPart("updownCV") MultipartFile updownCV,
            @RequestParam("jobPosition") String jobPosition,
            @RequestParam("jobDescription") String jobDescription) {

        log.info("收到简历匹配分析请求: jobPosition={}, fileName={}",
                jobPosition, updownCV.getOriginalFilename());

        ResumeMatchRequest request = new ResumeMatchRequest();
        request.setUpdownCV(updownCV);
        request.setJobPosition(jobPosition);
        request.setJobDescription(jobDescription);

        return resumeMatchService.analyzeResumeMatch(request)
                .map(result -> {
                    log.info("简历匹配分析成功");
                    return ResultUtils.success(result);
                })
                .onErrorResume(error -> {
                    log.error("简历匹配分析失败: {}", error.getMessage(), error);
                    return Mono.just(ResultUtils.error(500, "简历分析失败: " + error.getMessage()));
                });
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("简历匹配服务正常运行");
    }
}
