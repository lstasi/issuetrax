package com.issuetrax.app.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RateLimitInterceptor @Inject constructor() : Interceptor {
    
    companion object {
        private const val TAG = "RateLimitInterceptor"
        private const val HEADER_RATE_LIMIT = "X-RateLimit-Limit"
        private const val HEADER_RATE_REMAINING = "X-RateLimit-Remaining"
        private const val HEADER_RATE_RESET = "X-RateLimit-Reset"
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // Log rate limit information
        val rateLimit = response.header(HEADER_RATE_LIMIT)
        val rateRemaining = response.header(HEADER_RATE_REMAINING)
        val rateReset = response.header(HEADER_RATE_RESET)
        
        if (rateLimit != null && rateRemaining != null) {
            Log.d(TAG, "GitHub API Rate Limit: $rateRemaining/$rateLimit remaining")
            
            if (rateRemaining.toIntOrNull() == 0 && rateReset != null) {
                val resetTime = rateReset.toLongOrNull()?.times(1000) ?: 0
                val currentTime = System.currentTimeMillis()
                val waitTime = (resetTime - currentTime) / 1000
                Log.w(TAG, "Rate limit exceeded. Reset in $waitTime seconds")
            }
        }
        
        // Check for rate limit error response
        if (response.code == 403) {
            val rateLimitExceeded = response.header("X-RateLimit-Remaining") == "0"
            if (rateLimitExceeded) {
                Log.e(TAG, "GitHub API rate limit exceeded")
            }
        }
        
        return response
    }
}
