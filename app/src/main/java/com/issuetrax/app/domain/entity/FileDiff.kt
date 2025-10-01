package com.issuetrax.app.domain.entity

data class FileDiff(
    val filename: String,
    val status: FileStatus,
    val additions: Int,
    val deletions: Int,
    val changes: Int,
    val patch: String?,
    val blobUrl: String?,
    val rawUrl: String?,
    val previousFilename: String? = null
)

enum class FileStatus {
    ADDED, MODIFIED, REMOVED, RENAMED, COPIED, CHANGED, UNCHANGED
}

data class CodeHunk(
    val oldStart: Int,
    val oldCount: Int,
    val newStart: Int,
    val newCount: Int,
    val lines: List<DiffLine>
)

data class DiffLine(
    val type: LineType,
    val content: String,
    val oldLineNumber: Int?,
    val newLineNumber: Int?
)

enum class LineType {
    CONTEXT, ADDITION, DELETION, NO_NEWLINE
}