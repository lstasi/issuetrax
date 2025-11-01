package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.entity.WorkflowRun
import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case for getting GitHub Actions workflow runs for a repository.
 */
class GetWorkflowRunsUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Gets workflow runs for a repository.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param event Optional event type filter (e.g., "pull_request")
     * @param status Optional status filter (e.g., "waiting", "in_progress")
     * @return Result with list of workflow runs
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        event: String? = null,
        status: String? = null
    ): Result<List<WorkflowRun>> {
        return repository.getWorkflowRuns(owner, repo, event, status)
    }
}
