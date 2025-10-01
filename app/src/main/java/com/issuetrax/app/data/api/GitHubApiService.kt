package com.issuetrax.app.data.api

import com.issuetrax.app.data.api.model.PullRequestDto
import com.issuetrax.app.data.api.model.RepositoryDto
import com.issuetrax.app.data.api.model.UserDto
import com.issuetrax.app.data.api.model.FileDiffDto
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
    ): Response<Unit>
}

data class CreateReviewRequest(
    val body: String?,
    val event: String,
    val comments: List<ReviewCommentRequest> = emptyList()
)

data class ReviewCommentRequest(
    val path: String,
    val position: Int,
    val body: String
)