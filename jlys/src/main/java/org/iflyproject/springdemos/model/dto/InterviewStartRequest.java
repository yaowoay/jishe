package org.iflyproject.springdemos.model.dto;

public class InterviewStartRequest {
    private Long resumeId;          // 上传的简历ID
    private Long interviewerConfigId; // 面试官配置ID
    private Long questionConfigId;   // 题目配置ID
    private Long interviewModeId;    // 面试模式ID

    // getters and setters
    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Long getInterviewerConfigId() {
        return interviewerConfigId;
    }

    public void setInterviewerConfigId(Long interviewerConfigId) {
        this.interviewerConfigId = interviewerConfigId;
    }

    public Long getQuestionConfigId() {
        return questionConfigId;
    }

    public void setQuestionConfigId(Long questionConfigId) {
        this.questionConfigId = questionConfigId;
    }

    public Long getInterviewModeId() {
        return interviewModeId;
    }

    public void setInterviewModeId(Long interviewModeId) {
        this.interviewModeId = interviewModeId;
    }
}