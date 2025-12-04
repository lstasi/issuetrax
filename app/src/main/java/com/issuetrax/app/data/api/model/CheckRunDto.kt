package com.issuetrax.app.data.api.model

import kotlinx.serialization.Serializable

/**
 * Data transfer object for GitHub Check Run.
 *
 * This represents a single check run from the GitHub Check Runs API.
 * Check runs are created by GitHub Actions and other CI tools.
 */
@Serializable
data class CheckRunDto(
    val id: Long,
    val name: String,
    val status: String,  // queued, in_progress, completed
    val conclusion: String? = null,  // success, failure, neutral, cancelled, skipped, timed_out, action_required, null (if not completed)
    val html_url: String? = null
)

/**
 * Response wrapper for listing check runs for a commit.
 * 
 * API: GET /repos/{owner}/{repo}/commits/{ref}/check-runs
 */
@Serializable
data class CheckRunsResponseDto(
    val total_count: Int,
    val check_runs: List<CheckRunDto>
)
