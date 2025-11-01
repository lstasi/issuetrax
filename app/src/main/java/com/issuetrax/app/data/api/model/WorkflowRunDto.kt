package com.issuetrax.app.data.api.model

import kotlinx.serialization.Serializable

/**
 * Data transfer object for GitHub Actions workflow run.
 *
 * This represents the response from the GitHub API for workflow runs.
 */
@Serializable
data class WorkflowRunDto(
    val id: Long,
    val name: String,
    val status: String,
    val conclusion: String? = null,
    val head_sha: String,
    val html_url: String
)

/**
 * Response wrapper for listing workflow runs.
 */
@Serializable
data class WorkflowRunsResponseDto(
    val total_count: Int,
    val workflow_runs: List<WorkflowRunDto>
)

/**
 * Response for approving a workflow run.
 */
@Serializable
data class WorkflowRunApprovalResponseDto(
    val state: String,
    val required_count: Int
)
