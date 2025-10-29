package com.issuetrax.app.domain.entity

/**
 * Represents the overall status of a commit based on all status checks.
 */
data class CommitStatus(
    val state: CommitState,
    val statuses: List<Status>,
    val totalCount: Int
)

/**
 * Represents a single status check for a commit.
 */
data class Status(
    val state: CommitState,
    val context: String,
    val description: String?,
    val targetUrl: String?
)

/**
 * The state of a commit status or check.
 */
enum class CommitState {
    PENDING,
    SUCCESS,
    FAILURE,
    ERROR
}
