package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case for approving a GitHub Actions workflow run.
 * 
 * This is used when a workflow requires manual approval to run,
 * typically for PRs from first-time contributors or external forks.
 */
class ApproveWorkflowRunUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Approves a workflow run.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param runId Workflow run ID to approve
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        runId: Long
    ): Result<Unit> {
        return repository.approveWorkflowRun(owner, repo, runId)
    }
}
