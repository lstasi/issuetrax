package com.issuetrax.app.domain.repository

import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.Repository
import com.issuetrax.app.domain.entity.Review
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
    ): Result<Review>
    
    suspend fun approvePullRequest(
        owner: String,
        repo: String,
        prNumber: Int,
        comment: String? = null
    ): Result<Unit>
    
    suspend fun closePullRequest(
        owner: String,
        repo: String,
        prNumber: Int
    ): Result<Unit>
    
    suspend fun mergePullRequest(
        owner: String,
        repo: String,
        prNumber: Int,
        commitTitle: String? = null,
        commitMessage: String? = null,
        mergeMethod: String = "merge"
    ): Result<String>
    
    suspend fun createIssue(
        owner: String,
        repo: String,
        title: String,
        body: String?,
        assignees: List<String> = emptyList()
    ): Result<com.issuetrax.app.domain.entity.Issue>
}

data class ReviewComment(
    val path: String,
    val position: Int,
    val body: String
)