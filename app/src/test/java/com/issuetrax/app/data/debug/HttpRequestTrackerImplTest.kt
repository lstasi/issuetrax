package com.issuetrax.app.data.debug

import com.issuetrax.app.domain.debug.HttpRequestInfo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HttpRequestTrackerImplTest {
    
    private lateinit var tracker: HttpRequestTrackerImpl
    
    @Before
    fun setup() {
        tracker = HttpRequestTrackerImpl()
    }
    
    @Test
    fun `trackRequest adds request to the beginning of the list`() = runTest {
        val request1 = createTestRequest(id = "1", method = "GET")
        val request2 = createTestRequest(id = "2", method = "POST")
        
        tracker.trackRequest(request1)
        tracker.trackRequest(request2)
        
        val requests = tracker.requests.value
        assertEquals(2, requests.size)
        assertEquals("2", requests[0].id) // Most recent first
        assertEquals("1", requests[1].id)
    }
    
    @Test
    fun `trackRequest respects maxRequests limit`() = runTest {
        // Add more than maxRequests
        repeat(tracker.maxRequests + 10) { index ->
            tracker.trackRequest(createTestRequest(id = index.toString()))
        }
        
        val requests = tracker.requests.value
        assertEquals(tracker.maxRequests, requests.size)
    }
    
    @Test
    fun `clearRequests removes all requests`() = runTest {
        tracker.trackRequest(createTestRequest(id = "1"))
        tracker.trackRequest(createTestRequest(id = "2"))
        
        tracker.clearRequests()
        
        assertTrue(tracker.requests.value.isEmpty())
    }
    
    @Test
    fun `HttpRequestInfo isSuccess returns true for 2xx status codes`() {
        val successRequest = createTestRequest(responseCode = 200)
        assertTrue(successRequest.isSuccess)
        
        val anotherSuccess = createTestRequest(responseCode = 201)
        assertTrue(anotherSuccess.isSuccess)
    }
    
    @Test
    fun `HttpRequestInfo isSuccess returns false for non-2xx status codes`() {
        val errorRequest = createTestRequest(responseCode = 404)
        assertFalse(errorRequest.isSuccess)
        
        val serverError = createTestRequest(responseCode = 500)
        assertFalse(serverError.isSuccess)
    }
    
    @Test
    fun `HttpRequestInfo statusSummary returns correct status`() {
        assertEquals("Success", createTestRequest(responseCode = 200).statusSummary)
        assertEquals("Failed", createTestRequest(responseCode = 404).statusSummary)
        assertEquals("Error", createTestRequest(error = "Network error").statusSummary)
        assertEquals("Pending", createTestRequest(responseCode = null).statusSummary)
    }
    
    @Test
    fun `HttpRequestInfo displayUrl removes GitHub API prefix`() {
        val request = createTestRequest(url = "https://api.github.com/repos/owner/repo")
        assertEquals("repos/owner/repo", request.displayUrl)
    }
    
    private fun createTestRequest(
        id: String = "test-id",
        method: String = "GET",
        url: String = "https://api.github.com/test",
        responseCode: Int? = 200,
        error: String? = null
    ) = HttpRequestInfo(
        id = id,
        timestamp = System.currentTimeMillis(),
        method = method,
        url = url,
        requestHeaders = emptyMap(),
        requestBody = null,
        responseCode = responseCode,
        responseMessage = "OK",
        responseHeaders = emptyMap(),
        responseBody = null,
        durationMs = 100L,
        error = error
    )
}
