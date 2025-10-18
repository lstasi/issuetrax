package com.issuetrax.app.presentation.ui.pr_review

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.FileStatus
import com.issuetrax.app.domain.entity.PRState
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.User
import com.issuetrax.app.domain.entity.UserType
import com.issuetrax.app.domain.repository.GitHubRepository
import com.issuetrax.app.domain.usecase.SubmitReviewUseCase
import io.mockk.coEvery
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
 * Integration tests for PR Review workflow (Task 2.5 Testing & Validation)
 * 
 * These tests validate the complete PR review functionality including:
 * - Loading PRs with different numbers of files (1, many)
 * - File navigation across different scenarios
 * - Error handling for various failure cases
 * - Data integrity and state consistency
 * 
 * Following Classic TDD approach with comprehensive scenario coverage.
 */
@ExperimentalCoroutinesApi
class PRReviewIntegrationTest {
    
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
    
    // ========== Test 1: PR with single file ==========
    
    @Test
    fun `SCENARIO 1 - Load PR with single file and verify state`() = runTest {
        // Given - A PR with exactly 1 file
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 1
        val mockPR = createTestPullRequest(prNumber, "Single File PR", changedFiles = 1)
        val mockFiles = listOf(
            createTestFileDiff("README.md", FileStatus.MODIFIED, additions = 10, deletions = 2)
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        // When - Load the PR
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Then - Verify initial state
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNull("Should have no error", state.error)
        assertNotNull("PR should be loaded", state.pullRequest)
        assertEquals("Should have 1 file", 1, state.files.size)
        assertEquals("Current file index should be 0", 0, state.currentFileIndex)
        assertNotNull("Current file should be set", state.currentFile)
        assertEquals("Current file should be README.md", "README.md", state.currentFile?.filename)
        
        // Verify navigation buttons should be disabled (only 1 file)
        val canGoNext = state.currentFileIndex < state.files.size - 1
        val canGoPrevious = state.currentFileIndex > 0
        assertFalse("Next button should be disabled", canGoNext)
        assertFalse("Previous button should be disabled", canGoPrevious)
    }
    
    @Test
    fun `SCENARIO 1 - Navigation should not change index when PR has single file`() = runTest {
        // Given - A PR with 1 file loaded
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 2
        val mockPR = createTestPullRequest(prNumber, "Single File PR", changedFiles = 1)
        val mockFiles = listOf(
            createTestFileDiff("main.kt", FileStatus.ADDED, additions = 50, deletions = 0)
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        val initialIndex = viewModel.uiState.value.currentFileIndex
        
        // When - Try to navigate next
        viewModel.navigateToNextFile()
        
        // Then - Index should stay the same
        assertEquals("Index should remain at 0", initialIndex, viewModel.uiState.value.currentFileIndex)
        assertEquals("Current file should still be main.kt", "main.kt", viewModel.uiState.value.currentFile?.filename)
        
        // When - Try to navigate previous
        viewModel.navigateToPreviousFile()
        
        // Then - Index should still be the same
        assertEquals("Index should remain at 0", initialIndex, viewModel.uiState.value.currentFileIndex)
    }
    
    // ========== Test 2: PR with many files ==========
    
    @Test
    fun `SCENARIO 2 - Load PR with many files and verify complete file list`() = runTest {
        // Given - A PR with 10 files
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 100
        val fileCount = 10
        val mockPR = createTestPullRequest(prNumber, "Multi-File PR", changedFiles = fileCount)
        val mockFiles = (1..fileCount).map { index ->
            createTestFileDiff(
                "file$index.kt",
                if (index % 3 == 0) FileStatus.ADDED 
                else if (index % 3 == 1) FileStatus.MODIFIED 
                else FileStatus.REMOVED,
                additions = index * 5,
                deletions = index * 2
            )
        }
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        // When - Load the PR
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Then - Verify state
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNull("Should have no error", state.error)
        assertEquals("Should have 10 files", fileCount, state.files.size)
        assertEquals("Current file index should be 0", 0, state.currentFileIndex)
        assertEquals("First file should be file1.kt", "file1.kt", state.currentFile?.filename)
        
        // Verify all files are present
        state.files.forEachIndexed { index, file ->
            assertEquals("File ${index + 1} should have correct name", "file${index + 1}.kt", file.filename)
        }
        
        // Verify navigation state
        assertTrue("Next button should be enabled", state.currentFileIndex < state.files.size - 1)
        assertFalse("Previous button should be disabled at start", state.currentFileIndex > 0)
    }
    
    @Test
    fun `SCENARIO 2 - Navigate through all files in large PR sequentially`() = runTest {
        // Given - A PR with 5 files
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 200
        val mockPR = createTestPullRequest(prNumber, "Navigation Test PR", changedFiles = 5)
        val mockFiles = listOf(
            createTestFileDiff("ComponentA.kt", FileStatus.MODIFIED),
            createTestFileDiff("ComponentB.kt", FileStatus.ADDED),
            createTestFileDiff("ComponentC.kt", FileStatus.MODIFIED),
            createTestFileDiff("ComponentD.kt", FileStatus.REMOVED),
            createTestFileDiff("ComponentE.kt", FileStatus.ADDED)
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // When/Then - Navigate forward through all files
        for (i in 0 until mockFiles.size) {
            val state = viewModel.uiState.value
            assertEquals("Should be at file $i", i, state.currentFileIndex)
            assertEquals("Should show correct file", mockFiles[i].filename, state.currentFile?.filename)
            
            if (i < mockFiles.size - 1) {
                viewModel.navigateToNextFile()
            }
        }
        
        // Should be at last file
        assertEquals("Should be at last file", mockFiles.size - 1, viewModel.uiState.value.currentFileIndex)
        
        // Try to go beyond last file
        viewModel.navigateToNextFile()
        assertEquals("Should stay at last file", mockFiles.size - 1, viewModel.uiState.value.currentFileIndex)
        
        // Navigate backward through all files
        for (i in (mockFiles.size - 1) downTo 0) {
            val state = viewModel.uiState.value
            assertEquals("Should be at file $i going back", i, state.currentFileIndex)
            
            if (i > 0) {
                viewModel.navigateToPreviousFile()
            }
        }
        
        // Should be back at first file
        assertEquals("Should be at first file", 0, viewModel.uiState.value.currentFileIndex)
        
        // Try to go before first file
        viewModel.navigateToPreviousFile()
        assertEquals("Should stay at first file", 0, viewModel.uiState.value.currentFileIndex)
    }
    
    // ========== Test 3: File navigation scenarios ==========
    
    @Test
    fun `SCENARIO 3 - Jump to specific file using navigateToFile`() = runTest {
        // Given - A PR with multiple files
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 300
        val mockPR = createTestPullRequest(prNumber, "Jump Navigation PR", changedFiles = 8)
        val mockFiles = (0..7).map { index ->
            createTestFileDiff("module$index/File.kt", FileStatus.MODIFIED)
        }
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // When - Jump to file at index 5
        viewModel.navigateToFile(5)
        
        // Then
        assertEquals("Should jump to index 5", 5, viewModel.uiState.value.currentFileIndex)
        assertEquals("Should show correct file", "module5/File.kt", viewModel.uiState.value.currentFile?.filename)
        
        // When - Jump to first file
        viewModel.navigateToFile(0)
        
        // Then
        assertEquals("Should jump to index 0", 0, viewModel.uiState.value.currentFileIndex)
        assertEquals("Should show first file", "module0/File.kt", viewModel.uiState.value.currentFile?.filename)
        
        // When - Jump to last file
        viewModel.navigateToFile(7)
        
        // Then
        assertEquals("Should jump to index 7", 7, viewModel.uiState.value.currentFileIndex)
        assertEquals("Should show last file", "module7/File.kt", viewModel.uiState.value.currentFile?.filename)
    }
    
    @Test
    fun `SCENARIO 3 - Invalid file index should be ignored`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 301
        val mockPR = createTestPullRequest(prNumber, "Invalid Index PR", changedFiles = 3)
        val mockFiles = listOf(
            createTestFileDiff("file1.kt", FileStatus.MODIFIED),
            createTestFileDiff("file2.kt", FileStatus.MODIFIED),
            createTestFileDiff("file3.kt", FileStatus.MODIFIED)
        )
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        val initialIndex = viewModel.uiState.value.currentFileIndex
        
        // When - Try negative index
        viewModel.navigateToFile(-1)
        
        // Then
        assertEquals("Should ignore negative index", initialIndex, viewModel.uiState.value.currentFileIndex)
        
        // When - Try index beyond bounds
        viewModel.navigateToFile(100)
        
        // Then
        assertEquals("Should ignore out of bounds index", initialIndex, viewModel.uiState.value.currentFileIndex)
    }
    
    // ========== Test 4: Error handling ==========
    
    @Test
    fun `SCENARIO 4 - Handle error when PR fails to load`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 400
        val errorMessage = "Network error: Unable to fetch PR"
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.failure(RuntimeException(errorMessage))
        
        // When
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNotNull("Error should be set", state.error)
        assertEquals("Error message should match", errorMessage, state.error)
        assertNull("PR should be null", state.pullRequest)
        assertTrue("Files should be empty", state.files.isEmpty())
        assertEquals("Current file index should be -1", -1, state.currentFileIndex)
        assertNull("Current file should be null", state.currentFile)
    }
    
    @Test
    fun `SCENARIO 4 - Handle error when files fail to load after PR loads successfully`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 401
        val mockPR = createTestPullRequest(prNumber, "PR with file error", changedFiles = 5)
        val errorMessage = "Failed to fetch file list"
        
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
        assertNotNull("Error should be set", state.error)
        assertEquals("Error message should match", errorMessage, state.error)
        assertNotNull("PR should still be loaded", state.pullRequest)
        assertTrue("Files should be empty due to error", state.files.isEmpty())
        assertEquals("Current file index should be -1", -1, state.currentFileIndex)
    }
    
    @Test
    fun `SCENARIO 4 - Handle PR with zero files gracefully`() = runTest {
        // Given - A PR that somehow has no changed files (edge case)
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 402
        val mockPR = createTestPullRequest(prNumber, "Empty PR", changedFiles = 0)
        val mockFiles = emptyList<FileDiff>()
        
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
        assertNull("Should have no error", state.error)
        assertNotNull("PR should be loaded", state.pullRequest)
        assertTrue("Files should be empty", state.files.isEmpty())
        assertEquals("Current file index should be -1", -1, state.currentFileIndex)
        assertNull("Current file should be null", state.currentFile)
        
        // Try navigation - should not crash
        viewModel.navigateToNextFile()
        viewModel.navigateToPreviousFile()
        viewModel.navigateToFile(0)
        
        // State should remain the same
        assertEquals("Index should stay -1", -1, viewModel.uiState.value.currentFileIndex)
    }
    
    @Test
    fun `SCENARIO 4 - Recover from error state by clearing error`() = runTest {
        // Given - An error state
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 403
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.failure(RuntimeException("Initial error"))
        
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        assertNotNull("Error should be set", viewModel.uiState.value.error)
        
        // When - Clear error
        viewModel.clearError()
        
        // Then
        assertNull("Error should be cleared", viewModel.uiState.value.error)
    }
    
    // ========== Test 5: Data integrity verification ==========
    
    @Test
    fun `SCENARIO 5 - Verify all PR metadata is correctly loaded and accessible`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 500
        val prTitle = "Feature: Add new authentication"
        val prBody = "This PR implements OAuth authentication"
        val changedFiles = 7
        val additions = 150
        val deletions = 30
        
        val mockPR = createTestPullRequest(
            prNumber, 
            prTitle, 
            body = prBody,
            changedFiles = changedFiles,
            additions = additions,
            deletions = deletions
        )
        val mockFiles = (1..changedFiles).map { index ->
            createTestFileDiff("src/auth/Module$index.kt", FileStatus.MODIFIED)
        }
        
        coEvery { 
            gitHubRepository.getPullRequest(owner, repo, prNumber) 
        } returns Result.success(mockPR)
        coEvery { 
            gitHubRepository.getPullRequestFiles(owner, repo, prNumber) 
        } returns Result.success(mockFiles)
        
        // When
        viewModel.loadPullRequest(owner, repo, prNumber)
        advanceUntilIdle()
        
        // Then - Verify PR metadata
        val state = viewModel.uiState.value
        val pr = state.pullRequest
        assertNotNull("PR should be loaded", pr)
        assertEquals("PR number should match", prNumber, pr?.number)
        assertEquals("PR title should match", prTitle, pr?.title)
        assertEquals("PR body should match", prBody, pr?.body)
        assertEquals("Changed files count should match", changedFiles, pr?.changedFiles)
        assertEquals("Additions count should match", additions, pr?.additions)
        assertEquals("Deletions count should match", deletions, pr?.deletions)
        assertEquals("Files list size should match changed files", changedFiles, state.files.size)
    }
    
    @Test
    fun `SCENARIO 5 - Verify file diff data contains all required information`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val prNumber = 501
        val mockPR = createTestPullRequest(prNumber, "Data Integrity Test")
        val mockFiles = listOf(
            createTestFileDiff(
                "app/MainActivity.kt", 
                FileStatus.MODIFIED,
                additions = 25,
                deletions = 10
            ),
            createTestFileDiff(
                "app/NewFeature.kt",
                FileStatus.ADDED,
                additions = 100,
                deletions = 0
            ),
            createTestFileDiff(
                "app/OldModule.kt",
                FileStatus.REMOVED,
                additions = 0,
                deletions = 50
            )
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
        
        // Then - Verify each file has complete data
        val state = viewModel.uiState.value
        assertEquals("Should have 3 files", 3, state.files.size)
        
        val modifiedFile = state.files[0]
        assertEquals("Modified file name", "app/MainActivity.kt", modifiedFile.filename)
        assertEquals("Modified file status", FileStatus.MODIFIED, modifiedFile.status)
        assertEquals("Modified file additions", 25, modifiedFile.additions)
        assertEquals("Modified file deletions", 10, modifiedFile.deletions)
        
        val addedFile = state.files[1]
        assertEquals("Added file name", "app/NewFeature.kt", addedFile.filename)
        assertEquals("Added file status", FileStatus.ADDED, addedFile.status)
        assertEquals("Added file additions", 100, addedFile.additions)
        assertEquals("Added file deletions", 0, addedFile.deletions)
        
        val removedFile = state.files[2]
        assertEquals("Removed file name", "app/OldModule.kt", removedFile.filename)
        assertEquals("Removed file status", FileStatus.REMOVED, removedFile.status)
        assertEquals("Removed file additions", 0, removedFile.additions)
        assertEquals("Removed file deletions", 50, removedFile.deletions)
    }
    
    // Helper functions to create test data
    private fun createTestPullRequest(
        number: Int,
        title: String,
        body: String = "Test PR body",
        changedFiles: Int = 1,
        additions: Int = 10,
        deletions: Int = 5
    ): PullRequest {
        return PullRequest(
            id = number.toLong(),
            number = number,
            title = title,
            body = body,
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
            changedFiles = changedFiles,
            additions = additions,
            deletions = deletions,
            commits = 1,
            headRef = "feature/test",
            baseRef = "main",
            htmlUrl = "https://github.com/testuser/test-repo/pull/$number"
        )
    }
    
    private fun createTestFileDiff(
        filename: String,
        status: FileStatus,
        additions: Int = 10,
        deletions: Int = 5
    ): FileDiff {
        return FileDiff(
            filename = filename,
            status = status,
            additions = additions,
            deletions = deletions,
            changes = additions + deletions,
            blobUrl = "https://github.com/test/test/blob/abc123/$filename",
            rawUrl = "https://github.com/test/test/raw/abc123/$filename",
            patch = "@@ -1,3 +1,3 @@\n-old line\n+new line\n context"
        )
    }
}
