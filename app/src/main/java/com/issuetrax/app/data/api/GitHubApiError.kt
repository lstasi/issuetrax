package com.issuetrax.app.data.api

import com.issuetrax.app.data.api.model.GitHubErrorDto
import kotlinx.serialization.json.Json
import retrofit2.Response

/**
 * Utilities for handling GitHub API errors with detailed information.
 */
object GitHubApiError {
    
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    /**
     * Extracts a detailed error message from a failed API response.
     * 
     * Attempts to parse the error body to extract GitHub's detailed error message.
     * Falls back to a generic message if parsing fails.
     * 
     * @param response The failed Retrofit response
     * @param defaultMessage Default message to use if error body cannot be parsed
     * @return A detailed error message
     */
    fun <T> getDetailedErrorMessage(response: Response<T>, defaultMessage: String): String {
        val statusCode = response.code()
        val statusMessage = response.message()
        
        // Try to parse the error body
        // Note: errorBody().string() consumes the response body and can only be called once
        val errorBody = response.errorBody()?.string()
        if (!errorBody.isNullOrEmpty()) {
            try {
                val githubError = json.decodeFromString<GitHubErrorDto>(errorBody)
                
                // Build a comprehensive error message
                val baseMessage = "$defaultMessage: $statusCode - ${githubError.message}"
                
                // Add specific error details if available
                val details = githubError.errors?.joinToString("; ") { detail ->
                    buildString {
                        detail.resource?.let { append("Resource: $it") }
                        detail.field?.let { 
                            if (isNotEmpty()) append(", ")
                            append("Field: $it") 
                        }
                        detail.code?.let { 
                            if (isNotEmpty()) append(", ")
                            append("Code: $it") 
                        }
                        detail.message?.let { 
                            if (isNotEmpty()) append(" - ")
                            append(it) 
                        }
                    }
                }
                
                // Add contextual help for common 403 errors
                val contextHelp = when {
                    statusCode == 403 && githubError.message.contains("rate limit", ignoreCase = true) -> {
                        "\n\nThis is a rate limit error. Please wait and try again later."
                    }
                    statusCode == 403 && (
                        githubError.message.contains("permission", ignoreCase = true) ||
                        githubError.message.contains("scope", ignoreCase = true) ||
                        githubError.message.contains("accessible", ignoreCase = true) ||
                        githubError.message.contains("integration", ignoreCase = true)
                    ) -> {
                        "\n\nThis is a permissions error. Your GitHub token may not have the required permissions/scopes. " +
                        "For merge operations, ensure the token has 'repo' scope. " +
                        "For workflow approvals, ensure the token has 'actions' scope."
                    }
                    statusCode == 403 && githubError.message.contains("protected", ignoreCase = true) -> {
                        "\n\nThe branch is protected and merge requirements are not met. " +
                        "Check if required reviews, status checks, or other branch protection rules are satisfied."
                    }
                    statusCode == 403 && githubError.message.contains("forbidden", ignoreCase = true) -> {
                        "\n\nThis request is forbidden. Possible reasons:\n" +
                        "• Insufficient token permissions/scopes\n" +
                        "• Branch protection rules not satisfied\n" +
                        "• Repository access restrictions\n" +
                        "• Rate limit exceeded"
                    }
                    statusCode == 403 -> {
                        "\n\nThis request returned a 403 Forbidden status. Possible reasons:\n" +
                        "• Insufficient token permissions/scopes\n" +
                        "• Branch protection rules not satisfied\n" +
                        "• Repository access restrictions\n" +
                        "• Rate limit exceeded"
                    }
                    else -> ""
                }
                
                return buildString {
                    append(baseMessage)
                    if (!details.isNullOrEmpty()) {
                        append("\n\nDetails: $details")
                    }
                    append(contextHelp)
                }
            } catch (e: Exception) {
                // If parsing fails, return a formatted message with available info
                return "$defaultMessage: $statusCode - $statusMessage\n\nResponse: ${errorBody.take(200)}"
            }
        }
        
        // Fallback if no error body
        return "$defaultMessage: $statusCode - $statusMessage"
    }
}
