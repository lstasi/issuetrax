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

class ApprovePullRequestUseCaseTest {
    
    private lateinit var repository: GitHubRepository
    private lateinit var useCase: ApprovePullRequestUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = ApprovePullRequestUseCase(repository)
    }
    
    @Test
    fun `invoke calls repository approvePullRequest with correct parameters`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val prNumber = 123
        val comment = "Looks good to me!"
        
        coEvery { repository.approvePullRequest(owner, repo, prNumber, comment) } returns Result.success(Unit)
        
        // When
        val result = useCase(owner, repo, prNumber, comment)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.approvePullRequest(owner, repo, prNumber, comment) }
    }
    
    @Test
    fun `invoke with null comment uses default`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val prNumber = 123
        
        coEvery { repository.approvePullRequest(owner, repo, prNumber, null) } returns Result.success(Unit)
        
        // When
        val result = useCase(owner, repo, prNumber, null)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.approvePullRequest(owner, repo, prNumber, null) }
    }
    
    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val prNumber = 123
        val errorMessage = "Network error"
        
        coEvery { repository.approvePullRequest(owner, repo, prNumber, null) } returns 
            Result.failure(Exception(errorMessage))
        
        // When
        val result = useCase(owner, repo, prNumber, null)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
}
