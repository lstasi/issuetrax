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
        // Mock getCheckRuns to return failure by default (no checks available)
        coEvery { gitHubRepository.getCheckRuns(any(), any(), any()) } returns Result.failure(Exception("No checks"))
        // Mock getWorkflowRunsForPR to return empty list by default
        coEvery { gitHubRepository.getWorkflowRunsForPR(any(), any(), any()) } returns Result.success(emptyList())
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
        assertFalse("Should not be loading release", state.isLoadingRelease)
        assertTrue("Repositories should be empty", state.repositories.isEmpty())
        assertTrue("Pull requests should be empty", state.pullRequests.isEmpty())
        assertNull("Selected repository should be null", state.selectedRepository)
        assertEquals("Filter should be OPEN", PRFilter.OPEN, state.filter)
        assertEquals("Sort order should be UPDATED", PRSortOrder.UPDATED, state.sortBy)
        assertNull("Error should be null", state.error)
        assertNull("Latest release should be null", state.latestRelease)
        assertFalse("Build actions sheet should not be shown", state.showBuildActionsSheet)
        assertNull("Selected PR for build actions should be null", state.selectedPrForBuildActions)
        assertTrue("Workflow runs should be empty", state.workflowRuns.isEmpty())
        assertFalse("Should not be loading workflow runs", state.isLoadingWorkflowRuns)
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
    
    @Test
    fun `loadLatestRelease should update state with release on success`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val mockRelease = com.issuetrax.app.domain.entity.Release(
            id = 1,
            tagName = "v1.0.0",
            name = "Release 1.0.0",
            body = "First release",
            htmlUrl = "https://github.com/testuser/test-repo/releases/tag/v1.0.0",
            draft = false,
            prerelease = false,
            createdAt = LocalDateTime.now().minusDays(1),
            publishedAt = LocalDateTime.now().minusDays(1),
            assets = listOf(
                com.issuetrax.app.domain.entity.ReleaseAsset(
                    id = 1,
                    name = "app-debug.apk",
                    label = "Debug APK",
                    contentType = "application/vnd.android.package-archive",
                    size = 1024000,
                    downloadCount = 10,
                    browserDownloadUrl = "https://github.com/testuser/test-repo/releases/download/v1.0.0/app-debug.apk",
                    createdAt = LocalDateTime.now().minusDays(1),
                    updatedAt = LocalDateTime.now().minusDays(1)
                )
            ),
            author = User(
                id = 1,
                login = "testuser",
                name = "Test User",
                email = null,
                avatarUrl = "https://example.com/avatar.png",
                htmlUrl = "https://github.com/testuser",
                type = UserType.USER
            )
        )
        
        coEvery { 
            gitHubRepository.getLatestRelease(owner, repo) 
        } returns Result.success(mockRelease)
        
        // When
        viewModel.loadLatestRelease(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be loading release", state.isLoadingRelease)
        assertEquals("Latest release should be set", mockRelease, state.latestRelease)
        
        coVerify { gitHubRepository.getLatestRelease(owner, repo) }
    }
    
    @Test
    fun `loadLatestRelease should update state with null release on failure`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        coEvery { 
            gitHubRepository.getLatestRelease(owner, repo) 
        } returns Result.failure(RuntimeException("Not found"))
        
        // When
        viewModel.loadLatestRelease(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be loading release", state.isLoadingRelease)
        assertNull("Latest release should be null", state.latestRelease)
        
        coVerify { gitHubRepository.getLatestRelease(owner, repo) }
    }
    
    @Test
    fun `showBuildActionsSheet should set state and load workflow runs on success`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val headSha = "abc123"
        val pullRequest = createTestPullRequest(1, "Test PR", LocalDateTime.now())
            .copy(headRef = headSha)
        val mockWorkflowRuns = listOf(
            com.issuetrax.app.domain.entity.WorkflowRun(
                id = 1,
                name = "Build",
                status = "completed",
                conclusion = "success",
                headSha = headSha,
                htmlUrl = "https://github.com/testuser/test-repo/actions/runs/1"
            )
        )
        
        coEvery { 
            gitHubRepository.getWorkflowRunsForPR(owner, repo, headSha) 
        } returns Result.success(mockWorkflowRuns)
        
        // When
        viewModel.showBuildActionsSheet(owner, repo, pullRequest)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue("Build actions sheet should be shown", state.showBuildActionsSheet)
        assertEquals("Selected PR should be set", pullRequest, state.selectedPrForBuildActions)
        assertFalse("Should not be loading workflow runs", state.isLoadingWorkflowRuns)
        assertEquals("Should have 1 workflow run", 1, state.workflowRuns.size)
        assertEquals("Workflow run should match", mockWorkflowRuns[0], state.workflowRuns[0])
        
        coVerify { gitHubRepository.getWorkflowRunsForPR(owner, repo, headSha) }
    }
    
    @Test
    fun `showBuildActionsSheet should set state and handle empty workflow runs`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val headSha = "abc123"
        val pullRequest = createTestPullRequest(1, "Test PR", LocalDateTime.now())
            .copy(headRef = headSha)
        
        coEvery { 
            gitHubRepository.getWorkflowRunsForPR(owner, repo, headSha) 
        } returns Result.success(emptyList())
        
        // When
        viewModel.showBuildActionsSheet(owner, repo, pullRequest)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue("Build actions sheet should be shown", state.showBuildActionsSheet)
        assertTrue("Workflow runs should be empty", state.workflowRuns.isEmpty())
        assertFalse("Should not be loading workflow runs", state.isLoadingWorkflowRuns)
        
        coVerify { gitHubRepository.getWorkflowRunsForPR(owner, repo, headSha) }
    }
    
    @Test
    fun `showBuildActionsSheet should set state and handle failure`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val headSha = "abc123"
        val pullRequest = createTestPullRequest(1, "Test PR", LocalDateTime.now())
            .copy(headRef = headSha)
        
        coEvery { 
            gitHubRepository.getWorkflowRunsForPR(owner, repo, headSha) 
        } returns Result.failure(RuntimeException("API Error"))
        
        // When
        viewModel.showBuildActionsSheet(owner, repo, pullRequest)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue("Build actions sheet should be shown", state.showBuildActionsSheet)
        assertTrue("Workflow runs should be empty on failure", state.workflowRuns.isEmpty())
        assertFalse("Should not be loading workflow runs", state.isLoadingWorkflowRuns)
        
        coVerify { gitHubRepository.getWorkflowRunsForPR(owner, repo, headSha) }
    }
    
    @Test
    fun `hideBuildActionsSheet should reset build actions state`() = runTest {
        // Given - First show the sheet
        val owner = "testuser"
        val repo = "test-repo"
        val headSha = "abc123"
        val pullRequest = createTestPullRequest(1, "Test PR", LocalDateTime.now())
            .copy(headRef = headSha)
        val mockWorkflowRuns = listOf(
            com.issuetrax.app.domain.entity.WorkflowRun(
                id = 1,
                name = "Build",
                status = "completed",
                conclusion = "success",
                headSha = headSha,
                htmlUrl = "https://github.com/testuser/test-repo/actions/runs/1"
            )
        )
        
        coEvery { 
            gitHubRepository.getWorkflowRunsForPR(owner, repo, headSha) 
        } returns Result.success(mockWorkflowRuns)
        
        viewModel.showBuildActionsSheet(owner, repo, pullRequest)
        advanceUntilIdle()
        
        // Verify sheet is shown
        assertTrue("Build actions sheet should be shown", viewModel.uiState.value.showBuildActionsSheet)
        
        // When
        viewModel.hideBuildActionsSheet()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Build actions sheet should be hidden", state.showBuildActionsSheet)
        assertNull("Selected PR should be null", state.selectedPrForBuildActions)
        assertTrue("Workflow runs should be cleared", state.workflowRuns.isEmpty())
    }
}
