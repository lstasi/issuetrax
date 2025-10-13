package com.issuetrax.app.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileDiffDto(
    val filename: String,
    val status: String,
    val additions: Int,
    val deletions: Int,
    val changes: Int,
    val patch: String?,
    @SerialName("blob_url") val blobUrl: String?,
    @SerialName("raw_url") val rawUrl: String?,
    @SerialName("previous_filename") val previousFilename: String? = null
)