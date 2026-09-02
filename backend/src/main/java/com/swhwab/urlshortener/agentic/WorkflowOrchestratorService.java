package com.swhwab.urlshortener.agentic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkflowOrchestratorService {
    private final Map<String, WorkflowExecution> executions = new ConcurrentHashMap<>();
    private final Map<String, Integer> retryCounts = new ConcurrentHashMap<>();
    private final Map<String, Integer> rollbackCounts = new ConcurrentHashMap<>();

    public WorkflowExecution initializeExecution(String projectName) {
        WorkflowExecution execution = new WorkflowExecution(projectName);
        executions.put(execution.getExecutionId(), execution);
        return execution;
    }

    public void markStageComplete(String executionId, WorkflowStage stage, String note) {
        WorkflowExecution execution = getExecution(executionId);
        execution.setStageStatus(stage, WorkflowStatus.COMPLETED, note);
    }

    public void markStageFailed(String executionId, WorkflowStage stage, String note) {
        WorkflowExecution execution = getExecution(executionId);
        execution.setStageStatus(stage, WorkflowStatus.FAILED, note);
        rollbackCounts.merge(executionId, 1, Integer::sum);
    }

    public void retryStage(String executionId, WorkflowStage stage, String note) {
        WorkflowExecution execution = getExecution(executionId);
        retryCounts.merge(executionId, 1, Integer::sum);
        execution.setStageStatus(stage, WorkflowStatus.IN_PROGRESS, note);
        execution.addAuditEntry(stage, "retry scheduled: " + note);
    }

    public void executeReleaseReadiness(String executionId) {
        WorkflowExecution execution = getExecution(executionId);

        for (WorkflowStage stage : List.of(WorkflowStage.REQUIREMENTS, WorkflowStage.ARCHITECTURE, WorkflowStage.IMPLEMENTATION, WorkflowStage.VALIDATION, WorkflowStage.DOCUMENTATION)) {
            if (execution.getStageStatus(stage) != WorkflowStatus.COMPLETED) {
                throw new IllegalStateException("Release readiness requires prior stage completion and approval gate");
            }
        }

        execution.setStageStatus(WorkflowStage.RELEASE_READINESS, WorkflowStatus.APPROVAL_REQUIRED, "awaiting human approval");
        throw new IllegalStateException("Release readiness requires human approval before promotion");
    }

    public WorkflowMetrics getMetrics(String executionId) {
        WorkflowExecution execution = getExecution(executionId);

        List<String> auditTrail = new ArrayList<>();
        for (WorkflowStage stage : WorkflowStage.values()) {
            auditTrail.addAll(execution.getAuditTrail(stage));
        }

        double successRate = calculateSuccessRate(execution);
        return new WorkflowMetrics(
                retryCounts.getOrDefault(executionId, 0),
                rollbackCounts.getOrDefault(executionId, 0),
                successRate,
                auditTrail
        );
    }

    private double calculateSuccessRate(WorkflowExecution execution) {
        Map<WorkflowStage, WorkflowStatus> states = execution.snapshot();
        int total = states.size();
        int completed = 0;

        for (WorkflowStatus status : states.values()) {
            if (status == WorkflowStatus.COMPLETED) {
                completed++;
            }
        }

        return total == 0 ? 0.0 : (completed * 100.0) / total;
    }

    private WorkflowExecution getExecution(String executionId) {
        WorkflowExecution execution = executions.get(executionId);
        if (execution == null) {
            throw new IllegalArgumentException("Unknown execution id: " + executionId);
        }
        return execution;
    }
}
