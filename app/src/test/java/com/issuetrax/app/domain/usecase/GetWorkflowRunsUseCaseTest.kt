package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.entity.WorkflowRun
import com.issuetrax.app.domain.repository.GitHubRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetWorkflowRunsUseCaseTest {
    
    private lateinit var repository: GitHubRepository
    private lateinit var useCase: GetWorkflowRunsUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = GetWorkflowRunsUseCase(repository)
    }
    
    @Test
    fun `invoke calls repository getWorkflowRuns with correct parameters`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val event = "pull_request"
        val status = "waiting"
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
        
        coEvery { repository.getWorkflowRuns(owner, repo, event, status) } returns 
            Result.success(mockWorkflowRuns)
        
        // When
        val result = useCase(owner, repo, event, status)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockWorkflowRuns, result.getOrNull())
        coVerify { repository.getWorkflowRuns(owner, repo, event, status) }
    }
    
    @Test
    fun `invoke with null filters calls repository correctly`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val mockWorkflowRuns = emptyList<WorkflowRun>()
        
        coEvery { repository.getWorkflowRuns(owner, repo, null, null) } returns 
            Result.success(mockWorkflowRuns)
        
        // When
        val result = useCase(owner, repo, null, null)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockWorkflowRuns, result.getOrNull())
        coVerify { repository.getWorkflowRuns(owner, repo, null, null) }
    }
    
    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val errorMessage = "Failed to get workflow runs"
        
        coEvery { repository.getWorkflowRuns(owner, repo, null, null) } returns 
            Result.failure(Exception(errorMessage))
        
        // When
        val result = useCase(owner, repo, null, null)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `invoke returns empty list when no workflow runs exist`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val emptyList = emptyList<WorkflowRun>()
        
        coEvery { repository.getWorkflowRuns(owner, repo, null, null) } returns 
            Result.success(emptyList)
        
        // When
        val result = useCase(owner, repo, null, null)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }
}
