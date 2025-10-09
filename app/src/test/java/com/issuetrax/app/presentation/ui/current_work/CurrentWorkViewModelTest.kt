package com.issuetrax.app.presentation.ui.current_work

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.issuetrax.app.domain.entity.PRState
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.User
import com.issuetrax.app.domain.entity.UserType
import com.issuetrax.app.domain.repository.GitHubRepository
import com.issuetrax.app.domain.usecase.GetPullRequestsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

/**
 * Unit tests for CurrentWorkViewModel
 * 
 * Following Classic TDD approach:
 * - Test ViewModel behavior in isolation
 * - Mock only external dependencies (UseCase and Repository)
 * - Verify state transitions and business logic
 * - Use real data classes instead of mocks where possible
 */
@ExperimentalCoroutinesApi
class CurrentWorkViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var getPullRequestsUseCase: GetPullRequestsUseCase
    private lateinit var gitHubRepository: GitHubRepository
    private lateinit var viewModel: CurrentWorkViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getPullRequestsUseCase = mockk()
        gitHubRepository = mockk()
        viewModel = CurrentWorkViewModel(getPullRequestsUseCase, gitHubRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should have default values`() {
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertFalse("Should not be loading PRs", state.isLoadingPRs)
        assertTrue("Repositories should be empty", state.repositories.isEmpty())
        assertTrue("Pull requests should be empty", state.pullRequests.isEmpty())
        assertNull("Selected repository should be null", state.selectedRepository)
        assertEquals("Filter should be OPEN", PRFilter.OPEN, state.filter)
        assertEquals("Sort order should be UPDATED", PRSortOrder.UPDATED, state.sortBy)
        assertNull("Error should be null", state.error)
    }
    
    @Test
    fun `loadPullRequests should update state to loading then success`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val mockPRs = listOf(
            createTestPullRequest(1, "PR 1", LocalDateTime.now().minusDays(1)),
            createTestPullRequest(2, "PR 2", LocalDateTime.now())
        )
        coEvery { 
            getPullRequestsUseCase(owner, repo, "open") 
        } returns flowOf(Result.success(mockPRs))
        
        // When
        viewModel.loadPullRequests(owner, repo)
        
        // Advance time to process coroutines
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be loading PRs", state.isLoadingPRs)
        assertEquals("Should have 2 PRs", 2, state.pullRequests.size)
        assertEquals("Selected repository should be set", "$owner/$repo", state.selectedRepository)
        assertNull("Error should be null", state.error)
        
        // Verify PRs are sorted by updated date (descending)
        assertEquals("First PR should be PR 2", 2, state.pullRequests[0].number)
        assertEquals("Second PR should be PR 1", 1, state.pullRequests[1].number)
        
        coVerify { getPullRequestsUseCase(owner, repo, "open") }
    }
    
    @Test
    fun `loadPullRequests should update state to loading then error on failure`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val errorMessage = "Network error"
        coEvery { 
            getPullRequestsUseCase(owner, repo, "open") 
        } returns flowOf(Result.failure(RuntimeException(errorMessage)))
        
        // When
        viewModel.loadPullRequests(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be loading PRs", state.isLoadingPRs)
        assertTrue("Pull requests should be empty", state.pullRequests.isEmpty())
        assertEquals("Error message should be set", errorMessage, state.error)
        
        coVerify { getPullRequestsUseCase(owner, repo, "open") }
    }
    
    @Test
    fun `filterPullRequests with OPEN should reload with open state`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val mockPRs = listOf(createTestPullRequest(1, "Open PR", LocalDateTime.now()))
        
        // First load to set selected repository
        coEvery { 
            getPullRequestsUseCase(owner, repo, "open") 
        } returns flowOf(Result.success(mockPRs))
        viewModel.loadPullRequests(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.filterPullRequests(PRFilter.OPEN)
        advanceUntilIdle()
        
        // Then
        assertEquals("Filter should be OPEN", PRFilter.OPEN, viewModel.uiState.value.filter)
        coVerify(exactly = 2) { getPullRequestsUseCase(owner, repo, "open") }
    }
    
    @Test
    fun `filterPullRequests with CLOSED should reload with closed state`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val openPRs = listOf(createTestPullRequest(1, "Open PR", LocalDateTime.now()))
        val closedPRs = listOf(createTestPullRequest(2, "Closed PR", LocalDateTime.now()))
        
        coEvery { 
            getPullRequestsUseCase(owner, repo, "open") 
        } returns flowOf(Result.success(openPRs))
        coEvery { 
            getPullRequestsUseCase(owner, repo, "closed") 
        } returns flowOf(Result.success(closedPRs))
        
        viewModel.loadPullRequests(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.filterPullRequests(PRFilter.CLOSED)
        advanceUntilIdle()
        
        // Then
        assertEquals("Filter should be CLOSED", PRFilter.CLOSED, viewModel.uiState.value.filter)
        coVerify { getPullRequestsUseCase(owner, repo, "closed") }
    }
    
    @Test
    fun `filterPullRequests with ALL should reload with all state`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val openPRs = listOf(createTestPullRequest(1, "PR", LocalDateTime.now()))
        val allPRs = listOf(
            createTestPullRequest(1, "Open PR", LocalDateTime.now()),
            createTestPullRequest(2, "Closed PR", LocalDateTime.now())
        )
        
        coEvery { 
            getPullRequestsUseCase(owner, repo, "open") 
        } returns flowOf(Result.success(openPRs))
        coEvery { 
            getPullRequestsUseCase(owner, repo, "all") 
        } returns flowOf(Result.success(allPRs))
        
        viewModel.loadPullRequests(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.filterPullRequests(PRFilter.ALL)
        advanceUntilIdle()
        
        // Then
        assertEquals("Filter should be ALL", PRFilter.ALL, viewModel.uiState.value.filter)
        assertEquals("Should have 2 PRs", 2, viewModel.uiState.value.pullRequests.size)
        coVerify { getPullRequestsUseCase(owner, repo, "all") }
    }
    
    @Test
    fun `refreshPullRequests should reload with current filter`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val mockPRs = listOf(createTestPullRequest(1, "PR", LocalDateTime.now()))
        
        coEvery { 
            getPullRequestsUseCase(owner, repo, "open") 
        } returns flowOf(Result.success(mockPRs))
        
        viewModel.loadPullRequests(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.refreshPullRequests()
        advanceUntilIdle()
        
        // Then
        coVerify(exactly = 2) { getPullRequestsUseCase(owner, repo, "open") }
    }
    
    @Test
    fun `sortPullRequests by CREATED should sort descending by created date`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val now = LocalDateTime.now()
        val mockPRs = listOf(
            createTestPullRequest(1, "Old PR", now.minusDays(5), now.minusDays(1)),
            createTestPullRequest(2, "New PR", now.minusDays(1), now)
        )
        
        coEvery { 
            getPullRequestsUseCase(owner, repo, "open") 
        } returns flowOf(Result.success(mockPRs))
        
        viewModel.loadPullRequests(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.sortPullRequests(PRSortOrder.CREATED)
        
        // Then
        val state = viewModel.uiState.value
        assertEquals("Sort order should be CREATED", PRSortOrder.CREATED, state.sortBy)
        assertEquals("First PR should be newest (2)", 2, state.pullRequests[0].number)
        assertEquals("Second PR should be oldest (1)", 1, state.pullRequests[1].number)
    }
    
    @Test
    fun `sortPullRequests by UPDATED should sort descending by updated date`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val now = LocalDateTime.now()
        val mockPRs = listOf(
            createTestPullRequest(1, "Recently updated", now.minusDays(5), now.minusHours(1)),
            createTestPullRequest(2, "Old updated", now.minusDays(1), now.minusDays(3))
        )
        
        coEvery { 
            getPullRequestsUseCase(owner, repo, "open") 
        } returns flowOf(Result.success(mockPRs))
        
        viewModel.loadPullRequests(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.sortPullRequests(PRSortOrder.UPDATED)
        
        // Then
        val state = viewModel.uiState.value
        assertEquals("First PR should be most recently updated (1)", 1, state.pullRequests[0].number)
        assertEquals("Second PR should be older updated (2)", 2, state.pullRequests[1].number)
    }
    
    @Test
    fun `clearError should set error to null`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        coEvery { 
            getPullRequestsUseCase(owner, repo, "open") 
        } returns flowOf(Result.failure(RuntimeException("Error")))
        
        viewModel.loadPullRequests(owner, repo)
        advanceUntilIdle()
        
        // Verify error is set
        assertEquals("Error should be set", "Error", viewModel.uiState.value.error)
        
        // When
        viewModel.clearError()
        
        // Then
        assertNull("Error should be null", viewModel.uiState.value.error)
    }
    
    @Test
    fun `loadPullRequests should handle empty list`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        coEvery { 
            getPullRequestsUseCase(owner, repo, "open") 
        } returns flowOf(Result.success(emptyList()))
        
        // When
        viewModel.loadPullRequests(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue("Pull requests should be empty", state.pullRequests.isEmpty())
        assertNull("Error should be null", state.error)
        assertEquals("Selected repository should be set", "$owner/$repo", state.selectedRepository)
    }
    
    // Helper function to create test pull requests with real data
    private fun createTestPullRequest(
        number: Int,
        title: String,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime = createdAt
    ): PullRequest {
        return PullRequest(
            id = number.toLong(),
            number = number,
            title = title,
            body = "Test body",
            state = PRState.OPEN,
            author = User(
                id = 1,
                login = "testuser",
                name = "Test User",
                email = null,
                avatarUrl = "https://example.com/avatar.png",
                htmlUrl = "https://github.com/testuser",
                type = UserType.USER
            ),
            createdAt = createdAt,
            updatedAt = updatedAt,
            mergedAt = null,
            closedAt = null,
            mergeable = true,
            merged = false,
            draft = false,
            reviewDecision = null,
            changedFiles = 1,
            additions = 10,
            deletions = 5,
            commits = 1,
            headRef = "feature/test",
            baseRef = "main",
            htmlUrl = "https://github.com/testuser/test-repo/pull/$number"
        )
    }
}
