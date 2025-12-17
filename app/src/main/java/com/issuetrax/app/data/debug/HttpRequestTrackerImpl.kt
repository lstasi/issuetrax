package com.issuetrax.app.data.debug

import com.issuetrax.app.domain.debug.HttpRequestInfo
import com.issuetrax.app.domain.debug.HttpRequestTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of HttpRequestTracker that stores requests in memory.
 * Thread-safe implementation suitable for concurrent HTTP requests.
 */
@Singleton
class HttpRequestTrackerImpl @Inject constructor() : HttpRequestTracker {
    
    override val maxRequests: Int = 50
    
    private val _requests = MutableStateFlow<List<HttpRequestInfo>>(emptyList())
    override val requests: StateFlow<List<HttpRequestInfo>> = _requests.asStateFlow()
    
    override fun trackRequest(request: HttpRequestInfo) {
        synchronized(this) {
            val currentRequests = _requests.value.toMutableList()
            currentRequests.add(0, request) // Add to beginning (most recent first)
            
            // Keep only the last maxRequests
            if (currentRequests.size > maxRequests) {
                currentRequests.subList(maxRequests, currentRequests.size).clear()
            }
            
            _requests.value = currentRequests
        }
    }
    
    override fun clearRequests() {
        _requests.value = emptyList()
    }
}
