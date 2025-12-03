package com.issuetrax.app.domain.entity

/**
 * Domain entity representing a GitHub Check Run.
 * 
 * Check runs are created by GitHub Actions and other CI integrations.
 * They provide status information for CI/CD pipelines.
 *
 * @property id The unique identifier for the check run
 * @property name The name of the check (e.g., "build", "test", workflow job name)
 * @property status The current status: queued, in_progress, completed
 * @property conclusion The conclusion if completed: success, failure, neutral, cancelled, skipped, timed_out, action_required
 * @property htmlUrl The URL to view the check run on GitHub
 */
data class CheckRun(
    val id: Long,
    val name: String,
    val status: CheckRunStatus,
    val conclusion: CheckRunConclusion?,
    val htmlUrl: String?
)

/**
 * The status of a check run.
 */
enum class CheckRunStatus {
    QUEUED,
    IN_PROGRESS,
    COMPLETED
}

/**
 * The conclusion of a completed check run.
 */
enum class CheckRunConclusion {
    SUCCESS,
    FAILURE,
    NEUTRAL,
    CANCELLED,
    SKIPPED,
    TIMED_OUT,
    ACTION_REQUIRED
}

/**
 * Summary of check runs for display in the UI.
 */
data class CheckRunSummary(
    val total: Int,
    val pending: Int,    // queued + in_progress
    val success: Int,    // completed with success conclusion
    val failed: Int,     // completed with failure, timed_out, or action_required conclusion
    val skipped: Int     // completed with skipped, neutral, or cancelled conclusion
)
