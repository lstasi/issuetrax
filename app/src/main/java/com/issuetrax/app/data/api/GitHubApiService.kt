package com.issuetrax.app.data.api

import com.issuetrax.app.data.api.model.PullRequestDto
import com.issuetrax.app.data.api.model.RepositoryDto
import com.issuetrax.app.data.api.model.ReviewDto
import com.issuetrax.app.data.api.model.UserDto
import com.issuetrax.app.data.api.model.FileDiffDto
import com.issuetrax.app.data.api.model.IssueDto
import com.issuetrax.app.data.api.model.WorkflowRunsResponseDto
import com.issuetrax.app.data.api.model.WorkflowRunApprovalResponseDto
import com.issuetrax.app.data.api.model.CheckRunsResponseDto
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
    
    @POST("repos/{owner}/{repo}/issues/{issue_number}/assignees")
    suspend fun addAssigneesToIssue(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNumber: Int,
        @Body assigneesRequest: AddAssigneesRequest
    ): Response<IssueDto>
    
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
    
    /**
     * Get check runs for a specific commit.
     * This is the proper way to get GitHub Actions job statuses.
     * 
     * API: GET /repos/{owner}/{repo}/commits/{ref}/check-runs
     */
    @GET("repos/{owner}/{repo}/commits/{ref}/check-runs")
    suspend fun getCheckRuns(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("ref") ref: String,
        @Query("per_page") perPage: Int = 100
    ): Response<CheckRunsResponseDto>
    
    @GET("repos/{owner}/{repo}/actions/runs")
    suspend fun getWorkflowRuns(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("event") event: String? = null,
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int = 10
    ): Response<WorkflowRunsResponseDto>
    
    @POST("repos/{owner}/{repo}/actions/runs/{run_id}/approve")
    suspend fun approveWorkflowRun(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("run_id") runId: Long
    ): Response<WorkflowRunApprovalResponseDto>
    
    /**
     * Re-run a workflow run.
     * This works for any workflow run (not just fork PRs like approve).
     */
    @POST("repos/{owner}/{repo}/actions/runs/{run_id}/rerun")
    suspend fun rerunWorkflowRun(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("run_id") runId: Long
    ): Response<Unit>
    
    /**
     * Re-run only the failed jobs in a workflow run.
     */
    @POST("repos/{owner}/{repo}/actions/runs/{run_id}/rerun-failed-jobs")
    suspend fun rerunFailedJobs(
        @Header("Authorization") authorization: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("run_id") runId: Long
    ): Response<Unit>
    
    /**
     * Mark a draft pull request as ready for review using GitHub GraphQL API.
     * This is required because GitHub REST API doesn't support this operation directly.
     */
    @POST("graphql")
    suspend fun markPrReadyForReview(
        @Header("Authorization") authorization: String,
        @Body request: GraphQLRequest
    ): Response<GraphQLResponse>
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

@Serializable
data class AddAssigneesRequest(
    val assignees: List<String>
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

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, String> = emptyMap()
)

@Serializable
data class GraphQLResponse(
    val data: GraphQLData? = null,
    val errors: List<GraphQLError>? = null
)

@Serializable
data class GraphQLData(
    val markPullRequestReadyForReview: MarkPullRequestReadyForReviewPayload? = null,
    val repository: GraphQLRepository? = null,
    val replaceActorsForAssignable: ReplaceActorsPayload? = null
)

@Serializable
data class GraphQLRepository(
    val id: String? = null,
    val issue: GraphQLIssue? = null,
    val suggestedActors: SuggestedActorsConnection? = null
)

@Serializable
data class GraphQLIssue(
    val id: String,
    val title: String? = null
)

@Serializable
data class SuggestedActorsConnection(
    val nodes: List<SuggestedActor>? = null
)

@Serializable
data class SuggestedActor(
    val login: String,
    val id: String,
    @Suppress("PropertyName")
    val __typename: String? = null
)

@Serializable
data class ReplaceActorsPayload(
    val assignable: AssignablePayload? = null
)

@Serializable
data class AssignablePayload(
    val id: String? = null
)

@Serializable
data class MarkPullRequestReadyForReviewPayload(
    val pullRequest: GraphQLPullRequest? = null
)

@Serializable
data class GraphQLPullRequest(
    val id: String,
    val isDraft: Boolean
)

@Serializable
data class GraphQLError(
    val message: String,
    val type: String? = null
)