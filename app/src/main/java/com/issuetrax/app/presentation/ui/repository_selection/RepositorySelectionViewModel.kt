package com.issuetrax.app.presentation.ui.repository_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.issuetrax.app.domain.entity.Repository
import com.issuetrax.app.domain.repository.RepositoryContextRepository
import com.issuetrax.app.domain.usecase.GetUserRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositorySelectionViewModel @Inject constructor(
    private val getUserRepositoriesUseCase: GetUserRepositoriesUseCase,
    private val repositoryContextRepository: RepositoryContextRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RepositorySelectionUiState())
    val uiState: StateFlow<RepositorySelectionUiState> = _uiState.asStateFlow()
    
    init {
        loadRepositories()
    }
    
    fun loadRepositories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            getUserRepositoriesUseCase().collect { result ->
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
    
    fun selectRepository(owner: String, repo: String) {
        viewModelScope.launch {
            repositoryContextRepository.saveSelectedRepository(owner, repo)
            _uiState.value = _uiState.value.copy(selectedRepository = "$owner/$repo")
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class RepositorySelectionUiState(
    val isLoading: Boolean = false,
    val repositories: List<Repository> = emptyList(),
    val selectedRepository: String? = null,
    val error: String? = null
)
