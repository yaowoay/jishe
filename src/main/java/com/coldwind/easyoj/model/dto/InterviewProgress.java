package com.coldwind.easyoj.model.dto;

public class InterviewProgress {
    private int currentStep;  // 1=上传简历, 2=配置, 3=面试中
    private boolean readyToStart;

    // getters and setters
    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public boolean isReadyToStart() {
        return readyToStart;
    }

    public void setReadyToStart(boolean readyToStart) {
        this.readyToStart = readyToStart;
    }
}