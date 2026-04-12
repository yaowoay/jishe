package org.iflyproject.springdemos.model.dto;

public class InterviewSession {
    private String sessionId;
    private String status;

    // getters and setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}