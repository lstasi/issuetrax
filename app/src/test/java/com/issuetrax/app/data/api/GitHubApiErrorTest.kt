package com.issuetrax.app.data.api

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class GitHubApiErrorTest {
    
    @Test
    fun `getDetailedErrorMessage parses GitHub error with message`() {
        // Given
        val errorJson = """
            {
                "message": "You must have the actions scope to approve a workflow run.",
                "documentation_url": "https://docs.github.com/rest/reference/actions"
            }
        """.trimIndent()
        
        val response = Response.error<Unit>(
            403,
            errorJson.toResponseBody()
        )
        
        // When
        val result = GitHubApiError.getDetailedErrorMessage(response, "Failed to approve")
        
        // Then
        assertTrue(result.contains("Failed to approve: 403"))
        assertTrue(result.contains("You must have the actions scope"))
        assertTrue(result.contains("token may not have the required permissions/scopes"))
    }
    
    @Test
    fun `getDetailedErrorMessage handles rate limit error`() {
        // Given
        val errorJson = """
            {
                "message": "API rate limit exceeded for user ID 12345."
            }
        """.trimIndent()
        
        val response = Response.error<Unit>(
            403,
            errorJson.toResponseBody()
        )
        
        // When
        val result = GitHubApiError.getDetailedErrorMessage(response, "Failed to merge")
        
        // Then
        assertTrue(result.contains("Failed to merge: 403"))
        assertTrue(result.contains("rate limit"))
        assertTrue(result.contains("wait and try again later"))
    }
    
    @Test
    fun `getDetailedErrorMessage handles protected branch error`() {
        // Given
        val errorJson = """
            {
                "message": "Required status check \"build\" is expected but not found.",
                "documentation_url": "https://docs.github.com/rest/reference/repos"
            }
        """.trimIndent()
        
        val response = Response.error<Unit>(
            403,
            errorJson.toResponseBody()
        )
        
        // When
        val result = GitHubApiError.getDetailedErrorMessage(response, "Failed to merge PR")
        
        // Then
        assertTrue(result.contains("Failed to merge PR: 403"))
        assertTrue(result.contains("Required status check"))
    }
    
    @Test
    fun `getDetailedErrorMessage includes error details when available`() {
        // Given
        val errorJson = """
            {
                "message": "Validation Failed",
                "errors": [
                    {
                        "resource": "PullRequest",
                        "field": "base",
                        "code": "invalid",
                        "message": "Base branch does not exist"
                    }
                ]
            }
        """.trimIndent()
        
        val response = Response.error<Unit>(
            422,
            errorJson.toResponseBody()
        )
        
        // When
        val result = GitHubApiError.getDetailedErrorMessage(response, "Failed to merge")
        
        // Then
        assertTrue(result.contains("Failed to merge: 422"))
        assertTrue(result.contains("Validation Failed"))
        assertTrue(result.contains("Resource: PullRequest"))
        assertTrue(result.contains("Field: base"))
        assertTrue(result.contains("Code: invalid"))
        assertTrue(result.contains("Base branch does not exist"))
    }
    
    @Test
    fun `getDetailedErrorMessage handles missing error body`() {
        // Given
        val response = Response.error<Unit>(
            403,
            "".toResponseBody()
        )
        
        // When
        val result = GitHubApiError.getDetailedErrorMessage(response, "Failed to approve")
        
        // Then
        assertTrue(result.contains("Failed to approve: 403"))
    }
    
    @Test
    fun `getDetailedErrorMessage handles invalid JSON in error body`() {
        // Given
        val response = Response.error<Unit>(
            403,
            "Not a valid JSON".toResponseBody()
        )
        
        // When
        val result = GitHubApiError.getDetailedErrorMessage(response, "Failed operation")
        
        // Then
        assertTrue(result.contains("Failed operation: 403"))
        assertTrue(result.contains("Not a valid JSON"))
    }
    
    @Test
    fun `getDetailedErrorMessage provides helpful context for 403 permission errors`() {
        // Given
        val errorJson = """
            {
                "message": "Resource not accessible by integration"
            }
        """.trimIndent()
        
        val response = Response.error<Unit>(
            403,
            errorJson.toResponseBody()
        )
        
        // When
        val result = GitHubApiError.getDetailedErrorMessage(response, "Failed to approve")
        
        // Then
        assertTrue(result.contains("permissions error"))
        assertTrue(result.contains("token may not have the required permissions/scopes"))
        assertTrue(result.contains("'repo' scope"))
        assertTrue(result.contains("'actions' scope"))
    }
    
    @Test
    fun `getDetailedErrorMessage provides general 403 help when specific cause unknown`() {
        // Given
        val errorJson = """
            {
                "message": "Forbidden"
            }
        """.trimIndent()
        
        val response = Response.error<Unit>(
            403,
            errorJson.toResponseBody()
        )
        
        // When
        val result = GitHubApiError.getDetailedErrorMessage(response, "Failed operation")
        
        // Then
        assertTrue(result.contains("Failed operation: 403"))
        assertTrue(result.contains("Forbidden"))
        assertTrue(result.contains("Possible reasons:"))
        assertTrue(result.contains("Insufficient token permissions/scopes"))
        assertTrue(result.contains("Branch protection rules"))
        assertTrue(result.contains("Repository access restrictions"))
        assertTrue(result.contains("Rate limit exceeded"))
    }
}
