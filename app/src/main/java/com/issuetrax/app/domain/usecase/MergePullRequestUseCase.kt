package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case for merging a pull request.
 * 
 * Attempts to merge the PR using the specified merge method.
 */
class MergePullRequestUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Merges the pull request.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param prNumber Pull request number
     * @param commitTitle Optional custom commit title
     * @param commitMessage Optional custom commit message
     * @param mergeMethod Merge method: "merge", "squash", or "rebase" (default: "merge")
     * @return Result with merge SHA or error
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        prNumber: Int,
        commitTitle: String? = null,
        commitMessage: String? = null,
        mergeMethod: String = "merge"
    ): Result<String> {
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
