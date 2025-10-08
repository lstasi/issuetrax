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
    val mergeable: Boolean? = null,
    val merged: Boolean? = null,
    val draft: Boolean? = null,
    @SerialName("changed_files") val changedFiles: Int? = null,
    val additions: Int? = null,
    val deletions: Int? = null,
    val commits: Int? = null,
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