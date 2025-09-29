package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.GitHubRepository
import com.issuetrax.app.domain.repository.ReviewComment
import javax.inject.Inject

class SubmitReviewUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository
) {
    suspend operator fun invoke(
        owner: String,
        repo: String,
        prNumber: Int,
        body: String?,
        event: ReviewEvent,
        comments: List<ReviewComment> = emptyList()
    ): Result<Unit> {
        val eventString = when (event) {
            ReviewEvent.APPROVE -> "APPROVE"
            ReviewEvent.REQUEST_CHANGES -> "REQUEST_CHANGES"
            ReviewEvent.COMMENT -> "COMMENT"
        }
        
        return gitHubRepository.createReview(
            owner = owner,
            repo = repo,
            number = prNumber,
            body = body,
            event = eventString,
            comments = comments
        )
    }
}

enum class ReviewEvent {
    APPROVE, REQUEST_CHANGES, COMMENT
}