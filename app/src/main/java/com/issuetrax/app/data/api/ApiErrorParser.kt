package com.issuetrax.app.data.api

import android.util.Log
import com.issuetrax.app.data.api.model.GitHubErrorResponseDto
import kotlinx.serialization.json.Json
import retrofit2.Response

/**
 * Utility for parsing GitHub API error responses
 */
object ApiErrorParser {
    private const val TAG = "ApiErrorParser"
    
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    /**
     * Parse error response from GitHub API and create detailed error message
     */
    fun parseErrorResponse(response: Response<*>): String {
        val errorBody = response.errorBody()?.string()
        val statusCode = response.code()
        val statusMessage = response.message()
        
        if (errorBody.isNullOrBlank()) {
            Log.w(TAG, "API error $statusCode: $statusMessage (no error body)")
            return "Failed with status $statusCode: $statusMessage"
        }
        
        return try {
            val errorResponse = json.decodeFromString<GitHubErrorResponseDto>(errorBody)
            val errorMessage = buildString {
                append("$statusCode: ${errorResponse.message}")
                
                if (!errorResponse.errors.isNullOrEmpty()) {
                    append("\nDetails:")
                    errorResponse.errors.forEach { error ->
                        append("\n  - ")
                        if (error.field != null) {
                            append("${error.field}: ")
                        }
                        append(error.code)
                        if (error.message != null) {
                            append(" (${error.message})")
                        }
                        if (error.value != null) {
                            append(" [value: ${error.value}]")
                        }
                    }
                }
                
                if (errorResponse.documentation_url != null) {
                    append("\nSee: ${errorResponse.documentation_url}")
                }
            }
            
            Log.e(TAG, "GitHub API Error: $errorMessage")
            Log.d(TAG, "Error body: $errorBody")
            
            errorMessage
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse error response: ${e.message}", e)
            Log.d(TAG, "Raw error body: $errorBody")
            "Failed with status $statusCode: $statusMessage\nError: $errorBody"
        }
    }
}
