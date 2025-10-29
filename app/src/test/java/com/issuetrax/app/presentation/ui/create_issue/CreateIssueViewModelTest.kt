package com.issuetrax.app.presentation.ui.create_issue

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.issuetrax.app.domain.entity.Issue
import com.issuetrax.app.domain.entity.IssueState
import com.issuetrax.app.domain.entity.User
import com.issuetrax.app.domain.entity.UserType
import com.issuetrax.app.domain.usecase.CreateIssueUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CreateIssueViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var createIssueUseCase: CreateIssueUseCase
    private lateinit var viewModel: CreateIssueViewModel
    private val testDispatcher = StandardTestDispatcher()

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
        Dispatchers.setMain(testDispatcher)
        createIssueUseCase = mockk()
        viewModel = CreateIssueViewModel(createIssueUseCase)
    }

    @Test
    fun `createIssue should complete successfully`() = runTest {
        // Given
        coEvery {
            createIssueUseCase("owner", "repo", "Test", "Body", listOf("copilot"))
        } returns Result.success(mockIssue)

        // When
        viewModel.createIssue("owner", "repo", "Test", "Body")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - check final state
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.issueCreated)
    }

    @Test
    fun `createIssue should update state on success`() = runTest {
        // Given
        coEvery {
            createIssueUseCase("owner", "repo", "Test Issue", "Test body", listOf("copilot"))
        } returns Result.success(mockIssue)

        // When
        viewModel.createIssue("owner", "repo", "Test Issue", "Test body")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.issueCreated)
        assertEquals(null, state.error)
    }

    @Test
    fun `createIssue should update state on failure`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery {
            createIssueUseCase("owner", "repo", "Test Issue", "Test body", listOf("copilot"))
        } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.createIssue("owner", "repo", "Test Issue", "Test body")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertFalse(state.issueCreated)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `createIssue should auto-assign to copilot`() = runTest {
        // Given
        coEvery {
            createIssueUseCase("owner", "repo", "Test Issue", "Test body", listOf("copilot"))
        } returns Result.success(mockIssue)

        // When
        viewModel.createIssue("owner", "repo", "Test Issue", "Test body")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            createIssueUseCase("owner", "repo", "Test Issue", "Test body", listOf("copilot"))
        }
    }

    @Test
    fun `createIssue should handle null body`() = runTest {
        // Given
        coEvery {
            createIssueUseCase("owner", "repo", "Test Issue", null, listOf("copilot"))
        } returns Result.success(mockIssue)

        // When
        viewModel.createIssue("owner", "repo", "Test Issue", null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            createIssueUseCase("owner", "repo", "Test Issue", null, listOf("copilot"))
        }
    }

    @Test
    fun `resetState should reset to initial state`() = runTest {
        // Given - create an issue first to change state
        coEvery {
            createIssueUseCase(any(), any(), any(), any(), any())
        } returns Result.success(mockIssue)

        viewModel.createIssue("owner", "repo", "Test Issue", "Test body")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.resetState()

        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertFalse(state.issueCreated)
        assertEquals(null, state.error)
    }

    @Test
    fun `createIssue should handle generic exception with default message`() = runTest {
        // Given
        coEvery {
            createIssueUseCase("owner", "repo", "Test Issue", "Test body", listOf("copilot"))
        } returns Result.failure(Exception())

        // When
        viewModel.createIssue("owner", "repo", "Test Issue", "Test body")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("Failed to create issue", state.error)
    }
}
