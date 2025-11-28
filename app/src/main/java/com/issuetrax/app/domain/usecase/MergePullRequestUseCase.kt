package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case for merging a pull request.
 * 
 * Attempts to merge the PR using the specified merge method.
 * If the PR is a draft, it will first be marked as ready for review.
 */
class MergePullRequestUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Merges the pull request.
     * 
     * If the PR is a draft, it will automatically be marked as ready for review
     * before attempting to merge.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param prNumber Pull request number
     * @param commitTitle Optional custom commit title
     * @param commitMessage Optional custom commit message
     * @param mergeMethod Merge method: "merge", "squash", or "rebase" (default: "merge")
     * @param isDraft Whether the PR is currently a draft (if true, will mark as ready first)
     * @return Result with merge SHA or error
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        prNumber: Int,
        commitTitle: String? = null,
        commitMessage: String? = null,
        mergeMethod: String = "merge",
        isDraft: Boolean = false
    ): Result<String> {
        // If PR is a draft, mark it as ready for review first
        if (isDraft) {
            val readyResult = repository.markPrReadyForReview(owner, repo, prNumber)
            if (readyResult.isFailure) {
                return Result.failure(
                    Exception("Failed to mark PR as ready for review: ${readyResult.exceptionOrNull()?.message}")
                )
            }
        }
        
        return repository.mergePullRequest(
            owner = owner,
            repo = repo,
            prNumber = prNumber,
            commitTitle = commitTitle,
            commitMessage = commitMessage,
            mergeMethod = mergeMethod
        )
    }
}
