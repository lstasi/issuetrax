package com.issuetrax.app.domain.entity

import java.time.LocalDateTime

data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: IssueState,
    val author: User,
    val assignees: List<User>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val htmlUrl: String
)

enum class IssueState {
    OPEN,
    CLOSED
}
