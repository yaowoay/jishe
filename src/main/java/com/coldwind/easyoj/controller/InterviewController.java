package com.coldwind.easyoj.controller;
//已完成测试

// ... existing code ...
import com.coldwind.easyoj.model.dto.InterviewProgress;
import com.coldwind.easyoj.model.dto.InterviewSession;
import com.coldwind.easyoj.model.dto.InterviewStartRequest;
import com.coldwind.easyoj.model.dto.InterviewStartResponse;
import com.coldwind.easyoj.service.InterviewFlowService;
// ... existing code ...
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    @Autowired
    private InterviewFlowService interviewFlowService;

    @PostMapping("/start")
    public ResponseEntity<InterviewStartResponse> startInterview(
            @RequestBody InterviewStartRequest request) {

        // 验证所有配置步骤已完成
        if (!interviewFlowService.validateAllStepsCompleted(request)) {
            return ResponseEntity.badRequest().body(
                    new InterviewStartResponse(false, "请完成所有配置步骤"));
        }

        // 初始化面试会话
        InterviewSession session = interviewFlowService.initializeInterviewSession(request);

        return ResponseEntity.ok(
                new InterviewStartResponse(true, "面试已启动", session));
    }

    @GetMapping("/progress/{sessionId}")
    public ResponseEntity<InterviewProgress> getInterviewProgress(
            @PathVariable String sessionId) {
        InterviewProgress progress = interviewFlowService.getProgress(sessionId);
        return ResponseEntity.ok(progress);
    }
}