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

class MergePullRequestUseCaseTest {
    
    private lateinit var repository: GitHubRepository
    private lateinit var useCase: MergePullRequestUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = MergePullRequestUseCase(repository)
    }
    
    @Test
    fun `invoke calls repository mergePullRequest with default merge method`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val prNumber = 123
        val expectedSha = "abc123def456"
        
        coEvery { 
            repository.mergePullRequest(owner, repo, prNumber, null, null, "merge") 
        } returns Result.success(expectedSha)
        
        // When
        val result = useCase(owner, repo, prNumber)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedSha, result.getOrNull())
        coVerify { repository.mergePullRequest(owner, repo, prNumber, null, null, "merge") }
    }
    
    @Test
    fun `invoke calls repository with custom commit title and message`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val prNumber = 123
        val commitTitle = "Custom merge title"
        val commitMessage = "Custom merge message"
        val expectedSha = "abc123def456"
        
        coEvery { 
            repository.mergePullRequest(owner, repo, prNumber, commitTitle, commitMessage, "merge") 
        } returns Result.success(expectedSha)
        
        // When
        val result = useCase(owner, repo, prNumber, commitTitle, commitMessage)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedSha, result.getOrNull())
        coVerify { 
            repository.mergePullRequest(owner, repo, prNumber, commitTitle, commitMessage, "merge") 
        }
    }
    
    @Test
    fun `invoke calls repository with squash merge method`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val prNumber = 123
        val mergeMethod = "squash"
        val expectedSha = "abc123def456"
        
        coEvery { 
            repository.mergePullRequest(owner, repo, prNumber, null, null, mergeMethod) 
        } returns Result.success(expectedSha)
        
        // When
        val result = useCase(owner, repo, prNumber, mergeMethod = mergeMethod)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedSha, result.getOrNull())
        coVerify { repository.mergePullRequest(owner, repo, prNumber, null, null, mergeMethod) }
    }
    
    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        val owner = "testOwner"
        val repo = "testRepo"
        val prNumber = 123
        val errorMessage = "Merge conflict"
        
        coEvery { 
            repository.mergePullRequest(owner, repo, prNumber, null, null, "merge") 
        } returns Result.failure(Exception(errorMessage))
        
        // When
        val result = useCase(owner, repo, prNumber)
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
}
