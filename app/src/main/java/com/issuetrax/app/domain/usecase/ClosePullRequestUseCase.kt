package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case for closing a pull request without merging.
 * 
 * Updates the PR state to "closed" without performing a merge.
 */
class ClosePullRequestUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Closes the pull request.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param prNumber Pull request number
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        prNumber: Int
    ): Result<Unit> {
        return repository.closePullRequest(owner, repo, prNumber)
    }
}
