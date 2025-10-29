package com.issuetrax.app.data.api.model

import kotlinx.serialization.Serializable

@Serializable
data class IssueDto(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val user: UserDto,
    val assignees: List<UserDto>,
    val created_at: String,
    val updated_at: String,
    val html_url: String
)
