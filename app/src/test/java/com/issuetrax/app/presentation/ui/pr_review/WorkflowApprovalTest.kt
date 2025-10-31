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
        viewModel = PRReviewViewModel(
            gitHubRepository,
            submitReviewUseCase,
            approvePullRequestUseCase,
            closePullRequestUseCase,
            mergePullRequestUseCase,
            createCommentUseCase,
            getCommitStatusUseCase,
            getWorkflowRunsUseCase,
            approveWorkflowRunUseCase
        )
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `loadWorkflowRuns updates state with workflow runs`() = runTest {
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
            )
        )
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", "waiting") 
        } returns Result.success(mockWorkflowRuns)
        
        // When
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals(mockWorkflowRuns, state.workflowRuns)
        coVerify { getWorkflowRunsUseCase(owner, repo, "pull_request", "waiting") }
    }
    
    @Test
    fun `loadWorkflowRuns handles empty workflow runs`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", "waiting") 
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
            getWorkflowRunsUseCase(owner, repo, "pull_request", "waiting") 
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
    fun `approveWorkflowRun approves first waiting run`() = runTest {
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
        
        // Set initial state with workflow runs
        viewModel.uiState.value.let { state ->
            viewModel.loadWorkflowRuns(owner, repo)
        }
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", "waiting") 
        } returns Result.success(listOf(waitingRun))
        
        coEvery { 
            approveWorkflowRunUseCase(owner, repo, 123456L) 
        } returns Result.success(Unit)
        
        // Load workflow runs first
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.approveWorkflowRun(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals("Workflow run approved successfully", state.actionMessage)
        coVerify { approveWorkflowRunUseCase(owner, repo, 123456L) }
    }
    
    @Test
    fun `approveWorkflowRun shows message when no waiting runs`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        
        coEvery { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", "waiting") 
        } returns Result.success(emptyList())
        
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.approveWorkflowRun(owner, repo)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals("No workflow runs require approval", state.actionMessage)
    }
    
    @Test
    fun `approveWorkflowRun handles approval failure`() = runTest {
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
            getWorkflowRunsUseCase(owner, repo, "pull_request", "waiting") 
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
            getWorkflowRunsUseCase(owner, repo, "pull_request", "waiting") 
        } returns Result.success(listOf(waitingRun)) andThen Result.success(emptyList())
        
        coEvery { 
            approveWorkflowRunUseCase(owner, repo, 123456L) 
        } returns Result.success(Unit)
        
        viewModel.loadWorkflowRuns(owner, repo)
        advanceUntilIdle()
        
        // When
        viewModel.approveWorkflowRun(owner, repo)
        advanceUntilIdle()
        
        // Then
        coVerify(exactly = 2) { 
            getWorkflowRunsUseCase(owner, repo, "pull_request", "waiting") 
        }
    }
}
