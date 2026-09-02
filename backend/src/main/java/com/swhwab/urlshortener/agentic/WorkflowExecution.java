package com.swhwab.urlshortener.agentic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorkflowExecution {
    private final String executionId;
    private final String projectName;
    private final Instant createdAt;
    private final Map<WorkflowStage, WorkflowStatus> stageStatus = new EnumMap<>(WorkflowStage.class);
    private final Map<WorkflowStage, List<WorkflowStage>> dependencies = new EnumMap<>(WorkflowStage.class);
    private final Map<WorkflowStage, List<String>> auditTrail = new EnumMap<>(WorkflowStage.class);
    private final Map<WorkflowStage, String> lastNotes = new EnumMap<>(WorkflowStage.class);

    public WorkflowExecution(String projectName) {
        this.executionId = UUID.randomUUID().toString();
        this.projectName = projectName;
        this.createdAt = Instant.now();

        initializeDefaultStates();
        initializeDependencies();
    }

    private void initializeDefaultStates() {
        for (WorkflowStage stage : WorkflowStage.values()) {
            stageStatus.put(stage, WorkflowStatus.PENDING);
            auditTrail.put(stage, new ArrayList<>());
            lastNotes.put(stage, "created");
        }
    }

    private void initializeDependencies() {
        dependencies.put(WorkflowStage.REQUIREMENTS, Collections.emptyList());
        dependencies.put(WorkflowStage.ARCHITECTURE, List.of(WorkflowStage.REQUIREMENTS));
        dependencies.put(WorkflowStage.IMPLEMENTATION, List.of(WorkflowStage.ARCHITECTURE));
        dependencies.put(WorkflowStage.VALIDATION, List.of(WorkflowStage.IMPLEMENTATION));
        dependencies.put(WorkflowStage.DOCUMENTATION, List.of(WorkflowStage.VALIDATION));
        dependencies.put(WorkflowStage.RELEASE_READINESS, List.of(WorkflowStage.VALIDATION, WorkflowStage.DOCUMENTATION));
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getProjectName() {
        return projectName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public WorkflowStatus getStageStatus(WorkflowStage stage) {
        return stageStatus.get(stage);
    }

    public List<WorkflowStage> getDependencies(WorkflowStage stage) {
        return dependencies.getOrDefault(stage, Collections.emptyList());
    }

    public void setStageStatus(WorkflowStage stage, WorkflowStatus status, String note) {
        stageStatus.put(stage, status);
        lastNotes.put(stage, note);
        auditTrail.get(stage).add(note);
    }

    public void addAuditEntry(WorkflowStage stage, String note) {
        auditTrail.get(stage).add(note);
    }

    public List<String> getAuditTrail(WorkflowStage stage) {
        return auditTrail.getOrDefault(stage, Collections.emptyList());
    }

    public String getLastNote(WorkflowStage stage) {
        return lastNotes.get(stage);
    }

    public Map<WorkflowStage, WorkflowStatus> snapshot() {
        return new HashMap<>(stageStatus);
    }
}
