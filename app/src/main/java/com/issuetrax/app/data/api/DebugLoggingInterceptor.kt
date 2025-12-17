package com.issuetrax.app.data.api

import com.issuetrax.app.domain.debug.HttpRequestInfo
import com.issuetrax.app.domain.debug.HttpRequestTracker
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

/**
 * OkHttp interceptor that tracks HTTP requests in debug mode.
 * Captures request and response details for display in the debug panel.
 */
class DebugLoggingInterceptor @Inject constructor(
    private val requestTracker: HttpRequestTracker
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()
        
        // Capture request details
        val requestInfo = captureRequest(request, requestId, startTime)
        
        try {
            val response = chain.proceed(request)
            val endTime = System.currentTimeMillis()
            
            // Capture response details
            val completeInfo = captureResponse(requestInfo, response, endTime - startTime)
            requestTracker.trackRequest(completeInfo)
            
            return response
        } catch (e: IOException) {
            val endTime = System.currentTimeMillis()
            
            // Capture error details
            val errorInfo = requestInfo.copy(
                durationMs = endTime - startTime,
                error = e.message ?: "Network error"
            )
            requestTracker.trackRequest(errorInfo)
            
            throw e
        }
    }
    
    private fun captureRequest(request: Request, requestId: String, timestamp: Long): HttpRequestInfo {
        val headers = request.headers.toMultimap().mapValues { it.value.joinToString(", ") }
        val body = try {
            request.body?.let { requestBody ->
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                buffer.readUtf8().take(1000) // Limit body size
            }
        } catch (e: Exception) {
            "[Error reading body: ${e.message}]"
        }
        
        return HttpRequestInfo(
            id = requestId,
            timestamp = timestamp,
            method = request.method,
            url = request.url.toString(),
            requestHeaders = headers,
            requestBody = body,
            responseCode = null,
            responseMessage = null,
            responseHeaders = null,
            responseBody = null,
            durationMs = null,
            error = null
        )
    }
    
    private fun captureResponse(
        requestInfo: HttpRequestInfo,
        response: Response,
        durationMs: Long
    ): HttpRequestInfo {
        val headers = response.headers.toMultimap().mapValues { it.value.joinToString(", ") }
        val body = try {
            response.peekBody(1000).string() // Peek at response body without consuming it
        } catch (e: Exception) {
            "[Error reading body: ${e.message}]"
        }
        
        return requestInfo.copy(
            responseCode = response.code,
            responseMessage = response.message,
            responseHeaders = headers,
            responseBody = body,
            durationMs = durationMs
        )
    }
}
