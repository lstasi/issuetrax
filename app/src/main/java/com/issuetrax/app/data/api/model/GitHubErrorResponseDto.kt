package com.issuetrax.app.data.api.model

import kotlinx.serialization.Serializable

/**
 * GitHub API error response model
 * See: https://docs.github.com/en/rest/overview/resources-in-the-rest-api#client-errors
 */
@Serializable
data class GitHubErrorResponseDto(
    val message: String,
    val errors: List<GitHubErrorDetail>? = null,
    val documentation_url: String? = null
)

@Serializable
data class GitHubErrorDetail(
    val resource: String? = null,
    val field: String? = null,
    val code: String,
    val message: String? = null,
    val value: String? = null
)
