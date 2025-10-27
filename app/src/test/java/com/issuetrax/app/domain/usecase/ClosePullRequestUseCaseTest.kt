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

class ClosePullRequestUseCaseTest {
    
    private lateinit var repository: GitHubRepository
    private lateinit var useCase: ClosePullRequestUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = ClosePullRequestUseCase(repository)
    }
    
    @Test
    fun `invoke calls repository closePullRequest with correct parameters`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val prNumber = 123
        
        coEvery { repository.closePullRequest(owner, repo, prNumber) } returns Result.success(Unit)
        
        // When
        val result = useCase(owner, repo, prNumber)
        
        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.closePullRequest(owner, repo, prNumber) }
    }
    
    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val prNumber = 123
        val errorMessage = "API error"
        
        coEvery { repository.closePullRequest(owner, repo, prNumber) } returns 
            Result.failure(Exception(errorMessage))
        
        // When
        val result = useCase(owner, repo, prNumber)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
}
