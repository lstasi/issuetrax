package com.issuetrax.app.data.api

import com.issuetrax.app.data.api.model.PullRequestDto
import com.issuetrax.app.data.api.model.RepositoryDto
import com.issuetrax.app.data.api.model.ReviewDto
import com.issuetrax.app.data.api.model.UserDto
import com.issuetrax.app.data.api.model.FileDiffDto
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.*

interface GitHubApiService {
    
    @GET("user")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): Response<UserDto>
    
    @GET("user/repos")
    suspend fun getUserRepositories(
        @Header("Authorization") authorization: String,
        @Query("sort") sort: String = "updated",
        @Query("per_page") perPage: Int = 100
    ): Response<List<RepositoryDto>>
    
    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPullRequests(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "open",
        @Query("per_page") perPage: Int = 100
    ): Response<List<PullRequestDto>>
    
    @GET("repos/{owner}/{repo}/pulls/{number}")
    suspend fun getPullRequest(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int
    ): Response<PullRequestDto>
    
    @GET("repos/{owner}/{repo}/pulls/{number}/files")
    suspend fun getPullRequestFiles(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Query("per_page") perPage: Int = 100
    ): Response<List<FileDiffDto>>
    
    @POST("repos/{owner}/{repo}/pulls/{number}/reviews")
    suspend fun createReview(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body reviewRequest: CreateReviewRequest
    ): Response<ReviewDto>
    
    @PUT("repos/{owner}/{repo}/pulls/{number}/merge")
    suspend fun mergePullRequest(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body mergeRequest: MergePullRequestRequest
    ): Response<MergeResultDto>
    
    @PATCH("repos/{owner}/{repo}/pulls/{number}")
    suspend fun updatePullRequest(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body updateRequest: UpdatePullRequestRequest
    ): Response<PullRequestDto>
    
    @POST("repos/{owner}/{repo}/issues/{number}/comments")
    suspend fun createIssueComment(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body commentRequest: CreateIssueCommentRequest
    ): Response<IssueCommentDto>
    
    @GET("repos/{owner}/{repo}/commits/{ref}/status")
    suspend fun getCommitStatus(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("ref") ref: String
    ): Response<CommitStatusDto>
}

@Serializable
data class CreateReviewRequest(
    val body: String?,
    val event: String,
    val comments: List<ReviewCommentRequest> = emptyList()
)

@Serializable
data class ReviewCommentRequest(
    val path: String,
    val position: Int,
    val body: String
)

@Serializable
data class MergePullRequestRequest(
    val commit_title: String? = null,
    val commit_message: String? = null,
    val merge_method: String = "merge" // "merge", "squash", or "rebase"
)

@Serializable
data class MergeResultDto(
    val sha: String,
    val merged: Boolean,
    val message: String
)

@Serializable
data class UpdatePullRequestRequest(
    val state: String? = null, // "open" or "closed"
    val title: String? = null,
    val body: String? = null
)

@Serializable
data class CreateIssueCommentRequest(
    val body: String
)

@Serializable
data class IssueCommentDto(
    val id: Long,
    val body: String,
    val user: UserDto,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class CommitStatusDto(
    val state: String, // "pending", "success", "failure", "error"
    val statuses: List<StatusDto>,
    val total_count: Int
)

@Serializable
data class StatusDto(
    val state: String,
    val context: String,
    val description: String?,
    val target_url: String?
)