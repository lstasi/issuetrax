package com.issuetrax.app.domain.entity

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Repository(
    val id: Long,
    val name: String,
    val fullName: String,
    val owner: User,
    val description: String?,
    val private: Boolean,
    val htmlUrl: String,
    val cloneUrl: String,
    val sshUrl: String,
    val defaultBranch: String,
    val language: String?,
    val stargazersCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val pushedAt: LocalDateTime?
)