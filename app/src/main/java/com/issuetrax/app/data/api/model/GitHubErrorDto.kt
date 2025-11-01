package com.issuetrax.app.data.api.model

import kotlinx.serialization.Serializable

/**
 * Represents an error response from the GitHub API.
 * 
 * GitHub API returns detailed error information in the response body
 * when requests fail, including specific reasons and documentation URLs.
 */
@Serializable
data class GitHubErrorDto(
    val message: String,
    val errors: List<ErrorDetail>? = null,
    val documentation_url: String? = null
)

@Serializable
data class ErrorDetail(
    val resource: String? = null,
    val field: String? = null,
    val code: String? = null,
    val message: String? = null
)
