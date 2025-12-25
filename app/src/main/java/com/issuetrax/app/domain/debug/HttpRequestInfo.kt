package com.issuetrax.app.domain.debug

/**
 * Data class representing HTTP request/response information for debug mode.
 * 
 * This is used to track and display HTTP requests in the debug panel.
 */
data class HttpRequestInfo(
    val id: String,
    val timestamp: Long,
    val method: String,
    val url: String,
    val requestHeaders: Map<String, String>,
    val requestBody: String?,
    val responseCode: Int?,
    val responseMessage: String?,
    val responseHeaders: Map<String, String>?,
    val responseBody: String?,
    val durationMs: Long?,
    val error: String?
) {
    val isSuccess: Boolean
        get() = responseCode != null && responseCode in 200..299
    
    val statusSummary: String
        get() = when {
            error != null -> "Error"
            responseCode == null -> "Pending"
            isSuccess -> "Success"
            else -> "Failed"
        }
    
    val displayUrl: String
        get() = url.removePrefix("https://api.github.com/")
}
