package com.issuetrax.app.domain.entity

import java.time.LocalDateTime

data class PullRequest(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: PRState,
    val author: User,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val mergedAt: LocalDateTime?,
    val closedAt: LocalDateTime?,
    val mergeable: Boolean?,
    val merged: Boolean?,
    val draft: Boolean?,
    val reviewDecision: ReviewDecision?,
    val changedFiles: Int?,
    val additions: Int?,
    val deletions: Int?,
    val commits: Int?,
    val headRef: String,
    val baseRef: String,
    val htmlUrl: String
)

enum class PRState {
    OPEN, CLOSED, MERGED
}

enum class ReviewDecision {
    APPROVED, CHANGES_REQUESTED, REVIEW_REQUIRED
}