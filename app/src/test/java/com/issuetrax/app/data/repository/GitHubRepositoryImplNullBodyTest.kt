package com.issuetrax.app.data.repository

import com.issuetrax.app.data.api.GitHubApiService
import com.issuetrax.app.data.api.model.CheckRunsResponseDto
import com.issuetrax.app.data.api.model.FileDiffDto
import com.issuetrax.app.data.api.model.PullRequestDto
import com.issuetrax.app.data.api.model.UserDto
import com.issuetrax.app.data.api.model.WorkflowRunsResponseDto
import com.issuetrax.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Tests that GitHubRepositoryImpl handles null response bodies gracefully
 * instead of crashing with NullPointerException.
 */
class GitHubRepositoryImplNullBodyTest {

    private lateinit var apiService: GitHubApiService
    private lateinit var authRepository: AuthRepository
    private lateinit var repository: GitHubRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        authRepository = mockk()
        repository = GitHubRepositoryImpl(apiService, authRepository)

        coEvery { authRepository.getAccessToken() } returns "test_token"
    }

    @Test
    fun `getCurrentUser returns failure when response body is null`() = runTest {
        // Given
        val response = Response.success<UserDto>(null)
        coEvery { apiService.getCurrentUser(any()) } returns response

        // When
        val result = repository.getCurrentUser()

        // Then
        assertTrue("Should be a failure", result.isFailure)
        assertTrue(
            "Error message should mention null body",
            result.exceptionOrNull()?.message?.contains("response body is null") == true
        )
    }

    @Test
    fun `getUserRepositories returns failure when response body is null`() = runTest {
        // Given
        val response = Response.success<List<com.issuetrax.app.data.api.model.RepositoryDto>>(null)
        coEvery { apiService.getUserRepositories(any()) } returns response

        // When - use toList() to avoid flow cancellation issues with first()
        val results = repository.getUserRepositories().toList()
        val result = results.first()

        // Then
        assertTrue("Should be a failure", result.isFailure)
        assertTrue(
            "Error message should mention null body",
            result.exceptionOrNull()?.message?.contains("response body is null") == true
        )
    }

    @Test
    fun `getPullRequest returns failure when response body is null`() = runTest {
        // Given
        val response = Response.success<PullRequestDto>(null)
        coEvery { apiService.getPullRequest(any(), any(), any(), any()) } returns response

        // When
        val result = repository.getPullRequest("owner", "repo", 1)

        // Then
        assertTrue("Should be a failure", result.isFailure)
        assertTrue(
            "Error message should mention null body",
            result.exceptionOrNull()?.message?.contains("response body is null") == true
        )
    }

    @Test
    fun `getPullRequestFiles returns failure when response body is null`() = runTest {
        // Given
        val response = Response.success<List<FileDiffDto>>(null)
        coEvery { apiService.getPullRequestFiles(any(), any(), any(), any()) } returns response

        // When
        val result = repository.getPullRequestFiles("owner", "repo", 1)

        // Then
        assertTrue("Should be a failure", result.isFailure)
        assertTrue(
            "Error message should mention null body",
            result.exceptionOrNull()?.message?.contains("response body is null") == true
        )
    }

    @Test
    fun `getCheckRuns returns failure when response body is null`() = runTest {
        // Given
        val response = Response.success<CheckRunsResponseDto>(null)
        coEvery { apiService.getCheckRuns(any(), any(), any(), any()) } returns response

        // When
        val result = repository.getCheckRuns("owner", "repo", "abc123")

        // Then
        assertTrue("Should be a failure", result.isFailure)
        assertTrue(
            "Error message should mention null body",
            result.exceptionOrNull()?.message?.contains("response body is null") == true
        )
    }

    @Test
    fun `getWorkflowRuns returns failure when response body is null`() = runTest {
        // Given
        val response = Response.success<WorkflowRunsResponseDto>(null)
        coEvery {
            apiService.getWorkflowRuns(any(), any(), any(), any(), any())
        } returns response

        // When
        val result = repository.getWorkflowRuns("owner", "repo", null, null)

        // Then
        assertTrue("Should be a failure", result.isFailure)
        assertTrue(
            "Error message should mention null body",
            result.exceptionOrNull()?.message?.contains("response body is null") == true
        )
    }
}
