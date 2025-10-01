package com.issuetrax.app.domain.repository

import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.Repository
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    suspend fun getCurrentUser(): Result<com.issuetrax.app.domain.entity.User>
    
    suspend fun getUserRepositories(): Flow<Result<List<Repository>>>
    
    suspend fun getPullRequests(
        owner: String,
        repo: String,
        state: String = "open"
    ): Flow<Result<List<PullRequest>>>
    
    suspend fun getPullRequest(
        owner: String,
        repo: String,
        number: Int
    ): Result<PullRequest>
    
    suspend fun getPullRequestFiles(
        owner: String,
        repo: String,
        number: Int
    ): Result<List<FileDiff>>
    
    suspend fun createReview(
        owner: String,
        repo: String,
        number: Int,
        body: String?,
        event: String, // "APPROVE", "REQUEST_CHANGES", "COMMENT"
        comments: List<ReviewComment> = emptyList()
    ): Result<Unit>
}

data class ReviewComment(
    val path: String,
    val position: Int,
    val body: String
)