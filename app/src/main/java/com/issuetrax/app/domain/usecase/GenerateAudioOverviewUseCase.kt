package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.entity.AudioOverviewScript
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.PRState
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.ReviewDecision
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Generates a podcast-style audio overview script for a pull request.
 *
 * The script is structured as a spoken-word summary suitable for text-to-speech
 * playback, covering the PR title, author, description, code changes, and status.
 */
class GenerateAudioOverviewUseCase @Inject constructor() {

    operator fun invoke(
        pullRequest: PullRequest,
        files: List<FileDiff> = emptyList(),
    ): AudioOverviewScript {
        val script = buildString {
            appendIntroduction(pullRequest)
            appendDescription(pullRequest)
            appendCodeChanges(pullRequest, files)
            appendStatusSection(pullRequest)
            appendOutro(pullRequest)
        }
        return AudioOverviewScript(
            prNumber = pullRequest.number,
            title = "PR #${pullRequest.number}: ${pullRequest.title}",
            script = script,
        )
    }

    private fun StringBuilder.appendIntroduction(pr: PullRequest) {
        val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
        val date = pr.createdAt.format(dateFormatter)
        append("Welcome to your pull request overview. ")
        append("Pull request number ${pr.number}: \"${pr.title}\". ")
        append("Opened by ${pr.author.login} on $date, ")
        append("targeting the ${pr.baseRef} branch from ${pr.headRef}. ")
    }

    private fun StringBuilder.appendDescription(pr: PullRequest) {
        val body = pr.body?.trim()
        appendLine()
        if (!body.isNullOrBlank()) {
            append("Description. ")
            append(cleanMarkdown(body))
            append(" ")
        } else {
            append("No description was provided for this pull request. ")
        }
    }

    private fun StringBuilder.appendCodeChanges(pr: PullRequest, files: List<FileDiff>) {
        appendLine()
        append("Code changes. ")
        val fileCount = pr.changedFiles ?: files.size
        val additions = pr.additions ?: files.sumOf { it.additions }
        val deletions = pr.deletions ?: files.sumOf { it.deletions }
        val commits = pr.commits
        append(
            "This pull request touches $fileCount ${if (fileCount == 1) "file" else "files"} " +
                "with $additions ${if (additions == 1) "addition" else "additions"} " +
                "and $deletions ${if (deletions == 1) "deletion" else "deletions"}",
        )
        if (commits != null && commits > 0) {
            append(" across $commits ${if (commits == 1) "commit" else "commits"}")
        }
        append(". ")
        if (files.isNotEmpty()) {
            val topFiles = files.take(3)
            val verb = if (topFiles.size == 1) "is" else "are"
            val noun = if (topFiles.size == 1) "file" else "files"
            append("The most notable $noun changed $verb: ")
            append(topFiles.joinToString(", ") { it.filename })
            if (files.size > 3) {
                append(", and ${files.size - 3} more")
            }
            append(". ")
        }
    }

    private fun StringBuilder.appendStatusSection(pr: PullRequest) {
        appendLine()
        append("Status. ")
        val stateText = when (pr.state) {
            PRState.OPEN -> if (pr.draft == true) "open as a draft" else "open and ready for review"
            PRState.CLOSED -> "closed"
            PRState.MERGED -> "merged"
        }
        append("This pull request is currently $stateText. ")
        pr.reviewDecision?.let { decision ->
            val decisionText = when (decision) {
                ReviewDecision.APPROVED -> "It has been approved"
                ReviewDecision.CHANGES_REQUESTED -> "Changes have been requested"
                ReviewDecision.REVIEW_REQUIRED -> "A review is still required"
            }
            append("$decisionText. ")
        }
        pr.checkRunSummary?.let { summary ->
            if (summary.total > 0) {
                append("Checks: ${summary.success} passed, ${summary.failed} failed")
                if (summary.pending > 0) append(", ${summary.pending} pending")
                append(". ")
            }
        }
    }

    private fun StringBuilder.appendOutro(pr: PullRequest) {
        appendLine()
        append("That's the overview for pull request number ${pr.number}. Thanks for listening.")
    }

    /**
     * Strips common Markdown syntax so the text reads naturally when spoken aloud.
     */
    private fun cleanMarkdown(text: String): String =
        text
            .replace(Regex("#{1,6}\\s+"), "")
            .replace(Regex("\\*{1,2}(.+?)\\*{1,2}"), "$1")
            .replace(Regex("`{1,3}[^`\n]*`{1,3}"), "code")
            .replace(Regex("\\[(.+?)]\\(.+?\\)"), "$1")
            .replace(Regex("^\\s*[-*+]\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("\n{2,}"), ". ")
            .trim()
}
