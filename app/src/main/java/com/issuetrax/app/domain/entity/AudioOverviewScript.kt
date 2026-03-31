package com.issuetrax.app.domain.entity

/**
 * Represents a podcast-style audio overview script generated from a pull request.
 *
 * @param prNumber The pull request number this overview is for.
 * @param title A short display title for the overview.
 * @param script The full spoken-word script suitable for text-to-speech playback.
 */
data class AudioOverviewScript(
    val prNumber: Int,
    val title: String,
    val script: String,
)
