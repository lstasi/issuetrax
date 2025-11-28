package com.issuetrax.app.presentation.ui.create_issue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.issuetrax.app.domain.usecase.CreateIssueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateIssueUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val issueCreated: Boolean = false
)

@HiltViewModel
class CreateIssueViewModel @Inject constructor(
    private val createIssueUseCase: CreateIssueUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateIssueUiState())
    val uiState: StateFlow<CreateIssueUiState> = _uiState.asStateFlow()

    fun createIssue(
        owner: String,
        repo: String,
        title: String,
        body: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = createIssueUseCase(
                owner = owner,
                repo = repo,
                title = title,
                body = body,
                assignees = listOf("Copilot") // Auto-assign to Copilot after issue creation
            )

            result.fold(
                onSuccess = {
                    _uiState.value = CreateIssueUiState(
                        isLoading = false,
                        issueCreated = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = CreateIssueUiState(
                        isLoading = false,
                        error = error.message ?: "Failed to create issue"
                    )
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = CreateIssueUiState()
    }
}
