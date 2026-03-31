package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.entity.CheckRunSummary
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.FileStatus
import com.issuetrax.app.domain.entity.PRState
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.ReviewDecision
import com.issuetrax.app.domain.entity.User
import com.issuetrax.app.domain.entity.UserType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Unit tests for [GenerateAudioOverviewUseCase].
 *
 * These tests verify that the generated podcast-style script contains the expected
 * content for the main PR fields, status, and code changes.
 */
class GenerateAudioOverviewUseCaseTest {

    private lateinit var useCase: GenerateAudioOverviewUseCase

    @Before
    fun setUp() {
        useCase = GenerateAudioOverviewUseCase()
    }

    // -------------------------------------------------------------------------
    // Script metadata
    // -------------------------------------------------------------------------

    @Test
    fun `returned script has correct prNumber`() {
        val pr = createPullRequest(number = 42)
        val result = useCase(pr)
        assertEquals(42, result.prNumber)
    }

    @Test
    fun `returned script title includes PR number and title`() {
        val pr = createPullRequest(number = 7, title = "Add feature X")
        val result = useCase(pr)
        assertTrue("Title should contain PR number", result.title.contains("7"))
        assertTrue("Title should contain PR title", result.title.contains("Add feature X"))
    }

    // -------------------------------------------------------------------------
    // Introduction section
    // -------------------------------------------------------------------------

    @Test
    fun `script includes PR number in introduction`() {
        val pr = createPullRequest(number = 123)
        val script = useCase(pr).script
        assertTrue("Script should mention PR number 123", script.contains("123"))
    }

    @Test
    fun `script includes PR title in introduction`() {
        val pr = createPullRequest(title = "Improve login flow")
        val script = useCase(pr).script
        assertTrue("Script should mention PR title", script.contains("Improve login flow"))
    }

    @Test
    fun `script includes author login in introduction`() {
        val pr = createPullRequest(authorLogin = "octocat")
        val script = useCase(pr).script
        assertTrue("Script should mention author login", script.contains("octocat"))
    }

    @Test
    fun `script includes head and base branch in introduction`() {
        val pr = createPullRequest(headRef = "feat/new-ui", baseRef = "develop")
        val script = useCase(pr).script
        assertTrue("Script should mention head branch", script.contains("feat/new-ui"))
        assertTrue("Script should mention base branch", script.contains("develop"))
    }

    // -------------------------------------------------------------------------
    // Description section
    // -------------------------------------------------------------------------

    @Test
    fun `script includes cleaned PR body text`() {
        val pr = createPullRequest(body = "Fixes the login bug")
        val script = useCase(pr).script
        assertTrue("Script should contain body text", script.contains("Fixes the login bug"))
    }

    @Test
    fun `script strips markdown headers from body`() {
        val pr = createPullRequest(body = "## Summary\nFixes the bug")
        val script = useCase(pr).script
        assertFalse("Script should not contain markdown header syntax", script.contains("##"))
        assertTrue("Script should still contain 'Summary'", script.contains("Summary"))
    }

    @Test
    fun `script strips bold markdown from body`() {
        val pr = createPullRequest(body = "**Important** change")
        val script = useCase(pr).script
        assertFalse("Script should not contain ** markers", script.contains("**"))
        assertTrue("Script should contain 'Important'", script.contains("Important"))
    }

    @Test
    fun `script replaces inline code with spoken word 'code'`() {
        val pr = createPullRequest(body = "Call `doSomething()` here")
        val script = useCase(pr).script
        assertFalse("Script should not contain backtick markers", script.contains("`"))
    }

    @Test
    fun `script replaces markdown links with link text`() {
        val pr = createPullRequest(body = "See [the docs](https://example.com) for details")
        val script = useCase(pr).script
        assertFalse("Script should not contain markdown link syntax", script.contains("]("))
        assertTrue("Script should contain link text", script.contains("the docs"))
    }

    @Test
    fun `script notes when no description is provided`() {
        val pr = createPullRequest(body = null)
        val script = useCase(pr).script
        assertTrue(
            "Script should indicate no description",
            script.contains("No description was provided", ignoreCase = true),
        )
    }

    @Test
    fun `script notes when description is blank`() {
        val pr = createPullRequest(body = "   ")
        val script = useCase(pr).script
        assertTrue(
            "Script should indicate no description when blank",
            script.contains("No description was provided", ignoreCase = true),
        )
    }

    // -------------------------------------------------------------------------
    // Code changes section
    // -------------------------------------------------------------------------

    @Test
    fun `script mentions number of changed files from PR metadata`() {
        val pr = createPullRequest(changedFiles = 5)
        val script = useCase(pr).script
        assertTrue("Script should mention 5 files", script.contains("5"))
    }

    @Test
    fun `script falls back to files list count when changedFiles is null`() {
        val pr = createPullRequest(changedFiles = null)
        val files = listOf(
            createFileDiff("a.kt"),
            createFileDiff("b.kt"),
            createFileDiff("c.kt"),
        )
        val script = useCase(pr, files).script
        assertTrue("Script should mention 3 files from list", script.contains("3"))
    }

    @Test
    fun `script mentions top three filenames`() {
        val files = listOf(
            createFileDiff("Alpha.kt"),
            createFileDiff("Beta.kt"),
            createFileDiff("Gamma.kt"),
        )
        val script = useCase(createPullRequest(), files).script
        assertTrue("Script should mention Alpha.kt", script.contains("Alpha.kt"))
        assertTrue("Script should mention Beta.kt", script.contains("Beta.kt"))
        assertTrue("Script should mention Gamma.kt", script.contains("Gamma.kt"))
    }

    @Test
    fun `script mentions remaining file count when more than three files`() {
        val files = (1..6).map { createFileDiff("file$it.kt") }
        val script = useCase(createPullRequest(changedFiles = 6), files).script
        assertTrue("Script should mention remaining 3 files", script.contains("3 more"))
    }

    // -------------------------------------------------------------------------
    // Status section
    // -------------------------------------------------------------------------

    @Test
    fun `script mentions open state for open PR`() {
        val pr = createPullRequest(state = PRState.OPEN, draft = false)
        val script = useCase(pr).script
        assertTrue("Script should mention open state", script.contains("open", ignoreCase = true))
    }

    @Test
    fun `script mentions draft state for draft PR`() {
        val pr = createPullRequest(state = PRState.OPEN, draft = true)
        val script = useCase(pr).script
        assertTrue("Script should mention draft state", script.contains("draft", ignoreCase = true))
    }

    @Test
    fun `script mentions merged state for merged PR`() {
        val pr = createPullRequest(state = PRState.MERGED)
        val script = useCase(pr).script
        assertTrue("Script should mention merged state", script.contains("merged", ignoreCase = true))
    }

    @Test
    fun `script mentions closed state for closed PR`() {
        val pr = createPullRequest(state = PRState.CLOSED)
        val script = useCase(pr).script
        assertTrue("Script should mention closed state", script.contains("closed", ignoreCase = true))
    }

    @Test
    fun `script mentions approved review decision`() {
        val pr = createPullRequest(reviewDecision = ReviewDecision.APPROVED)
        val script = useCase(pr).script
        assertTrue("Script should mention approved", script.contains("approved", ignoreCase = true))
    }

    @Test
    fun `script mentions changes requested review decision`() {
        val pr = createPullRequest(reviewDecision = ReviewDecision.CHANGES_REQUESTED)
        val script = useCase(pr).script
        assertTrue(
            "Script should mention changes requested",
            script.contains("Changes have been requested", ignoreCase = true),
        )
    }

    @Test
    fun `script includes check run summary when available`() {
        val pr = createPullRequest(
            checkRunSummary = CheckRunSummary(total = 5, pending = 0, success = 4, failed = 1, skipped = 0),
        )
        val script = useCase(pr).script
        assertTrue("Script should mention passed checks", script.contains("4 passed"))
        assertTrue("Script should mention failed checks", script.contains("1 failed"))
    }

    @Test
    fun `script skips check run summary when none available`() {
        val pr = createPullRequest(checkRunSummary = null)
        val script = useCase(pr).script
        assertFalse("Script should not mention 'passed' checks", script.contains("passed"))
    }

    // -------------------------------------------------------------------------
    // Outro section
    // -------------------------------------------------------------------------

    @Test
    fun `script ends with thanks for listening`() {
        val pr = createPullRequest()
        val script = useCase(pr).script
        assertTrue(
            "Script should end with thanks",
            script.contains("Thanks for listening", ignoreCase = true),
        )
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun createPullRequest(
        number: Int = 1,
        title: String = "Test PR",
        body: String? = "Test body",
        state: PRState = PRState.OPEN,
        draft: Boolean = false,
        authorLogin: String = "dev",
        headRef: String = "feature/branch",
        baseRef: String = "main",
        changedFiles: Int? = 2,
        additions: Int? = 10,
        deletions: Int? = 3,
        commits: Int? = 2,
        reviewDecision: ReviewDecision? = null,
        checkRunSummary: CheckRunSummary? = null,
    ): PullRequest = PullRequest(
        id = number.toLong(),
        number = number,
        title = title,
        body = body,
        state = state,
        author = User(
            id = 1,
            login = authorLogin,
            name = authorLogin,
            email = null,
            avatarUrl = "https://example.com/avatar.png",
            htmlUrl = "https://github.com/$authorLogin",
            type = UserType.USER,
        ),
        createdAt = LocalDateTime.of(2024, 1, 15, 10, 0),
        updatedAt = LocalDateTime.of(2024, 1, 16, 12, 0),
        mergedAt = null,
        closedAt = null,
        mergeable = true,
        merged = state == PRState.MERGED,
        draft = draft,
        reviewDecision = reviewDecision,
        changedFiles = changedFiles,
        additions = additions,
        deletions = deletions,
        commits = commits,
        headRef = headRef,
        baseRef = baseRef,
        htmlUrl = "https://github.com/owner/repo/pull/$number",
        checkRunSummary = checkRunSummary,
    )

    private fun createFileDiff(filename: String): FileDiff = FileDiff(
        filename = filename,
        status = FileStatus.MODIFIED,
        additions = 5,
        deletions = 2,
        changes = 7,
        patch = null,
        blobUrl = null,
        rawUrl = null,
    )
}
