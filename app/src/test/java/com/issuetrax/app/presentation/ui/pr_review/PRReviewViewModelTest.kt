package com.issuetrax.app.presentation.ui.pr_review

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.PRState
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.User
import com.issuetrax.app.domain.entity.UserType
import com.issuetrax.app.domain.repository.GitHubRepository
import com.issuetrax.app.domain.usecase.ReviewEvent
import com.issuetrax.app.domain.usecase.SubmitReviewUseCase
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

/**
 * Unit tests for PRReviewViewModel
 * 
 * Following Classic TDD approach:
 * - Test ViewModel behavior in isolation
 * - Mock only external dependencies (Repository and UseCase)
 * - Verify state transitions and business logic
 * - Use real data classes instead of mocks where possible
 */
@ExperimentalCoroutinesApi
class PRReviewViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var gitHubRepository: GitHubRepository
    private lateinit var submitReviewUseCase: SubmitReviewUseCase
    private lateinit var approvePullRequestUseCase: com.issuetrax.app.domain.usecase.ApprovePullRequestUseCase
    private lateinit var closePullRequestUseCase: com.issuetrax.app.domain.usecase.ClosePullRequestUseCase
    private lateinit var viewModel: PRReviewViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gitHubRepository = mockk()
        submitReviewUseCase = mockk()
        approvePullRequestUseCase = mockk()
        closePullRequestUseCase = mockk()
        viewModel = PRReviewViewModel(gitHubRepository, submitReviewUseCase, approvePullRequestUseCase, closePullRequestUseCase)
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
        assertFalse("Should not be submitting review", state.isSubmittingReview)
        assertNull("Pull request should be null", state.pullRequest)
        assertTrue("Files should be empty", state.files.isEmpty())
        assertEquals("Current file index should be -1", -1, state.currentFileIndex)
        assertFalse("Review submitted should be false", state.reviewSubmitted)
        assertNull("Error should be null", state.error)
        assertNull("Current file should be null", state.currentFile)
        assertEquals("View state should be FILE_LIST", PRReviewViewState.FILE_LIST, state.viewState)
        assertEquals("Selected chunk index should be -1", -1, state.selectedChunkIndex)
    }
    
    @Test
    fun `loadPullRequest should update state to loading then success with PR and files`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified"),
            createTestFileDiff("file2.kt", "added")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        // When
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNotNull("Pull request should be loaded", state.pullRequest)
        assertEquals("PR number should match", prNumber, state.pullRequest?.number)
        assertEquals("Files should be loaded", 2, state.files.size)
        assertEquals("Current file index should be 0", 0, state.currentFileIndex)
        assertNull("Error should be null", state.error)
        assertNotNull("Current file should be set", state.currentFile)
        assertEquals("Current file should be first file", "file1.kt", state.currentFile?.filename)
        
        coVerify { gitHubRepository.getPullRequest(owner, repo, prNumber) }
        coVerify { gitHubRepository.getPullRequestFiles(owner, repo, prNumber) }
    }
    
    @Test
    fun `loadPullRequest should handle error when loading PR fails`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val errorMessage = "Failed to fetch PR"
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.failure(RuntimeException(errorMessage))
        
        // When
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNull("Pull request should be null", state.pullRequest)
        assertTrue("Files should be empty", state.files.isEmpty())
        assertEquals("Error message should be set", errorMessage, state.error)
        
        coVerify { gitHubRepository.getPullRequest(owner, repo, prNumber) }
    }
    
    @Test
    fun `loadPullRequest should handle error when loading files fails`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val errorMessage = "Failed to load files"
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.failure(RuntimeException(errorMessage))
        
        // When
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNotNull("Pull request should be loaded", state.pullRequest)
        assertTrue("Files should be empty", state.files.isEmpty())
        assertEquals("Error message should be set", errorMessage, state.error)
        
        coVerify { gitHubRepository.getPullRequest(owner, repo, prNumber) }
        coVerify { gitHubRepository.getPullRequestFiles(owner, repo, prNumber) }
    }
    
    @Test
    fun `loadPullRequest should set currentFileIndex to -1 when no files`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(emptyList())
        
        // When
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals("Current file index should be -1", -1, state.currentFileIndex)
        assertNull("Current file should be null", state.currentFile)
    }
    
    @Test
    fun `navigateToNextFile should increment currentFileIndex`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified"),
            createTestFileDiff("file2.kt", "added"),
            createTestFileDiff("file3.kt", "deleted")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // When
        viewModel.navigateToNextFile()
        
        // Then
        assertEquals("Current file index should be 1", 1, viewModel.uiState.value.currentFileIndex)
        assertEquals("Current file should be second file", "file2.kt", viewModel.uiState.value.currentFile?.filename)
    }
    
    @Test
    fun `navigateToNextFile should not go beyond last file`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified"),
            createTestFileDiff("file2.kt", "added")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Navigate to last file
        viewModel.navigateToNextFile()
        
        // When - try to navigate past last file
        viewModel.navigateToNextFile()
        
        // Then
        assertEquals("Current file index should stay at 1", 1, viewModel.uiState.value.currentFileIndex)
    }
    
    @Test
    fun `navigateToPreviousFile should decrement currentFileIndex`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified"),
            createTestFileDiff("file2.kt", "added")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Navigate to second file first
        viewModel.navigateToNextFile()
        
        // When
        viewModel.navigateToPreviousFile()
        
        // Then
        assertEquals("Current file index should be 0", 0, viewModel.uiState.value.currentFileIndex)
        assertEquals("Current file should be first file", "file1.kt", viewModel.uiState.value.currentFile?.filename)
    }
    
    @Test
    fun `navigateToPreviousFile should not go below 0`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // When - try to navigate before first file
        viewModel.navigateToPreviousFile()
        
        // Then
        assertEquals("Current file index should stay at 0", 0, viewModel.uiState.value.currentFileIndex)
    }
    
    @Test
    fun `navigateToFile should set currentFileIndex to specified valid index and change to FILE_DIFF state`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified"),
            createTestFileDiff("file2.kt", "added"),
            createTestFileDiff("file3.kt", "removed")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // When
        viewModel.navigateToFile(2)
        
        // Then
        assertEquals("Current file index should be 2", 2, viewModel.uiState.value.currentFileIndex)
        assertEquals("Current file should be third file", "file3.kt", viewModel.uiState.value.currentFile?.filename)
        assertEquals("View state should be FILE_DIFF", PRReviewViewState.FILE_DIFF, viewModel.uiState.value.viewState)
    }
    
    @Test
    fun `navigateToFile should ignore negative index`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified"),
            createTestFileDiff("file2.kt", "added")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // When
        viewModel.navigateToFile(-1)
        
        // Then
        assertEquals("Current file index should stay at 0", 0, viewModel.uiState.value.currentFileIndex)
    }
    
    @Test
    fun `navigateToFile should ignore index beyond files size`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified"),
            createTestFileDiff("file2.kt", "added")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // When
        viewModel.navigateToFile(10)
        
        // Then
        assertEquals("Current file index should stay at 0", 0, viewModel.uiState.value.currentFileIndex)
    }
    
    @Test
    fun `submitReview should update state to submitting then success`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val body = "LGTM"
        val event = ReviewEvent.APPROVE
        val mockReview = mockk<com.issuetrax.app.domain.entity.Review>()
        
        coEvery { 
            submitReviewUseCase(owner, repo, prNumber, body, event) 
        } returns Result.success(mockReview)
        
        // When
        viewModel.submitReview(owner, repo, prNumber, body, event)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be submitting review", state.isSubmittingReview)
        assertTrue("Review submitted should be true", state.reviewSubmitted)
        assertNull("Error should be null", state.error)
        
        coVerify { submitReviewUseCase(owner, repo, prNumber, body, event) }
    }
    
    @Test
    fun `submitReview should handle submission error`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val body = "LGTM"
        val event = ReviewEvent.APPROVE
        val errorMessage = "Submission failed"
        
        coEvery { 
            submitReviewUseCase(owner, repo, prNumber, body, event) 
        } returns Result.failure(RuntimeException(errorMessage))
        
        // When
        viewModel.submitReview(owner, repo, prNumber, body, event)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be submitting review", state.isSubmittingReview)
        assertFalse("Review submitted should be false", state.reviewSubmitted)
        assertEquals("Error message should be set", errorMessage, state.error)
        
        coVerify { submitReviewUseCase(owner, repo, prNumber, body, event) }
    }
    
    @Test
    fun `clearError should set error to null`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.failure(RuntimeException("Test error"))
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Verify error is set
        assertEquals("Error should be set", "Test error", viewModel.uiState.value.error)
        
        // When
        viewModel.clearError()
        
        // Then
        assertNull("Error should be null", viewModel.uiState.value.error)
    }
    
    @Test
    fun `currentFile computed property should return null when index is -1`() {
        // Given
        val state = PRReviewUiState(
            files = listOf(createTestFileDiff("file1.kt", "modified")),
            currentFileIndex = -1
        )
        
        // Then
        assertNull("Current file should be null", state.currentFile)
    }
    
    @Test
    fun `currentFile computed property should return null when index is out of bounds`() {
        // Given
        val state = PRReviewUiState(
            files = listOf(createTestFileDiff("file1.kt", "modified")),
            currentFileIndex = 5
        )
        
        // Then
        assertNull("Current file should be null", state.currentFile)
    }
    
    @Test
    fun `currentFile computed property should return file at current index`() {
        // Given
        val files = listOf(
            createTestFileDiff("file1.kt", "modified"),
            createTestFileDiff("file2.kt", "added")
        )
        val state = PRReviewUiState(
            files = files,
            currentFileIndex = 1
        )
        
        // Then
        assertNotNull("Current file should not be null", state.currentFile)
        assertEquals("Current file should be second file", "file2.kt", state.currentFile?.filename)
    }
    
    @Test
    fun `returnToFileList should change view state to FILE_LIST`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified"),
            createTestFileDiff("file2.kt", "added")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Navigate to a file first (goes to FILE_DIFF state)
        viewModel.navigateToFile(0)
        assertEquals("View state should be FILE_DIFF", PRReviewViewState.FILE_DIFF, viewModel.uiState.value.viewState)
        
        // When
        viewModel.returnToFileList()
        
        // Then
        assertEquals("View state should be FILE_LIST", PRReviewViewState.FILE_LIST, viewModel.uiState.value.viewState)
    }
    
    @Test
    fun `showChunkDetail should change view state to CHUNK_DETAIL and set selectedChunkIndex`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Navigate to file first
        viewModel.navigateToFile(0)
        
        // When
        viewModel.showChunkDetail(2)
        
        // Then
        assertEquals("View state should be CHUNK_DETAIL", PRReviewViewState.CHUNK_DETAIL, viewModel.uiState.value.viewState)
        assertEquals("Selected chunk index should be 2", 2, viewModel.uiState.value.selectedChunkIndex)
    }
    
    @Test
    fun `returnToFileDiff should change view state to FILE_DIFF and reset selectedChunkIndex`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 123
        val mockPR = createTestPullRequest(prNumber, "Test PR")
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", "modified")
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Navigate to file and then to chunk detail
        viewModel.navigateToFile(0)
        viewModel.showChunkDetail(2)
        assertEquals("View state should be CHUNK_DETAIL", PRReviewViewState.CHUNK_DETAIL, viewModel.uiState.value.viewState)
        
        // When
        viewModel.returnToFileDiff()
        
        // Then
        assertEquals("View state should be FILE_DIFF", PRReviewViewState.FILE_DIFF, viewModel.uiState.value.viewState)
        assertEquals("Selected chunk index should be -1", -1, viewModel.uiState.value.selectedChunkIndex)
    }
    
    // Helper functions to create test data
    private fun createTestPullRequest(
        number: Int,
        title: String
    ): PullRequest {
        return PullRequest(
            id = number.toLong(),
            number = number,
            title = title,
            body = "Test PR body",
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
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            mergedAt = null,
            closedAt = null,
            mergeable = true,
            merged = false,
            draft = false,
            reviewDecision = null,
            changedFiles = 2,
            additions = 10,
            deletions = 5,
            commits = 1,
            headRef = "feature/test",
            baseRef = "main",
            htmlUrl = "https://github.com/testuser/test-repo/pull/$number"
        )
    }
    
    private fun createTestFileDiff(
        filename: String,
        status: String
    ): FileDiff {
        val fileStatus = when (status) {
            "added" -> com.issuetrax.app.domain.entity.FileStatus.ADDED
            "modified" -> com.issuetrax.app.domain.entity.FileStatus.MODIFIED
            "deleted" -> com.issuetrax.app.domain.entity.FileStatus.REMOVED
            "renamed" -> com.issuetrax.app.domain.entity.FileStatus.RENAMED
            else -> com.issuetrax.app.domain.entity.FileStatus.CHANGED
        }
        return FileDiff(
            filename = filename,
            status = fileStatus,
            additions = 10,
            deletions = 5,
            changes = 15,
            blobUrl = "https://github.com/test/test/blob/abc123/$filename",
            rawUrl = "https://github.com/test/test/raw/abc123/$filename",
            patch = "@@ -1,3 +1,3 @@\n-old line\n+new line\n context"
        )
    }
}
