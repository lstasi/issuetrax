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
    private val submitReviewUseCase: SubmitReviewUseCase
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
}

data class PRReviewUiState(
    val isLoading: Boolean = false,
    val isSubmittingReview: Boolean = false,
    val pullRequest: PullRequest? = null,
    val files: List<FileDiff> = emptyList(),
    val currentFileIndex: Int = -1,
    val reviewSubmitted: Boolean = false,
    val error: String? = null
) {
    val currentFile: FileDiff?
        get() = if (currentFileIndex >= 0 && currentFileIndex < files.size) {
            files[currentFileIndex]
        } else null
}