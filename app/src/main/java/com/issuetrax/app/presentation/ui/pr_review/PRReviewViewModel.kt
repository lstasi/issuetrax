package com.issuetrax.app.presentation.ui.pr_review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.issuetrax.app.domain.entity.CodeHunk
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.repository.GitHubRepository
import com.issuetrax.app.domain.usecase.SubmitReviewUseCase
import com.issuetrax.app.domain.usecase.ReviewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View mode for the PR Review screen
 */
enum class PRViewMode {
    FILE_LIST,     // Show list of changed files
    FILE_DIFF,     // Show diff for selected file
    HUNK_DETAIL    // Show full-screen hunk detail
}

@HiltViewModel
class PRReviewViewModel @Inject constructor(
    private val gitHubRepository: GitHubRepository,
    private val submitReviewUseCase: SubmitReviewUseCase,
    private val approvePullRequestUseCase: com.issuetrax.app.domain.usecase.ApprovePullRequestUseCase,
    private val closePullRequestUseCase: com.issuetrax.app.domain.usecase.ClosePullRequestUseCase,
    private val mergePullRequestUseCase: com.issuetrax.app.domain.usecase.MergePullRequestUseCase,
    private val createCommentUseCase: com.issuetrax.app.domain.usecase.CreateCommentUseCase,
    private val getCommitStatusUseCase: com.issuetrax.app.domain.usecase.GetCommitStatusUseCase,
    private val getWorkflowRunsUseCase: com.issuetrax.app.domain.usecase.GetWorkflowRunsUseCase,
    private val approveWorkflowRunUseCase: com.issuetrax.app.domain.usecase.ApproveWorkflowRunUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PRReviewUiState())
    val uiState: StateFlow<PRReviewUiState> = _uiState.asStateFlow()
    
    fun loadPullRequest(owner: String, repo: String, number: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Load PR details
            val prResult = gitHubRepository.getPullRequest(owner, repo, number)
            if (prResult.isSuccess) {
                val pullRequest = prResult.getOrThrow()
                _uiState.value = _uiState.value.copy(pullRequest = pullRequest)
                
                // Load PR files
                val filesResult = gitHubRepository.getPullRequestFiles(owner, repo, number)
                if (filesResult.isSuccess) {
                    val files = filesResult.getOrThrow()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        files = files,
                        currentFileIndex = -1,
                        viewMode = PRViewMode.FILE_LIST
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = filesResult.exceptionOrNull()?.message ?: "Failed to load files"
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = prResult.exceptionOrNull()?.message ?: "Failed to load pull request"
                )
            }
        }
    }
    
    fun navigateToNextFile() {
        val currentState = _uiState.value
        if (currentState.currentFileIndex < currentState.files.size - 1) {
            _uiState.value = currentState.copy(
                currentFileIndex = currentState.currentFileIndex + 1
            )
        }
    }
    
    fun navigateToPreviousFile() {
        val currentState = _uiState.value
        if (currentState.currentFileIndex > 0) {
            _uiState.value = currentState.copy(
                currentFileIndex = currentState.currentFileIndex - 1
            )
        }
    }
    
    fun navigateToFile(index: Int) {
        val currentState = _uiState.value
        if (index >= 0 && index < currentState.files.size) {
            _uiState.value = currentState.copy(
                currentFileIndex = index,
                viewMode = PRViewMode.FILE_DIFF
            )
        }
    }
    
    fun navigateToFileList() {
        _uiState.value = _uiState.value.copy(
            viewMode = PRViewMode.FILE_LIST,
            currentFileIndex = -1,
            selectedHunk = null
        )
    }
    
    fun selectHunk(hunk: CodeHunk, hunkIndex: Int) {
        _uiState.value = _uiState.value.copy(
            selectedHunk = hunk,
            selectedHunkIndex = hunkIndex,
            viewMode = PRViewMode.HUNK_DETAIL
        )
    }
    
    fun closeHunkDetail() {
        _uiState.value = _uiState.value.copy(
            selectedHunk = null,
            selectedHunkIndex = -1,
            viewMode = PRViewMode.FILE_DIFF
        )
    }
    
    fun submitReview(owner: String, repo: String, prNumber: Int, body: String?, event: ReviewEvent) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingReview = true, error = null)
            
            val result = submitReviewUseCase(owner, repo, prNumber, body, event)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    reviewSubmitted = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to submit review"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Approves the pull request with an optional comment.
     */
    fun approvePullRequest(owner: String, repo: String, prNumber: Int, comment: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingReview = true, error = null)
            
            val result = approvePullRequestUseCase(owner, repo, prNumber, comment)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    reviewSubmitted = true,
                    actionMessage = "Pull request approved successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to approve pull request"
                )
            }
        }
    }
    
    /**
     * Closes the pull request without merging.
     */
    fun closePullRequest(owner: String, repo: String, prNumber: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingReview = true, error = null)
            
            val result = closePullRequestUseCase(owner, repo, prNumber)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    actionMessage = "Pull request closed successfully"
                )
                // Reload PR to update state
                loadPullRequest(owner, repo, prNumber)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to close pull request"
                )
            }
        }
    }
    
    /**
     * Clears action message after it's been displayed.
     */
    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(actionMessage = null)
    }
    
    /**
     * Merges the pull request.
     * If the PR is a draft, it will first be marked as ready for review.
     */
    fun mergePullRequest(
        owner: String,
        repo: String,
        prNumber: Int,
        commitTitle: String? = null,
        commitMessage: String? = null,
        mergeMethod: String = "merge"
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingReview = true, error = null)
            
            // Check if PR is a draft
            val isDraft = _uiState.value.pullRequest?.draft == true
            
            val result = mergePullRequestUseCase(
                owner = owner,
                repo = repo,
                prNumber = prNumber,
                commitTitle = commitTitle,
                commitMessage = commitMessage,
                mergeMethod = mergeMethod,
                isDraft = isDraft
            )
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    actionMessage = "Pull request merged successfully"
                )
                // Reload PR to update state
                loadPullRequest(owner, repo, prNumber)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to merge pull request"
                )
            }
        }
    }
    
    /**
     * Creates a comment on the pull request.
     */
    fun createComment(owner: String, repo: String, prNumber: Int, body: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingReview = true, error = null)
            
            val result = createCommentUseCase(owner, repo, prNumber, body)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    actionMessage = "Comment posted successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to post comment"
                )
            }
        }
    }
    
    /**
     * Loads commit status for the PR's head reference.
     */
    fun loadCommitStatus(owner: String, repo: String) {
        viewModelScope.launch {
            val pullRequest = _uiState.value.pullRequest ?: return@launch
            
            val result = getCommitStatusUseCase(owner, repo, pullRequest.headRef)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    commitStatus = result.getOrNull()
                )
            }
            // Silently fail - commit status is optional information
        }
    }
    
    /**
     * Loads workflow runs for the repository.
     */
    fun loadWorkflowRuns(owner: String, repo: String) {
        viewModelScope.launch {
            val result = getWorkflowRunsUseCase(owner, repo, event = "pull_request", status = "waiting")
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    workflowRuns = result.getOrNull() ?: emptyList()
                )
            }
            // Silently fail - workflow runs are optional information
        }
    }
    
    /**
     * Approves a workflow run that requires manual approval.
     */
    fun approveWorkflowRun(owner: String, repo: String) {
        viewModelScope.launch {
            // Find the first waiting workflow run
            val workflowRuns = _uiState.value.workflowRuns
            val waitingRun = workflowRuns.firstOrNull { it.status == "waiting" }
            
            if (waitingRun == null) {
                _uiState.value = _uiState.value.copy(
                    actionMessage = "No workflow runs require approval"
                )
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(isSubmittingReview = true, error = null)
            
            val result = approveWorkflowRunUseCase(owner, repo, waitingRun.id)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    actionMessage = "Workflow run approved successfully"
                )
                // Reload workflow runs to update status
                loadWorkflowRuns(owner, repo)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSubmittingReview = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to approve workflow run"
                )
            }
        }
    }
}

data class PRReviewUiState(
    val isLoading: Boolean = false,
    val isSubmittingReview: Boolean = false,
    val pullRequest: PullRequest? = null,
    val files: List<FileDiff> = emptyList(),
    val currentFileIndex: Int = -1,
    val reviewSubmitted: Boolean = false,
    val error: String? = null,
    val actionMessage: String? = null,
    val viewMode: PRViewMode = PRViewMode.FILE_LIST,
    val selectedHunk: CodeHunk? = null,
    val selectedHunkIndex: Int = -1,
    val commitStatus: com.issuetrax.app.domain.entity.CommitStatus? = null,
    val workflowRuns: List<com.issuetrax.app.domain.entity.WorkflowRun> = emptyList()
) {
    val currentFile: FileDiff?
        get() = if (currentFileIndex >= 0 && currentFileIndex < files.size) {
            files[currentFileIndex]
        } else null
}