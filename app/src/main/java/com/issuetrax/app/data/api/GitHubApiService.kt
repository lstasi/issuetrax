package com.issuetrax.app.data.api

import com.issuetrax.app.data.api.model.PullRequestDto
import com.issuetrax.app.data.api.model.RepositoryDto
import com.issuetrax.app.data.api.model.ReviewDto
import com.issuetrax.app.data.api.model.UserDto
import com.issuetrax.app.data.api.model.FileDiffDto
import com.issuetrax.app.data.api.model.IssueDto
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
    
    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issueRequest: CreateIssueRequest
    ): Response<IssueDto>
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
data class CreateIssueRequest(
    val title: String,
    val body: String? = null,
    val assignees: List<String> = emptyList()
)