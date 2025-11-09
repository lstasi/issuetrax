package com.issuetrax.app.data.repository

import com.issuetrax.app.data.api.GitHubApiService
import com.issuetrax.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Test enhanced error handling in GitHubRepositoryImpl for 403 responses.
 */
class GitHubRepositoryImplErrorHandlingTest {
    
    private lateinit var apiService: GitHubApiService
    private lateinit var authRepository: AuthRepository
    private lateinit var repository: GitHubRepositoryImpl
    
    @Before
    fun setup() {
        apiService = mockk()
        authRepository = mockk()
        repository = GitHubRepositoryImpl(apiService, authRepository)
        
        // Mock auth token
        coEvery { authRepository.getAccessToken() } returns "test_token"
    }
    
    @Test
    fun `approvePullRequest returns detailed error message on 403 permission error`() = runTest {
        // Given
        val errorJson = """
            {
                "message": "Resource not accessible by integration",
                "documentation_url": "https://docs.github.com/rest/reference/pulls"
            }
        """.trimIndent()
        
        val response = Response.error<com.issuetrax.app.data.api.model.ReviewDto>(
            403,
            errorJson.toResponseBody()
        )
        
        coEvery { 
            apiService.createReview(any(), any(), any(), any(), any()) 
        } returns response
        
        // When
        val result = repository.approvePullRequest("owner", "repo", 123, null)
        
        // Then
        assertTrue(result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message ?: ""
        assertTrue("Expected detailed 403 message, got: $errorMessage", 
            errorMessage.contains("Failed to approve PR: 403"))
        assertTrue("Expected GitHub error message, got: $errorMessage",
            errorMessage.contains("Resource not accessible by integration"))
        assertTrue("Expected contextual help, got: $errorMessage",
            errorMessage.contains("permissions error"))
    }
    
    @Test
    fun `mergePullRequest returns detailed error message on 403 protected branch error`() = runTest {
        // Given
        val errorJson = """
            {
                "message": "Required status check \"ci\" is expected.",
                "documentation_url": "https://docs.github.com/rest/reference/repos"
            }
        """.trimIndent()
        
        val response = Response.error<com.issuetrax.app.data.api.MergeResultDto>(
            403,
            errorJson.toResponseBody()
        )
        
        coEvery { 
            apiService.mergePullRequest(any(), any(), any(), any(), any()) 
        } returns response
        
        // When
        val result = repository.mergePullRequest("owner", "repo", 123, null, null, "merge")
        
        // Then
        assertTrue(result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message ?: ""
        assertTrue("Expected detailed 403 message, got: $errorMessage", 
            errorMessage.contains("Failed to merge PR: 403"))
        assertTrue("Expected GitHub error message, got: $errorMessage",
            errorMessage.contains("Required status check"))
    }
    
    @Test
    fun `approveWorkflowRun returns detailed error message on 403 scope error`() = runTest {
        // Given
        val errorJson = """
            {
                "message": "You must have the actions scope to approve a workflow run.",
                "documentation_url": "https://docs.github.com/rest/reference/actions"
            }
        """.trimIndent()
        
        val response = Response.error<com.issuetrax.app.data.api.model.WorkflowRunApprovalResponseDto>(
            403,
            errorJson.toResponseBody()
        )
        
        coEvery { 
            apiService.approveWorkflowRun(any(), any(), any(), any()) 
        } returns response
        
        // When
        val result = repository.approveWorkflowRun("owner", "repo", 12345L)
        
        // Then
        assertTrue(result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message ?: ""
        assertTrue("Expected detailed 403 message, got: $errorMessage", 
            errorMessage.contains("Failed to approve workflow run: 403"))
        assertTrue("Expected GitHub error message, got: $errorMessage",
            errorMessage.contains("You must have the actions scope"))
        assertTrue("Expected contextual help, got: $errorMessage",
            errorMessage.contains("'actions' scope"))
    }
    
    @Test
    fun `closePullRequest returns detailed error message on 403 error`() = runTest {
        // Given
        val errorJson = """
            {
                "message": "You don't have permission to close this pull request."
            }
        """.trimIndent()
        
        val response = Response.error<com.issuetrax.app.data.api.model.PullRequestDto>(
            403,
            errorJson.toResponseBody()
        )
        
        coEvery { 
            apiService.updatePullRequest(any(), any(), any(), any(), any()) 
        } returns response
        
        // When
        val result = repository.closePullRequest("owner", "repo", 123)
        
        // Then
        assertTrue(result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message ?: ""
        assertTrue("Expected detailed 403 message, got: $errorMessage", 
            errorMessage.contains("Failed to close PR: 403"))
        assertTrue("Expected GitHub error message, got: $errorMessage",
            errorMessage.contains("You don't have permission"))
    }
    
    @Test
    fun `mergePullRequest returns detailed error message on 403 rate limit error`() = runTest {
        // Given
        val errorJson = """
            {
                "message": "API rate limit exceeded for user ID 12345."
            }
        """.trimIndent()
        
        val response = Response.error<com.issuetrax.app.data.api.MergeResultDto>(
            403,
            errorJson.toResponseBody()
        )
        
        coEvery { 
            apiService.mergePullRequest(any(), any(), any(), any(), any()) 
        } returns response
        
        // When
        val result = repository.mergePullRequest("owner", "repo", 123, null, null, "merge")
        
        // Then
        assertTrue(result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message ?: ""
        assertTrue("Expected detailed 403 message, got: $errorMessage", 
            errorMessage.contains("Failed to merge PR: 403"))
        assertTrue("Expected rate limit message, got: $errorMessage",
            errorMessage.contains("rate limit"))
        assertTrue("Expected contextual help, got: $errorMessage",
            errorMessage.contains("wait and try again later"))
    }
}
