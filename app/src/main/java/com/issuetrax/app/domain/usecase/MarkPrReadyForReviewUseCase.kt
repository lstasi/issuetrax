package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case for marking a draft pull request as ready for review.
 * 
 * This must be called before merging a draft PR, as GitHub doesn't allow
 * merging draft pull requests directly.
 */
class MarkPrReadyForReviewUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Marks the pull request as ready for review.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param prNumber Pull request number
     * @return Result with Unit on success or error
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        prNumber: Int
    ): Result<Unit> {
        return repository.markPrReadyForReview(
            owner = owner,
            repo = repo,
            prNumber = prNumber
        )
    }
}
