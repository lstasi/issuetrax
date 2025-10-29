package com.issuetrax.app.data.mapper

import com.issuetrax.app.data.api.model.FileDiffDto
import com.issuetrax.app.data.api.model.PullRequestDto
import com.issuetrax.app.data.api.model.RepositoryDto
import com.issuetrax.app.data.api.model.ReviewDto
import com.issuetrax.app.data.api.model.UserDto
import com.issuetrax.app.data.api.model.IssueDto
import com.issuetrax.app.domain.entity.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun UserDto.toDomain(): User {
    return User(
        id = id,
        login = login,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        type = when (type.lowercase()) {
            "organization" -> UserType.ORGANIZATION
            "bot" -> UserType.BOT
            else -> UserType.USER
        }
    )
}

fun RepositoryDto.toDomain(): Repository {
    return Repository(
        id = id,
        name = name,
        fullName = fullName,
        owner = owner.toDomain(),
        description = description,
        private = private,
        archived = archived,
        htmlUrl = htmlUrl,
        cloneUrl = cloneUrl,
        sshUrl = sshUrl,
        defaultBranch = defaultBranch,
        language = language,
        stargazersCount = stargazersCount,
        forksCount = forksCount,
        openIssuesCount = openIssuesCount,
        createdAt = parseDateTime(createdAt),
        updatedAt = parseDateTime(updatedAt),
        pushedAt = pushedAt?.let { parseDateTime(it) }
    )
}

fun PullRequestDto.toDomain(): PullRequest {
    return PullRequest(
        id = id,
        number = number,
        title = title,
        body = body,
        state = when (state.lowercase()) {
            "closed" -> if (merged == true) PRState.MERGED else PRState.CLOSED
            else -> PRState.OPEN
        },
        author = user.toDomain(),
        createdAt = parseDateTime(createdAt),
        updatedAt = parseDateTime(updatedAt),
        mergedAt = mergedAt?.let { parseDateTime(it) },
        closedAt = closedAt?.let { parseDateTime(it) },
        mergeable = mergeable,
        merged = merged,
        draft = draft,
        reviewDecision = null, // This would need additional API call to get review state
        changedFiles = changedFiles,
        additions = additions,
        deletions = deletions,
        commits = commits,
        headRef = head.ref,
        baseRef = base.ref,
        htmlUrl = htmlUrl
    )
}

fun FileDiffDto.toDomain(): FileDiff {
    return FileDiff(
        filename = filename,
        status = when (status.lowercase()) {
            "added" -> FileStatus.ADDED
            "modified" -> FileStatus.MODIFIED
            "removed" -> FileStatus.REMOVED
            "renamed" -> FileStatus.RENAMED
            "copied" -> FileStatus.COPIED
            "changed" -> FileStatus.CHANGED
            else -> FileStatus.UNCHANGED
        },
        additions = additions,
        deletions = deletions,
        changes = changes,
        patch = patch,
        blobUrl = blobUrl,
        rawUrl = rawUrl,
        previousFilename = previousFilename
    )
}

fun ReviewDto.toDomain(): Review {
    return Review(
        id = id,
        user = user.toDomain(),
        body = body,
        state = when (state.uppercase()) {
            "APPROVED" -> ReviewState.APPROVED
            "CHANGES_REQUESTED" -> ReviewState.CHANGES_REQUESTED
            "COMMENTED" -> ReviewState.COMMENTED
            "DISMISSED" -> ReviewState.DISMISSED
            "PENDING" -> ReviewState.PENDING
            else -> ReviewState.COMMENTED
        },
        htmlUrl = htmlUrl,
        pullRequestUrl = pullRequestUrl,
        submittedAt = submittedAt?.let { parseDateTime(it) },
        commitId = commitId,
        authorAssociation = authorAssociation
    )
}

fun IssueDto.toDomain(): Issue {
    return Issue(
        id = id,
        number = number,
        title = title,
        body = body,
        state = when (state.lowercase()) {
            "closed" -> IssueState.CLOSED
            else -> IssueState.OPEN
        },
        author = user.toDomain(),
        assignees = assignees.map { it.toDomain() },
        createdAt = parseDateTime(created_at),
        updatedAt = parseDateTime(updated_at),
        htmlUrl = html_url
    )
}

private fun parseDateTime(dateTimeString: String): LocalDateTime {
    return try {
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        // Fallback for GitHub's date format
        LocalDateTime.parse(dateTimeString.replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}