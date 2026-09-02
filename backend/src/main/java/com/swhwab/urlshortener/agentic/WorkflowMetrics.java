package com.swhwab.urlshortener.agentic;

import java.util.ArrayList;
import java.util.List;

public class WorkflowMetrics {
    private final int retryCount;
    private final int rollbackCount;
    private final double successRate;
    private final List<String> auditTrail;

    public WorkflowMetrics(int retryCount, int rollbackCount, double successRate, List<String> auditTrail) {
        this.retryCount = retryCount;
        this.rollbackCount = rollbackCount;
        this.successRate = successRate;
        this.auditTrail = new ArrayList<>(auditTrail);
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getRollbackCount() {
        return rollbackCount;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public List<String> getAuditTrail() {
        return auditTrail;
    }
}
