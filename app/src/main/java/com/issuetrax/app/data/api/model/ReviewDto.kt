package com.issuetrax.app.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val id: Long,
    val user: UserDto,
    val body: String?,
    val state: String, // "APPROVED", "CHANGES_REQUESTED", "COMMENTED", "DISMISSED", "PENDING"
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("pull_request_url")
    val pullRequestUrl: String,
    @SerialName("submitted_at")
    val submittedAt: String?,
    @SerialName("commit_id")
    val commitId: String,
    @SerialName("author_association")
    val authorAssociation: String
)
