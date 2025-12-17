package com.issuetrax.app.data.repository

import android.util.Log
import com.issuetrax.app.data.api.ApiErrorParser
import com.issuetrax.app.data.api.GitHubApiService
import com.issuetrax.app.data.api.CreateReviewRequest
import com.issuetrax.app.data.api.ReviewCommentRequest
import com.issuetrax.app.data.api.CreateIssueRequest
import com.issuetrax.app.data.mapper.toDomain
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.Repository
import com.issuetrax.app.domain.entity.Review
import com.issuetrax.app.domain.entity.User
import com.issuetrax.app.domain.entity.Issue
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
    
    companion object {
        private const val TAG = "GitHubRepositoryImpl"
    }
    
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
    
    override suspend fun approvePullRequest(
        owner: String,
        repo: String,
        prNumber: Int,
        comment: String?
    ): Result<Unit> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val request = CreateReviewRequest(
                body = comment ?: "Approved via Issuetrax",
                event = "APPROVE",
                comments = emptyList()
            )
            
            val response = apiService.createReview("Bearer $token", owner, repo, prNumber, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = com.issuetrax.app.data.api.GitHubApiError.getDetailedErrorMessage(
                    response,
                    "Failed to approve PR"
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun closePullRequest(
        owner: String,
        repo: String,
        prNumber: Int
    ): Result<Unit> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val request = com.issuetrax.app.data.api.UpdatePullRequestRequest(
                state = "closed"
            )
            
            val response = apiService.updatePullRequest("Bearer $token", owner, repo, prNumber, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = com.issuetrax.app.data.api.GitHubApiError.getDetailedErrorMessage(
                    response,
                    "Failed to close PR"
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun mergePullRequest(
        owner: String,
        repo: String,
        prNumber: Int,
        commitTitle: String?,
        commitMessage: String?,
        mergeMethod: String
    ): Result<String> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val request = com.issuetrax.app.data.api.MergePullRequestRequest(
                commit_title = commitTitle,
                commit_message = commitMessage,
                merge_method = mergeMethod
            )
            
            val response = apiService.mergePullRequest("Bearer $token", owner, repo, prNumber, request)
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null && result.merged) {
                    Result.success(result.sha)
                } else {
                    Result.failure(Exception("Failed to merge PR: ${result?.message ?: "Unknown error"}"))
                }
            } else {
                val errorMessage = com.issuetrax.app.data.api.GitHubApiError.getDetailedErrorMessage(
                    response,
                    "Failed to merge PR"
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createIssue(
        owner: String,
        repo: String,
        title: String,
        body: String?,
        assignees: List<String>
    ): Result<Issue> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            // Create issue without assignees first
            val request = CreateIssueRequest(
                title = title,
                body = body,
                assignees = emptyList()
            )
            
            Log.d(TAG, "Creating issue in $owner/$repo with title: '$title'")
            
            val response = apiService.createIssue("Bearer $token", owner, repo, request)
            if (response.isSuccessful) {
                val issueDto = response.body()
                if (issueDto != null) {
                    Log.d(TAG, "Issue created successfully: #${issueDto.number}")
                    
                    // If Copilot is in assignees, use GraphQL API to assign
                    if (assignees.any { it.equals("Copilot", ignoreCase = true) }) {
                        Log.d(TAG, "Attempting to assign Copilot to issue #${issueDto.number} via GraphQL")
                        assignCopilotToIssue(token, owner, repo, issueDto.number)
                    }
                    
                    Result.success(issueDto.toDomain())
                } else {
                    val error = "Failed to create issue: response body is null"
                    Log.e(TAG, error)
                    Result.failure(Exception(error))
                }
            } else {
                val errorMessage = ApiErrorParser.parseErrorResponse(response)
                Log.e(TAG, "Failed to create issue: $errorMessage")
                Result.failure(Exception("Failed to create issue: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while creating issue", e)
            Result.failure(e)
        }
    }
    
    /**
     * Assigns Copilot coding agent to an issue using GitHub GraphQL API.
     * This is required because Copilot cannot be assigned via the REST API.
     */
    private suspend fun assignCopilotToIssue(
        token: String,
        owner: String,
        repo: String,
        issueNumber: Int
    ) {
        try {
            // Step 1: Get the Copilot bot ID from suggested actors
            val suggestedActorsQuery = """
                query {
                    repository(owner: "$owner", name: "$repo") {
                        suggestedActors(capabilities: [CAN_BE_ASSIGNED], first: 100) {
                            nodes {
                                login
                                __typename
                                ... on Bot { id }
                                ... on User { id }
                            }
                        }
                    }
                }
            """.trimIndent()
            
            val actorsRequest = com.issuetrax.app.data.api.GraphQLRequest(query = suggestedActorsQuery)
            val actorsResponse = apiService.markPrReadyForReview("Bearer $token", actorsRequest)
            
            if (!actorsResponse.isSuccessful) {
                Log.w(TAG, "Failed to get suggested actors: ${actorsResponse.code()}")
                return
            }
            
            val actorsData = actorsResponse.body()
            val copilotBot = actorsData?.data?.repository?.suggestedActors?.nodes
                ?.find { it.login == "copilot-swe-agent" }
            
            if (copilotBot == null) {
                Log.w(TAG, "Copilot coding agent not available for this repository")
                return
            }
            
            val botId = copilotBot.id
            Log.d(TAG, "Found Copilot bot ID: $botId")
            
            // Step 2: Get the issue's GraphQL ID
            val issueQuery = """
                query {
                    repository(owner: "$owner", name: "$repo") {
                        issue(number: $issueNumber) {
                            id
                            title
                        }
                    }
                }
            """.trimIndent()
            
            val issueRequest = com.issuetrax.app.data.api.GraphQLRequest(query = issueQuery)
            val issueResponse = apiService.markPrReadyForReview("Bearer $token", issueRequest)
            
            if (!issueResponse.isSuccessful) {
                Log.w(TAG, "Failed to get issue ID: ${issueResponse.code()}")
                return
            }
            
            val issueData = issueResponse.body()
            val issueId = issueData?.data?.repository?.issue?.id
            
            if (issueId == null) {
                Log.w(TAG, "Could not get issue GraphQL ID")
                return
            }
            
            Log.d(TAG, "Found issue GraphQL ID: $issueId")
            
            // Step 3: Assign Copilot to the issue
            val assignMutation = """
                mutation {
                    replaceActorsForAssignable(input: {assignableId: "$issueId", actorIds: ["$botId"]}) {
                        assignable {
                            ... on Issue {
                                id
                            }
                        }
                    }
                }
            """.trimIndent()
            
            val assignRequest = com.issuetrax.app.data.api.GraphQLRequest(query = assignMutation)
            val assignResponse = apiService.markPrReadyForReview("Bearer $token", assignRequest)
            
            if (assignResponse.isSuccessful) {
                val assignData = assignResponse.body()
                if (assignData?.errors.isNullOrEmpty()) {
                    Log.d(TAG, "Successfully assigned Copilot to issue #$issueNumber")
                } else {
                    Log.w(TAG, "GraphQL errors assigning Copilot: ${assignData?.errors?.map { it.message }}")
                }
            } else {
                Log.w(TAG, "Failed to assign Copilot: ${assignResponse.code()}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Exception while assigning Copilot to issue", e)
        }
    }
    
    override suspend fun createIssueComment(
        owner: String,
        repo: String,
        issueNumber: Int,
        body: String
    ): Result<Unit> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val request = com.issuetrax.app.data.api.CreateIssueCommentRequest(body = body)
            
            val response = apiService.createIssueComment("Bearer $token", owner, repo, issueNumber, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create comment: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCommitStatus(
        owner: String,
        repo: String,
        ref: String
    ): Result<com.issuetrax.app.domain.entity.CommitStatus> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.getCommitStatus("Bearer $token", owner, repo, ref)
            if (response.isSuccessful) {
                val statusDto = response.body()!!
                val commitStatus = com.issuetrax.app.domain.entity.CommitStatus(
                    state = statusDto.state.toCommitState(),
                    statuses = statusDto.statuses.map { status ->
                        com.issuetrax.app.domain.entity.Status(
                            state = status.state.toCommitState(),
                            context = status.context,
                            description = status.description,
                            targetUrl = status.target_url
                        )
                    },
                    totalCount = statusDto.total_count
                )
                Result.success(commitStatus)
            } else {
                Result.failure(Exception("Failed to get commit status: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCheckRuns(
        owner: String,
        repo: String,
        ref: String
    ): Result<List<com.issuetrax.app.domain.entity.CheckRun>> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.getCheckRuns("Bearer $token", owner, repo, ref)
            if (response.isSuccessful) {
                val checkRunsDto = response.body()!!
                val checkRuns = checkRunsDto.check_runs.map { it.toDomain() }
                Result.success(checkRuns)
            } else {
                Result.failure(Exception("Failed to get check runs: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getWorkflowRuns(
        owner: String,
        repo: String,
        event: String?,
        status: String?
    ): Result<List<com.issuetrax.app.domain.entity.WorkflowRun>> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.getWorkflowRuns(
                authorization = "Bearer $token",
                owner = owner,
                repo = repo,
                event = event,
                status = status
            )
            
            if (response.isSuccessful) {
                val workflowRunsResponse = response.body()!!
                val workflowRuns = workflowRunsResponse.workflow_runs.map { it.toDomain() }
                Result.success(workflowRuns)
            } else {
                Result.failure(Exception("Failed to get workflow runs: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getWorkflowRunsForPR(
        owner: String,
        repo: String,
        headSha: String
    ): Result<List<com.issuetrax.app.domain.entity.WorkflowRun>> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.getWorkflowRuns(
                authorization = "Bearer $token",
                owner = owner,
                repo = repo,
                event = null,
                status = null
            )
            
            if (response.isSuccessful) {
                val workflowRunsResponse = response.body()!!
                // Filter workflow runs by head SHA to get only those for this PR
                val workflowRuns = workflowRunsResponse.workflow_runs
                    .filter { it.head_sha == headSha }
                    .map { it.toDomain() }
                Result.success(workflowRuns)
            } else {
                Result.failure(Exception("Failed to get workflow runs for PR: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun approveWorkflowRun(
        owner: String,
        repo: String,
        runId: Long
    ): Result<Unit> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.approveWorkflowRun(
                authorization = "Bearer $token",
                owner = owner,
                repo = repo,
                runId = runId
            )
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = com.issuetrax.app.data.api.GitHubApiError.getDetailedErrorMessage(
                    response,
                    "Failed to approve workflow run"
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun rerunWorkflowRun(
        owner: String,
        repo: String,
        runId: Long
    ): Result<Unit> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.rerunWorkflowRun(
                authorization = "Bearer $token",
                owner = owner,
                repo = repo,
                runId = runId
            )
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = com.issuetrax.app.data.api.GitHubApiError.getDetailedErrorMessage(
                    response,
                    "Failed to re-run workflow"
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun rerunFailedJobs(
        owner: String,
        repo: String,
        runId: Long
    ): Result<Unit> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.rerunFailedJobs(
                authorization = "Bearer $token",
                owner = owner,
                repo = repo,
                runId = runId
            )
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = com.issuetrax.app.data.api.GitHubApiError.getDetailedErrorMessage(
                    response,
                    "Failed to re-run failed jobs"
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markPrReadyForReview(
        owner: String,
        repo: String,
        prNumber: Int
    ): Result<Unit> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            // First, get the PR to obtain its node_id (required for GraphQL)
            val prResponse = apiService.getPullRequest("Bearer $token", owner, repo, prNumber)
            if (!prResponse.isSuccessful) {
                return Result.failure(Exception("Failed to get PR details: ${prResponse.code()}"))
            }
            
            val prDto = prResponse.body()
                ?: return Result.failure(Exception("PR response body is null"))
            
            if (prDto.draft != true) {
                // PR is not a draft, no action needed
                return Result.success(Unit)
            }
            
            val nodeId = prDto.nodeId
            
            // Use GraphQL mutation to mark as ready for review
            val mutation = """
                mutation MarkReadyForReview(${'$'}pullRequestId: ID!) {
                    markPullRequestReadyForReview(input: {pullRequestId: ${'$'}pullRequestId}) {
                        pullRequest {
                            id
                            isDraft
                        }
                    }
                }
            """.trimIndent()
            
            val request = com.issuetrax.app.data.api.GraphQLRequest(
                query = mutation,
                variables = mapOf("pullRequestId" to nodeId)
            )
            
            val response = apiService.markPrReadyForReview("Bearer $token", request)
            
            if (response.isSuccessful) {
                val graphqlResponse = response.body()
                val errors = graphqlResponse?.errors
                if (!errors.isNullOrEmpty()) {
                    val errorMessages = errors.joinToString(", ") { it.message }
                    return Result.failure(Exception("GraphQL error: $errorMessages"))
                }
                
                val payload = graphqlResponse?.data?.markPullRequestReadyForReview
                if (payload?.pullRequest?.isDraft == false) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to mark PR as ready for review"))
                }
            } else {
                val errorMessage = com.issuetrax.app.data.api.GitHubApiError.getDetailedErrorMessage(
                    response,
                    "Failed to mark PR as ready for review"
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLatestRelease(
        owner: String,
        repo: String
    ): Result<com.issuetrax.app.domain.entity.Release> {
        return try {
            val token = authRepository.getAccessToken()
                ?: return Result.failure(Exception("No access token"))
            
            val response = apiService.getLatestRelease("Bearer $token", owner, repo)
            if (response.isSuccessful) {
                val releaseDto = response.body()
                    ?: return Result.failure(Exception("No release found"))
                Result.success(releaseDto.toDomain())
            } else {
                val errorMessage = com.issuetrax.app.data.api.GitHubApiError.getDetailedErrorMessage(
                    response,
                    "Failed to get latest release"
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun String.toCommitState(): com.issuetrax.app.domain.entity.CommitState {
        return when (this.lowercase()) {
            "pending" -> com.issuetrax.app.domain.entity.CommitState.PENDING
            "success" -> com.issuetrax.app.domain.entity.CommitState.SUCCESS
            "failure" -> com.issuetrax.app.domain.entity.CommitState.FAILURE
            "error" -> com.issuetrax.app.domain.entity.CommitState.ERROR
            else -> com.issuetrax.app.domain.entity.CommitState.PENDING
        }
    }
}