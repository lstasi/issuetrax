package com.issuetrax.app.presentation.ui.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.issuetrax.app.domain.debug.HttpRequestInfo
import com.issuetrax.app.domain.debug.HttpRequestTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the debug panel that displays HTTP requests.
 */
@HiltViewModel
class DebugPanelViewModel @Inject constructor(
    private val requestTracker: HttpRequestTracker
) : ViewModel() {
    
    val requests: StateFlow<List<HttpRequestInfo>> = requestTracker.requests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun clearRequests() {
        requestTracker.clearRequests()
    }
}
