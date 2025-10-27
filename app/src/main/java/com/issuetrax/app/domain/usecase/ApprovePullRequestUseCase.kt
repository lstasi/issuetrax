package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case for approving a pull request.
 * 
 * Submits an "APPROVE" review for the specified pull request.
 * This is a quick approve without detailed comments.
 */
class ApprovePullRequestUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Approves the pull request.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param prNumber Pull request number
     * @param comment Optional approval comment
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        prNumber: Int,
        comment: String? = null
    ): Result<Unit> {
        return repository.approvePullRequest(owner, repo, prNumber, comment)
    }
}
