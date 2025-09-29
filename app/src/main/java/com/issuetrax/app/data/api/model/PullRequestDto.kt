package com.issuetrax.app.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullRequestDto(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val user: UserDto,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("merged_at") val mergedAt: String?,
    @SerialName("closed_at") val closedAt: String?,
    val mergeable: Boolean?,
    val merged: Boolean,
    val draft: Boolean,
    @SerialName("changed_files") val changedFiles: Int,
    val additions: Int,
    val deletions: Int,
    val commits: Int,
    val head: BranchDto,
    val base: BranchDto,
    @SerialName("html_url") val htmlUrl: String
)

@Serializable
data class BranchDto(
    val label: String,
    val ref: String,
    val sha: String,
    val user: UserDto,
    val repo: RepositoryDto
)