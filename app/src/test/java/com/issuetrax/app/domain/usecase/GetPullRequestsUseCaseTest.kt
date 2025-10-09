package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.repository.GitHubRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetPullRequestsUseCase
 * 
 * Following Classic TDD approach:
 * - Test the use case behavior in isolation
 * - Mock only the repository (external dependency)
 * - Verify correct delegation to repository
 */
class GetPullRequestsUseCaseTest {
    
    private lateinit var gitHubRepository: GitHubRepository
    private lateinit var useCase: GetPullRequestsUseCase
    
    @Before
    fun setup() {
        gitHubRepository = mockk()
        useCase = GetPullRequestsUseCase(gitHubRepository)
    }
    
    @Test
    fun `invoke should return success result with pull requests from repository`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val state = "open"
        val mockPRs = listOf(
            createMockPullRequest(1, "First PR"),
            createMockPullRequest(2, "Second PR")
        )
        coEvery { 
            gitHubRepository.getPullRequests(owner, repo, state) 
        } returns flowOf(Result.success(mockPRs))
        
        // When
        val result = useCase(owner, repo, state).first()
        
        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should return 2 PRs", 2, result.getOrNull()?.size)
        assertEquals("First PR should match", mockPRs[0], result.getOrNull()?.get(0))
        
        coVerify(exactly = 1) { 
            gitHubRepository.getPullRequests(owner, repo, state) 
        }
    }
    
    @Test
    fun `invoke should return failure result when repository fails`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val state = "open"
        val exception = RuntimeException("API error")
        coEvery { 
            gitHubRepository.getPullRequests(owner, repo, state) 
        } returns flowOf(Result.failure(exception))
        
        // When
        val result = useCase(owner, repo, state).first()
        
        // Then
        assertTrue("Result should be failure", result.isFailure)
        assertEquals("Should return same exception", exception, result.exceptionOrNull())
        
        coVerify(exactly = 1) { 
            gitHubRepository.getPullRequests(owner, repo, state) 
        }
    }
    
    @Test
    fun `invoke should use default state parameter when not provided`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val mockPRs = listOf(createMockPullRequest(1, "Test PR"))
        coEvery { 
            gitHubRepository.getPullRequests(owner, repo, "open") 
        } returns flowOf(Result.success(mockPRs))
        
        // When
        val result = useCase(owner, repo).first()
        
        // Then
        assertTrue("Result should be success", result.isSuccess)
        coVerify(exactly = 1) { 
            gitHubRepository.getPullRequests(owner, repo, "open") 
        }
    }
    
    @Test
    fun `invoke should pass all state values to repository`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        val states = listOf("open", "closed", "all")
        
        // Mock all states upfront
        coEvery { 
            gitHubRepository.getPullRequests(owner, repo, "open") 
        } returns flowOf(Result.success(emptyList()))
        coEvery { 
            gitHubRepository.getPullRequests(owner, repo, "closed") 
        } returns flowOf(Result.success(emptyList()))
        coEvery { 
            gitHubRepository.getPullRequests(owner, repo, "all") 
        } returns flowOf(Result.success(emptyList()))
        
        // When & Then
        states.forEach { state ->
            val result = useCase(owner, repo, state).first()
            assertTrue("Result should be success for state $state", result.isSuccess)
        }
        
        coVerify(exactly = 1) { 
            gitHubRepository.getPullRequests(owner, repo, "open") 
        }
        coVerify(exactly = 1) { 
            gitHubRepository.getPullRequests(owner, repo, "closed") 
        }
        coVerify(exactly = 1) { 
            gitHubRepository.getPullRequests(owner, repo, "all") 
        }
    }
    
    @Test
    fun `invoke should handle empty list from repository`() = runTest {
        // Given
        val owner = "testuser"
        val repo = "test-repo"
        coEvery { 
            gitHubRepository.getPullRequests(owner, repo, "open") 
        } returns flowOf(Result.success(emptyList()))
        
        // When
        val result = useCase(owner, repo).first()
        
        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertTrue("List should be empty", result.getOrNull()?.isEmpty() == true)
    }
    
    // Helper function to create mock pull requests for testing
    private fun createMockPullRequest(number: Int, title: String): PullRequest {
        return mockk(relaxed = true) {
            coEvery { this@mockk.number } returns number
            coEvery { this@mockk.title } returns title
        }
    }
}
