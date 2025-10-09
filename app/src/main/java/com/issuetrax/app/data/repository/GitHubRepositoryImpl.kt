package com.issuetrax.app.data.repository

import com.issuetrax.app.data.api.GitHubApiService
import com.issuetrax.app.data.api.CreateReviewRequest
import com.issuetrax.app.data.api.ReviewCommentRequest
import com.issuetrax.app.data.mapper.toDomain
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.Repository
import com.issuetrax.app.domain.entity.Review
import com.issuetrax.app.domain.entity.User
import com.issuetrax.app.domain.repository.AuthRepository
import com.issuetrax.app.domain.repository.GitHubRepository
import com.issuetrax.app.domain.repository.ReviewComment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService,
    private val authRepository: AuthRepository
) : GitHubRepository {
    
    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.getCurrentUser("Bearer $token")
            if (response.isSuccessful) {
                val userDto = response.body()!!
                Result.success(userDto.toDomain())
            } else {
                Result.failure(Exception("Failed to get current user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserRepositories(): Flow<Result<List<Repository>>> = flow {
        try {
            val token = authRepository.getAccessToken()
            if (token == null) {
                emit(Result.failure(Exception("No access token")))
                return@flow
            }
            
            val response = apiService.getUserRepositories("Bearer $token")
            if (response.isSuccessful) {
                val repositories = response.body()!!
                    .filter { !it.archived }  // Filter out archived repositories
                    .map { it.toDomain() }
                emit(Result.success(repositories))
            } else {
                emit(Result.failure(Exception("Failed to get repositories: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override suspend fun getPullRequests(
        owner: String,
        repo: String,
        state: String
    ): Flow<Result<List<PullRequest>>> = flow {
        try {
            val token = authRepository.getAccessToken()
            if (token == null) {
                emit(Result.failure(Exception("No access token")))
                return@flow
            }
            
            val response = apiService.getPullRequests("Bearer $token", owner, repo, state)
            if (response.isSuccessful) {
                val pullRequests = response.body()!!.map { it.toDomain() }
                emit(Result.success(pullRequests))
            } else {
                emit(Result.failure(Exception("Failed to get pull requests: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override suspend fun getPullRequest(
        owner: String,
        repo: String,
        number: Int
    ): Result<PullRequest> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.getPullRequest("Bearer $token", owner, repo, number)
            if (response.isSuccessful) {
                val pullRequest = response.body()!!.toDomain()
                Result.success(pullRequest)
            } else {
                Result.failure(Exception("Failed to get pull request: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPullRequestFiles(
        owner: String,
        repo: String,
        number: Int
    ): Result<List<FileDiff>> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.getPullRequestFiles("Bearer $token", owner, repo, number)
            if (response.isSuccessful) {
                val files = response.body()!!.map { it.toDomain() }
                Result.success(files)
            } else {
                Result.failure(Exception("Failed to get pull request files: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createReview(
        owner: String,
        repo: String,
        number: Int,
        body: String?,
        event: String,
        comments: List<ReviewComment>
    ): Result<Review> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val request = CreateReviewRequest(
                body = body,
                event = event,
                comments = comments.map { ReviewCommentRequest(it.path, it.position, it.body) }
            )
            
            val response = apiService.createReview("Bearer $token", owner, repo, number, request)
            if (response.isSuccessful) {
                val reviewDto = response.body()
                if (reviewDto != null) {
                    val review = reviewDto.toDomain()
                    Result.success(review)
                } else {
                    Result.failure(Exception("Failed to create review: response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to create review: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}