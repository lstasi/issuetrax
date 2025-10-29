package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.entity.Issue
import com.issuetrax.app.domain.entity.IssueState
import com.issuetrax.app.domain.entity.User
import com.issuetrax.app.domain.entity.UserType
import com.issuetrax.app.domain.repository.GitHubRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class CreateIssueUseCaseTest {

    private lateinit var repository: GitHubRepository
    private lateinit var useCase: CreateIssueUseCase

    private val mockUser = User(
        id = 1,
        login = "testuser",
        name = "Test User",
        email = null,
        avatarUrl = "https://example.com/avatar.png",
        htmlUrl = "https://github.com/testuser",
        type = UserType.USER
    )

    private val mockIssue = Issue(
        id = 123,
        number = 1,
        title = "Test Issue",
        body = "Test body",
        state = IssueState.OPEN,
        author = mockUser,
        assignees = listOf(mockUser),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        htmlUrl = "https://github.com/owner/repo/issues/1"
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = CreateIssueUseCase(repository)
    }

    @Test
    fun `invoke should create issue successfully`() = runTest {
        // Given
        val owner = "owner"
        val repo = "repo"
        val title = "Test Issue"
        val body = "Test body"
        val assignees = listOf("copilot")

        coEvery {
            repository.createIssue(owner, repo, title, body, assignees)
        } returns Result.success(mockIssue)

        // When
        val result = useCase(owner, repo, title, body, assignees)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockIssue, result.getOrNull())
        coVerify {
            repository.createIssue(owner, repo, title, body, assignees)
        }
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        // Given
        val owner = "owner"
        val repo = "repo"
        val title = "Test Issue"
        val body = "Test body"
        val assignees = listOf("copilot")
        val error = Exception("Network error")

        coEvery {
            repository.createIssue(owner, repo, title, body, assignees)
        } returns Result.failure(error)

        // When
        val result = useCase(owner, repo, title, body, assignees)

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `invoke should work with null body`() = runTest {
        // Given
        val owner = "owner"
        val repo = "repo"
        val title = "Test Issue"
        val body: String? = null
        val assignees = emptyList<String>()

        coEvery {
            repository.createIssue(owner, repo, title, body, assignees)
        } returns Result.success(mockIssue)

        // When
        val result = useCase(owner, repo, title, body, assignees)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            repository.createIssue(owner, repo, title, null, assignees)
        }
    }

    @Test
    fun `invoke should work with empty assignees list`() = runTest {
        // Given
        val owner = "owner"
        val repo = "repo"
        val title = "Test Issue"
        val body = "Test body"
        val assignees = emptyList<String>()

        coEvery {
            repository.createIssue(owner, repo, title, body, assignees)
        } returns Result.success(mockIssue)

        // When
        val result = useCase(owner, repo, title, body, assignees)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            repository.createIssue(owner, repo, title, body, emptyList())
        }
    }
}
