package com.issuetrax.app.domain.entity

import java.time.LocalDateTime

data class Review(
    val id: Long,
    val user: User,
    val body: String?,
    val state: ReviewState,
    val htmlUrl: String,
    val pullRequestUrl: String,
    val submittedAt: LocalDateTime?,
    val commitId: String,
    val authorAssociation: String
)

enum class ReviewState {
    APPROVED,
    CHANGES_REQUESTED,
    COMMENTED,
    DISMISSED,
    PENDING
}
