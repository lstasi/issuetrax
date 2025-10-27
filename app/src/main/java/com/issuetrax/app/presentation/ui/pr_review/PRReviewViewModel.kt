package com.issuetrax.app.presentation.ui.pr_review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class PRReviewViewModel @Inject constructor(
    private val gitHubRepository: GitHubRepository,
    private val submitReviewUseCase: SubmitReviewUseCase,
    private val approvePullRequestUseCase: com.issuetrax.app.domain.usecase.ApprovePullRequestUseCase,
    private val closePullRequestUseCase: com.issuetrax.app.domain.usecase.ClosePullRequestUseCase
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
                        currentFileIndex = if (files.isNotEmpty()) 0 else -1
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
                viewState = PRReviewViewState.FILE_DIFF
            )
        }
    }
    
    /**
     * Returns to the file list view.
     */
    fun returnToFileList() {
        _uiState.value = _uiState.value.copy(
            viewState = PRReviewViewState.FILE_LIST
        )
    }
    
    /**
     * Shows a specific chunk in full screen.
     */
    fun showChunkDetail(chunkIndex: Int) {
        _uiState.value = _uiState.value.copy(
            viewState = PRReviewViewState.CHUNK_DETAIL,
            selectedChunkIndex = chunkIndex
        )
    }
    
    /**
     * Returns from chunk detail to file diff view.
     */
    fun returnToFileDiff() {
        _uiState.value = _uiState.value.copy(
            viewState = PRReviewViewState.FILE_DIFF,
            selectedChunkIndex = -1
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
}

/**
 * Navigation view state for PR review screen.
 */
enum class PRReviewViewState {
    /** Showing the list of files */
    FILE_LIST,
    /** Showing the diff for a specific file */
    FILE_DIFF,
    /** Showing a specific chunk in full screen */
    CHUNK_DETAIL
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
    val viewState: PRReviewViewState = PRReviewViewState.FILE_LIST,
    val selectedChunkIndex: Int = -1
) {
    val currentFile: FileDiff?
        get() = if (currentFileIndex >= 0 && currentFileIndex < files.size) {
            files[currentFileIndex]
        } else null
}