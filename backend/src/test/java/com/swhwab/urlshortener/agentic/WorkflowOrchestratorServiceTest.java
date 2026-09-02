package com.swhwab.urlshortener.agentic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkflowOrchestratorServiceTest {

    @Test
    void shouldCreateExecutionGraphWithGovernedDependencies() {
        WorkflowOrchestratorService service = new WorkflowOrchestratorService();

        WorkflowExecution execution = service.initializeExecution("url-shortener-demo");

        assertThat(execution.getExecutionId()).isNotBlank();
        assertThat(execution.getStageStatus(WorkflowStage.REQUIREMENTS)).isEqualTo(WorkflowStatus.PENDING);
        assertThat(execution.getStageStatus(WorkflowStage.ARCHITECTURE)).isEqualTo(WorkflowStatus.PENDING);
        assertThat(execution.getStageStatus(WorkflowStage.RELEASE_READINESS)).isEqualTo(WorkflowStatus.PENDING);
        assertThat(execution.getDependencies(WorkflowStage.RELEASE_READINESS)).contains(WorkflowStage.VALIDATION);
    }

    @Test
    void shouldRequireApprovalBeforeHighImpactReleaseStage() {
        WorkflowOrchestratorService service = new WorkflowOrchestratorService();
        WorkflowExecution execution = service.initializeExecution("url-shortener-demo");

        service.markStageComplete(execution.getExecutionId(), WorkflowStage.REQUIREMENTS, "requirements accepted");
        service.markStageComplete(execution.getExecutionId(), WorkflowStage.ARCHITECTURE, "design approved");
        service.markStageComplete(execution.getExecutionId(), WorkflowStage.IMPLEMENTATION, "build completed");
        service.markStageComplete(execution.getExecutionId(), WorkflowStage.VALIDATION, "tests passed");
        service.markStageComplete(execution.getExecutionId(), WorkflowStage.DOCUMENTATION, "docs updated");

        assertThatThrownBy(() -> service.executeReleaseReadiness(execution.getExecutionId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("approval");
    }

    @Test
    void shouldTrackRetriesRollbackAndMetrics() {
        WorkflowOrchestratorService service = new WorkflowOrchestratorService();
        WorkflowExecution execution = service.initializeExecution("url-shortener-demo");

        service.markStageFailed(execution.getExecutionId(), WorkflowStage.VALIDATION, "temporary test infrastructure issue");
        service.retryStage(execution.getExecutionId(), WorkflowStage.VALIDATION, "re-ran tests after infrastructure restored");

        WorkflowMetrics metrics = service.getMetrics(execution.getExecutionId());

        assertThat(metrics.getRetryCount()).isGreaterThanOrEqualTo(1);
        assertThat(metrics.getRollbackCount()).isGreaterThanOrEqualTo(0);
        assertThat(metrics.getAuditTrail()).isNotEmpty();
        assertThat(metrics.getSuccessRate()).isGreaterThanOrEqualTo(0.0);
    }
}
