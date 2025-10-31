package com.issuetrax.app.domain.entity

/**
 * Domain entity representing a GitHub Actions workflow run.
 *
 * @property id The unique identifier for the workflow run
 * @property name The name of the workflow
 * @property status The current status (queued, in_progress, completed, waiting)
 * @property conclusion The conclusion of the run if completed (success, failure, cancelled, etc.)
 * @property headSha The commit SHA that the workflow run is associated with
 * @property htmlUrl The URL to view the workflow run on GitHub
 */
data class WorkflowRun(
    val id: Long,
    val name: String,
    val status: String,
    val conclusion: String?,
    val headSha: String,
    val htmlUrl: String
)
