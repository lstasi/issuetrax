package com.issuetrax.app.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseDto(
    val id: Long,
    @SerialName("tag_name") val tagName: String,
    val name: String?,
    val body: String?,
    @SerialName("html_url") val htmlUrl: String,
    val draft: Boolean,
    val prerelease: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("published_at") val publishedAt: String?,
    val assets: List<ReleaseAssetDto>,
    val author: UserDto
)

@Serializable
data class ReleaseAssetDto(
    val id: Long,
    val name: String,
    val label: String?,
    @SerialName("content_type") val contentType: String,
    val size: Long,
    @SerialName("download_count") val downloadCount: Int,
    @SerialName("browser_download_url") val browserDownloadUrl: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
