package com.issuetrax.app.domain.repository

import com.issuetrax.app.domain.entity.CommitStatus
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
    
    suspend fun createIssueComment(
        owner: String,
        repo: String,
        issueNumber: Int,
        body: String
    ): Result<Unit>
    
    suspend fun getCommitStatus(
        owner: String,
        repo: String,
        ref: String
    ): Result<CommitStatus>
    
    /**
     * Gets check runs for a specific commit.
     * This is the proper way to get GitHub Actions job statuses.
     */
    suspend fun getCheckRuns(
        owner: String,
        repo: String,
        ref: String
    ): Result<List<com.issuetrax.app.domain.entity.CheckRun>>
    
    suspend fun getWorkflowRuns(
        owner: String,
        repo: String,
        event: String? = null,
        status: String? = null
    ): Result<List<com.issuetrax.app.domain.entity.WorkflowRun>>
    
    /**
     * Gets workflow runs for a specific pull request by filtering by head SHA.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param headSha The commit SHA of the PR head
     * @return Result with list of WorkflowRun for the specific PR
     */
    suspend fun getWorkflowRunsForPR(
        owner: String,
        repo: String,
        headSha: String
    ): Result<List<com.issuetrax.app.domain.entity.WorkflowRun>>
    
    /**
     * Approves a workflow run from a fork PR by a first-time contributor.
     * Note: This only works for fork pull requests, not same-repo PRs.
     */
    suspend fun approveWorkflowRun(
        owner: String,
        repo: String,
        runId: Long
    ): Result<Unit>
    
    /**
     * Re-runs a workflow run. Works for any workflow run.
     */
    suspend fun rerunWorkflowRun(
        owner: String,
        repo: String,
        runId: Long
    ): Result<Unit>
    
    /**
     * Re-runs only the failed jobs in a workflow run.
     */
    suspend fun rerunFailedJobs(
        owner: String,
        repo: String,
        runId: Long
    ): Result<Unit>
    
    /**
     * Marks a draft pull request as ready for review.
     * This is required before merging a draft PR.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param prNumber Pull request number
     * @return Result with Unit on success or error
     */
    suspend fun markPrReadyForReview(
        owner: String,
        repo: String,
        prNumber: Int
    ): Result<Unit>
    
    /**
     * Gets the latest release for a repository.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @return Result with Release on success or error
     */
    suspend fun getLatestRelease(
        owner: String,
        repo: String
    ): Result<com.issuetrax.app.domain.entity.Release>
}

data class ReviewComment(
    val path: String,
    val position: Int,
    val body: String
)