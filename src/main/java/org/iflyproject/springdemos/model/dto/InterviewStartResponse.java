package org.iflyproject.springdemos.model.dto;

public class InterviewStartResponse {
    private boolean success;
    private String message;
    private InterviewSession session;

    // 构造函数
    public InterviewStartResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public InterviewStartResponse(boolean success, String message,
                                  InterviewSession session) {
        this(success, message);
        this.session = session;
    }

    // getters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InterviewSession getSession() {
        return session;
    }

    public void setSession(InterviewSession session) {
        this.session = session;
    }
}