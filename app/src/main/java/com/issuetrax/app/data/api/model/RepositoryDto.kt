package com.issuetrax.app.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepositoryDto(
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val owner: UserDto,
    val description: String?,
    val private: Boolean,
    val archived: Boolean,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("clone_url") val cloneUrl: String,
    @SerialName("ssh_url") val sshUrl: String,
    @SerialName("default_branch") val defaultBranch: String,
    val language: String?,
    @SerialName("stargazers_count") val stargazersCount: Int,
    @SerialName("forks_count") val forksCount: Int,
    @SerialName("open_issues_count") val openIssuesCount: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("pushed_at") val pushedAt: String?
)