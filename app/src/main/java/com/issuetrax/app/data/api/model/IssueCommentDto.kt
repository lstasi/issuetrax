package com.issuetrax.app.data.api.model

import kotlinx.serialization.Serializable

@Serializable
data class IssueCommentDto(
    val id: Long,
    val body: String,
    val user: UserDto,
    val created_at: String,
    val updated_at: String
)
