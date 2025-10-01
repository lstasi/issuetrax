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
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)