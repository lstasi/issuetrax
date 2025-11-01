package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.GitHubRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ApproveWorkflowRunUseCaseTest {
    
    private lateinit var repository: GitHubRepository
    private lateinit var useCase: ApproveWorkflowRunUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = ApproveWorkflowRunUseCase(repository)
    }
    
    @Test
    fun `invoke calls repository approveWorkflowRun with correct parameters`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val runId = 123456L
        
        coEvery { repository.approveWorkflowRun(owner, repo, runId) } returns Result.success(Unit)
        
        // When
        val result = useCase(owner, repo, runId)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.approveWorkflowRun(owner, repo, runId) }
    }
    
    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val runId = 123456L
        val errorMessage = "Failed to approve workflow run"
        
        coEvery { repository.approveWorkflowRun(owner, repo, runId) } returns 
            Result.failure(Exception(errorMessage))
        
        // When
        val result = useCase(owner, repo, runId)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `invoke handles network errors`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val runId = 123456L
        val exception = Exception("Network error")
        
        coEvery { repository.approveWorkflowRun(owner, repo, runId) } returns 
            Result.failure(exception)
        
        // When
        val result = useCase(owner, repo, runId)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
