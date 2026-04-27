package com.issuetrax.app.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.issuetrax.app.domain.usecase.AuthenticateUseCase
import com.issuetrax.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authenticateUseCase: AuthenticateUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        // Check if user is already authenticated
        viewModelScope.launch {
            authRepository.isAuthenticated().collect { isAuthenticated ->
                if (isAuthenticated) {
                    _uiState.value = _uiState.value.copy(isAuthenticated = true)
                }
            }
        }
    }
    
    fun authenticate(authCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Validate GitHub token format
            if (!isValidGitHubToken(authCode)) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Invalid GitHub token format. Token should start with ghp_, gho_, ghu_, ghs_, or ghr_"
                )
                return@launch
            }

            val result = authenticateUseCase(authCode)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Authentication failed"
                )
            }
        }
    }

    private fun isValidGitHubToken(token: String): Boolean {
        // GitHub Personal Access Tokens have specific prefixes:
        // ghp_ - Personal Access Token
        // gho_ - OAuth Access Token
        // ghu_ - User-to-server token
        // ghs_ - Server-to-server token
        // ghr_ - Refresh token
        val validPrefixes = listOf("ghp_", "gho_", "ghu_", "ghs_", "ghr_")
        return validPrefixes.any { token.startsWith(it) } && token.length >= 40
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)