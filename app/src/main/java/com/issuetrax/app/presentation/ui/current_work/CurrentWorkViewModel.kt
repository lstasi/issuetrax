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

enum class PRFilter {
    ALL, OPEN, CLOSED
}

enum class PRSortOrder {
    CREATED, UPDATED, COMMENTS
}

@HiltViewModel
class CurrentWorkViewModel @Inject constructor(
    private val getPullRequestsUseCase: GetPullRequestsUseCase,
    private val gitHubRepository: GitHubRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CurrentWorkUiState())
    val uiState: StateFlow<CurrentWorkUiState> = _uiState.asStateFlow()
    
    // Store all fetched PRs for filtering/sorting
    private var allPullRequests: List<PullRequest> = emptyList()
    
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
    
    fun loadPullRequests(owner: String, repo: String, state: String = "open") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPRs = true, error = null)
            
            getPullRequestsUseCase(owner, repo, state).collect { result ->
                if (result.isSuccess) {
                    val pullRequests = result.getOrNull() ?: emptyList()
                    allPullRequests = pullRequests
                    val filtered = applyFilterAndSort(pullRequests)
                    _uiState.value = _uiState.value.copy(
                        isLoadingPRs = false,
                        pullRequests = filtered,
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
    
    fun refreshPullRequests() {
        val selectedRepo = _uiState.value.selectedRepository ?: return
        val parts = selectedRepo.split("/")
        if (parts.size == 2) {
            val state = when (_uiState.value.filter) {
                PRFilter.OPEN -> "open"
                PRFilter.CLOSED -> "closed"
                PRFilter.ALL -> "all"
            }
            loadPullRequests(parts[0], parts[1], state)
        }
    }
    
    fun filterPullRequests(filter: PRFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
        
        // Reload PRs with the new filter state
        val selectedRepo = _uiState.value.selectedRepository ?: return
        val parts = selectedRepo.split("/")
        if (parts.size == 2) {
            val state = when (filter) {
                PRFilter.OPEN -> "open"
                PRFilter.CLOSED -> "closed"
                PRFilter.ALL -> "all"
            }
            loadPullRequests(parts[0], parts[1], state)
        }
    }
    
    fun sortPullRequests(sortOrder: PRSortOrder) {
        _uiState.value = _uiState.value.copy(sortBy = sortOrder)
        val sorted = applyFilterAndSort(allPullRequests)
        _uiState.value = _uiState.value.copy(pullRequests = sorted)
    }
    
    private fun applyFilterAndSort(pullRequests: List<PullRequest>): List<PullRequest> {
        // Note: filtering by state is done at API level, so we just sort here
        return when (_uiState.value.sortBy) {
            PRSortOrder.CREATED -> pullRequests.sortedByDescending { it.createdAt }
            PRSortOrder.UPDATED -> pullRequests.sortedByDescending { it.updatedAt }
            PRSortOrder.COMMENTS -> pullRequests.sortedByDescending { it.commits } // Using commits as proxy since we don't have comments count
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
    val filter: PRFilter = PRFilter.OPEN,
    val sortBy: PRSortOrder = PRSortOrder.UPDATED,
    val error: String? = null
)