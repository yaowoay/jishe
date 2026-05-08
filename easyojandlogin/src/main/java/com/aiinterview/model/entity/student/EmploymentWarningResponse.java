// com/aiinterview/entity/EmploymentWarningResponse.java
package com.aiinterview.model.entity.student;

import lombok.Data;
import java.util.List;

@Data
public class EmploymentWarningResponse {
    private boolean success;
    private List<PredictionResult> predictions;
    private int total;
    private String timestamp;
    private String error;

    @Data
    public static class PredictionResult {
        private int index;
        private Double successProbability;
        private String riskLevel;
        private Double riskScore;
        private boolean needWarning;
        private String recommendedAction;
    }
}