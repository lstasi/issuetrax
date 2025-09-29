package com.issuetrax.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "pull_requests")
data class PullRequestEntity(
    @PrimaryKey val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val authorId: Long,
    val authorLogin: String,
    val authorAvatarUrl: String,
    val repositoryId: Long,
    val repositoryFullName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val mergedAt: LocalDateTime?,
    val closedAt: LocalDateTime?,
    val mergeable: Boolean?,
    val merged: Boolean,
    val draft: Boolean,
    val changedFiles: Int,
    val additions: Int,
    val deletions: Int,
    val commits: Int,
    val headRef: String,
    val baseRef: String,
    val htmlUrl: String,
    val lastSyncAt: LocalDateTime = LocalDateTime.now()
)