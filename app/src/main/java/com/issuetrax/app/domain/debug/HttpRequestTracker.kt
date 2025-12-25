package com.issuetrax.app.domain.debug

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for tracking HTTP requests in debug mode.
 */
interface HttpRequestTracker {
    /**
     * Observable list of tracked HTTP requests.
     * Most recent requests appear first.
     */
    val requests: StateFlow<List<HttpRequestInfo>>
    
    /**
     * Add a new HTTP request to the tracker.
     */
    fun trackRequest(request: HttpRequestInfo)
    
    /**
     * Clear all tracked requests.
     */
    fun clearRequests()
    
    /**
     * Get maximum number of requests to keep in memory.
     */
    val maxRequests: Int
}
