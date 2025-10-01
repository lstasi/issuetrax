package com.issuetrax.app.presentation.ui.current_work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.entity.Repository
import com.issuetrax.app.domain.repository.GitHubRepository
import com.issuetrax.app.domain.usecase.GetPullRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentWorkViewModel @Inject constructor(
    private val getPullRequestsUseCase: GetPullRequestsUseCase,
    private val gitHubRepository: GitHubRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CurrentWorkUiState())
    val uiState: StateFlow<CurrentWorkUiState> = _uiState.asStateFlow()
    
    fun loadUserRepositories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            gitHubRepository.getUserRepositories().collect { result ->
                if (result.isSuccess) {
                    val repositories = result.getOrNull() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        repositories = repositories
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load repositories"
                    )
                }
            }
        }
    }
    
    fun loadPullRequests(owner: String, repo: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPRs = true, error = null)
            
            getPullRequestsUseCase(owner, repo).collect { result ->
                if (result.isSuccess) {
                    val pullRequests = result.getOrNull() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        isLoadingPRs = false,
                        pullRequests = pullRequests,
                        selectedRepository = "$owner/$repo"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoadingPRs = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to load pull requests"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class CurrentWorkUiState(
    val isLoading: Boolean = false,
    val isLoadingPRs: Boolean = false,
    val repositories: List<Repository> = emptyList(),
    val pullRequests: List<PullRequest> = emptyList(),
    val selectedRepository: String? = null,
    val error: String? = null
)