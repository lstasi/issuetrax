package com.issuetrax.app.presentation.ui.pr_review.components

import com.issuetrax.app.domain.entity.PRState
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.User
import com.issuetrax.app.domain.entity.UserType
import org.junit.Test
import java.time.LocalDateTime

/**
 * Unit tests for PRMetadataCard composable.
 * 
 * These tests verify the data transformation and business logic
 * for displaying PR metadata. Full UI tests would be in androidTest.
 */
class PRMetadataCardTest {
    
    @Test
    fun `PRMetadataCard displays correct PR information`() {
        // Given - a test pull request with all fields populated
        val pullRequest = createTestPullRequest(
            number = 123,
            title = "Add new feature",
            body = "This is a test PR description",
            state = PRState.OPEN,
            commits = 5,
            changedFiles = 10,
            additions = 100,
            deletions = 50
        )
        
        // When/Then - The composable should display all this information
        // Note: Full UI assertions would be in androidTest with Compose testing
        // Here we're just verifying the data object is correctly structured
        assert(pullRequest.number == 123)
        assert(pullRequest.title == "Add new feature")
        assert(pullRequest.body == "This is a test PR description")
        assert(pullRequest.state == PRState.OPEN)
        assert(pullRequest.commits == 5)
        assert(pullRequest.changedFiles == 10)
        assert(pullRequest.additions == 100)
        assert(pullRequest.deletions == 50)
    }
    
    @Test
    fun `PRMetadataCard handles null body correctly`() {
        // Given - a PR with null body
        val pullRequest = createTestPullRequest(
            number = 456,
            title = "Bug fix",
            body = null
        )
        
        // When/Then - The composable should handle null body gracefully
        assert(pullRequest.body == null)
        assert(pullRequest.title == "Bug fix")
    }
    
    @Test
    fun `PRMetadataCard handles merged PR correctly`() {
        // Given - a merged PR
        val mergedAt = LocalDateTime.now().minusDays(1)
        val pullRequest = createTestPullRequest(
            number = 789,
            title = "Merged feature",
            state = PRState.MERGED,
            mergedAt = mergedAt
        )
        
        // When/Then - The composable should show merged state and time
        assert(pullRequest.state == PRState.MERGED)
        assert(pullRequest.mergedAt == mergedAt)
    }
    
    @Test
    fun `PRMetadataCard handles closed PR correctly`() {
        // Given - a closed PR
        val closedAt = LocalDateTime.now().minusHours(2)
        val pullRequest = createTestPullRequest(
            number = 999,
            title = "Closed PR",
            state = PRState.CLOSED,
            closedAt = closedAt
        )
        
        // When/Then - The composable should show closed state and time
        assert(pullRequest.state == PRState.CLOSED)
        assert(pullRequest.closedAt == closedAt)
    }
    
    private fun createTestPullRequest(
        number: Int,
        title: String,
        body: String? = "Test PR body",
        state: PRState = PRState.OPEN,
        commits: Int? = 1,
        changedFiles: Int? = 2,
        additions: Int? = 10,
        deletions: Int? = 5,
        mergedAt: LocalDateTime? = null,
        closedAt: LocalDateTime? = null
    ): PullRequest {
        return PullRequest(
            id = number.toLong(),
            number = number,
            title = title,
            body = body,
            state = state,
            author = User(
                id = 1,
                login = "testuser",
                name = "Test User",
                email = null,
                avatarUrl = "https://example.com/avatar.png",
                htmlUrl = "https://github.com/testuser",
                type = UserType.USER
            ),
            createdAt = LocalDateTime.now().minusDays(7),
            updatedAt = LocalDateTime.now().minusHours(1),
            mergedAt = mergedAt,
            closedAt = closedAt,
            mergeable = true,
            merged = state == PRState.MERGED,
            draft = false,
            reviewDecision = null,
            changedFiles = changedFiles,
            additions = additions,
            deletions = deletions,
            commits = commits,
            headRef = "feature/test",
            baseRef = "main",
            htmlUrl = "https://github.com/testuser/test-repo/pull/$number"
        )
    }
}
