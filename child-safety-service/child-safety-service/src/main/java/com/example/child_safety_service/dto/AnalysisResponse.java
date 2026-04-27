package com.example.child_safety_service.dto;

public class AnalysisResponse {

    private boolean isSafe;
    private String reason;
    private double confidence;

    public AnalysisResponse(boolean isSafe, String reason, double confidence) {
        this.isSafe = isSafe;
        this.reason = reason;
        this.confidence = confidence;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public void setSafe(boolean safe) {
        isSafe = safe;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}