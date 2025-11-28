package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case to re-run a workflow run.
 * This works for any workflow run (unlike approve which only works for fork PRs).
 */
class RerunWorkflowUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Re-runs a workflow run.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param runId The workflow run ID to re-run
     * @param failedOnly If true, only re-run failed jobs; if false, re-run all jobs
     * @return Result with Unit on success or error
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        runId: Long,
        failedOnly: Boolean = false
    ): Result<Unit> {
        return if (failedOnly) {
            repository.rerunFailedJobs(owner, repo, runId)
        } else {
            repository.rerunWorkflowRun(owner, repo, runId)
        }
    }
}
