package com.issuetrax.app.domain.entity

import java.time.LocalDateTime

/**
 * Represents a GitHub release
 */
data class Release(
    val id: Long,
    val tagName: String,
    val name: String?,
    val body: String?,
    val htmlUrl: String,
    val draft: Boolean,
    val prerelease: Boolean,
    val createdAt: LocalDateTime,
    val publishedAt: LocalDateTime?,
    val assets: List<ReleaseAsset>,
    val author: User
)

/**
 * Represents an asset attached to a GitHub release
 */
data class ReleaseAsset(
    val id: Long,
    val name: String,
    val label: String?,
    val contentType: String,
    val size: Long,
    val downloadCount: Int,
    val browserDownloadUrl: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
