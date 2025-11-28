package com.issuetrax.app.presentation.ui.pr_review

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.issuetrax.app.domain.entity.WorkflowRun
import com.issuetrax.app.domain.repository.GitHubRepository
import com.issuetrax.app.domain.usecase.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for workflow approval functionality in PRReviewViewModel.
 */
@ExperimentalCoroutinesApi
class WorkflowApprovalTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var gitHubRepository: GitHubRepository
    private lateinit var submitReviewUseCase: SubmitReviewUseCase
    private lateinit var approvePullRequestUseCase: ApprovePullRequestUseCase
    private lateinit var closePullRequestUseCase: ClosePullRequestUseCase
    private lateinit var mergePullRequestUseCase: MergePullRequestUseCase
    private lateinit var createCommentUseCase: CreateCommentUseCase
    private lateinit var getCommitStatusUseCase: GetCommitStatusUseCase
    private lateinit var getWorkflowRunsUseCase: GetWorkflowRunsUseCase
    private lateinit var approveWorkflowRunUseCase: ApproveWorkflowRunUseCase
    private lateinit var rerunWorkflowUseCase: RerunWorkflowUseCase
    private lateinit var viewModel: PRReviewViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gitHubRepository = mockk()
        submitReviewUseCase = mockk()
        approvePullRequestUseCase = mockk()
        closePullRequestUseCase = mockk()
        mergePullRequestUseCase = mockk()
        createCommentUseCase = mockk()
        getCommitStatusUseCase = mockk()
        getWorkflowRunsUseCase = mockk()
        approveWorkflowRunUseCase = mockk()
        rerunWorkflowUseCase = mockk()
        viewModel = PRReviewViewModel(
            gitHubRepository,
            submitReviewUseCase,
            approvePullRequestUseCase,
            closePullRequestUseCase,
            mergePullRequestUseCase,
            createCommentUseCase,
            getCommitStatusUseCase,
            getWorkflowRunsUseCase,
            approveWorkflowRunUseCase,
            rerunWorkflowUseCase
        )
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `loadWorkflowRuns updates state with all workflow runs`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val mockWorkflowRuns = listOf(
            WorkflowRun(
                id = 123456L,
                name = "CI",
                status = "waiting",
                conclusion = null,
                headSha = "abc123",
                htmlUrl = "https://github.com/owner/repo/actions/runs/123456"
            ),
            WorkflowRun(
                id = 789012L,
                name = "Tests",
                status = "completed",
                conclusion = "success",
                headSha = "abc123",
                htmlUrl = "https://github.com/owner/repo/actions/runs/789012"
            )
        )
        
        // No status filter - get all workflow runs for pull_request
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", null) 
        } returns Result.success(mockWorkflowRuns)
        
        // When
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // Then - all runs should be stored (no filtering)
        val state = viewModel.uiState.value
        assertEquals(2, state.workflowRuns.size)
        coVerify { getWorkflowRunsUseCase(owner, repo, "pull_request", null) }
    }
    
    @Test
    fun `approveWorkflowRun prioritizes waiting status`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val mockWorkflowRuns = listOf(
            WorkflowRun(
                id = 1L,
                name = "CI",
                status = "completed",
                conclusion = "success",
                headSha = "abc123",
                htmlUrl = "https://github.com/owner/repo/actions/runs/1"
            ),
            WorkflowRun(
                id = 2L,
                name = "Deploy",
                status = "waiting",
                conclusion = null,
                headSha = "abc123",
                htmlUrl = "https://github.com/owner/repo/actions/runs/2"
            ),
            WorkflowRun(
                id = 3L,
                name = "Build",
                status = "action_required",
                conclusion = null,
                headSha = "abc123",
                htmlUrl = "https://github.com/owner/repo/actions/runs/3"
            )
        )
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", null) 
        } returns Result.success(mockWorkflowRuns)
        
        // When
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // Then - all runs should be stored
        val state = viewModel.uiState.value
        assertEquals(3, state.workflowRuns.size)
    }
    
    @Test
    fun `loadWorkflowRuns handles empty workflow runs`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", null) 
        } returns Result.success(emptyList())
        
        // When
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state.workflowRuns.isEmpty())
    }
    
    @Test
    fun `loadWorkflowRuns silently fails on error`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", null) 
        } returns Result.failure(Exception("Network error"))
        
        // When
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state.workflowRuns.isEmpty())
        assertNull(state.error) // Should fail silently
    }
    
    @Test
    fun `approveWorkflowRun approves waiting run with highest priority`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val waitingRun = WorkflowRun(
            id = 123456L,
            name = "CI",
            status = "waiting",
            conclusion = null,
            headSha = "abc123",
            htmlUrl = "https://github.com/owner/repo/actions/runs/123456"
        )
        val actionRequiredRun = WorkflowRun(
            id = 789012L,
            name = "Deploy",
            status = "action_required",
            conclusion = null,
            headSha = "abc123",
            htmlUrl = "https://github.com/owner/repo/actions/runs/789012"
        )
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", null) 
        } returns Result.success(listOf(actionRequiredRun, waitingRun))
        
        coEvery { 
            approveWorkflowRunUseCase(owner, repo, 123456L) 
        } returns Result.success(Unit)
        
        // Load workflow runs first
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // When - should approve the "waiting" run (highest priority)
        viewModel.approveWorkflowRun(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals("Workflow 'CI' approved successfully", state.actionMessage)
        coVerify { approveWorkflowRunUseCase(owner, repo, 123456L) }
    }
    
    @Test
    fun `approveWorkflowRun suggests rerun when no waiting runs exist`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val completedRun = WorkflowRun(
            id = 789012L,
            name = "Tests",
            status = "completed",
            conclusion = "success",
            headSha = "abc123",
            htmlUrl = "https://github.com/owner/repo/actions/runs/789012"
        )
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", null) 
        } returns Result.success(listOf(completedRun))
        
        // Load workflow runs first
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // When - no waiting runs, suggests rerun instead
        viewModel.approveWorkflowRun(owner, repo)
        advanceUntilIdle()
        
        // Then - should suggest using re-run for non-fork PRs
        val state = viewModel.uiState.value
        assertEquals("No workflows need approval. Use re-run for non-fork PRs.", state.actionMessage)
    }
    
    @Test
    fun `approveWorkflowRun shows message when no runs exist`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", null) 
        } returns Result.success(emptyList())
        
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // When - no workflow runs at all
        viewModel.approveWorkflowRun(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals("No workflow runs found", state.actionMessage)
    }
    
    @Test
    fun `approveWorkflowRun handles approval failure with run name`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val waitingRun = WorkflowRun(
            id = 123456L,
            name = "CI",
            status = "waiting",
            conclusion = null,
            headSha = "abc123",
            htmlUrl = "https://github.com/owner/repo/actions/runs/123456"
        )
        val errorMessage = "Permission denied"
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", null) 
        } returns Result.success(listOf(waitingRun))
        
        coEvery { 
            approveWorkflowRunUseCase(owner, repo, 123456L) 
        } returns Result.failure(Exception(errorMessage))
        
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.approveWorkflowRun(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals(errorMessage, state.error)
        assertFalse(state.isSubmittingReview)
    }
    
    @Test
    fun `approveWorkflowRun reloads workflow runs after successful approval`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val waitingRun = WorkflowRun(
            id = 123456L,
            name = "CI",
            status = "waiting",
            conclusion = null,
            headSha = "abc123",
            htmlUrl = "https://github.com/owner/repo/actions/runs/123456"
        )
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", null) 
        } returns Result.success(listOf(waitingRun)) andThen Result.success(emptyList())
        
        coEvery { 
            approveWorkflowRunUseCase(owner, repo, 123456L) 
        } returns Result.success(Unit)
        
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.approveWorkflowRun(owner, repo)
        advanceUntilIdle()
        
        // Then - verify workflow runs were loaded twice (initial + after approval)
        coVerify(exactly = 2) { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", null) 
        }
    }
}
