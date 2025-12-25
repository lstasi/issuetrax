# Debug Mode HTTP Request Panel

## Overview

The Issuetrax app includes a debug-only HTTP request tracking panel that appears at the bottom of the screen when running debug builds. This feature helps developers monitor and troubleshoot API interactions during development.

## Features

### 1. Automatic Request Tracking
- All HTTP requests made through the app are automatically captured
- Request and response details are stored in memory
- Maximum of 50 requests are kept (oldest are automatically removed)

### 2. Compact Request Bar
When an HTTP request is made, a small bar appears at the bottom of the screen showing:
- HTTP method (GET, POST, etc.)
- Request URL (GitHub API path)
- Response status (Success, Failed, Error, Pending)
- HTTP status code
- Request duration in milliseconds

The bar is color-coded:
- **Green background**: Successful request (2xx status code)
- **Orange background**: Failed request (4xx, 5xx status codes)
- **Red background**: Network error

### 3. Expandable Request List
Tap on the compact bar to expand and see:
- Full list of recent HTTP requests (up to 50)
- Each request shows method, URL, status code, duration, and timestamp
- Tap any request to view detailed information

### 4. Detailed Request View
Selecting a specific request shows comprehensive information:
- **Request Summary**: Method, URL, status code, duration
- **Request Headers**: All HTTP headers sent with the request
- **Request Body**: Request payload (if applicable)
- **Response Headers**: All HTTP headers received
- **Response Body**: Response payload (first 1000 characters)
- **Error Details**: Error messages if the request failed

### 5. Actions
- **Expand/Collapse**: Toggle between compact and expanded view
- **Clear All**: Remove all tracked requests from memory
- **Back to List**: Return from detail view to request list

## Technical Implementation

### Architecture

The debug panel follows Clean Architecture principles:

1. **Domain Layer**
   - `HttpRequestInfo`: Data class representing request/response details
   - `HttpRequestTracker`: Interface for tracking HTTP requests

2. **Data Layer**
   - `HttpRequestTrackerImpl`: In-memory implementation of request tracking
   - `DebugLoggingInterceptor`: OkHttp interceptor that captures requests

3. **Presentation Layer**
   - `DebugPanel`: Composable UI component
   - `DebugPanelViewModel`: State management for the UI

4. **Dependency Injection**
   - `DebugModule`: Provides debug-related dependencies
   - `NetworkModule`: Integrates debug interceptor (debug builds only)

### Debug-Only Behavior

The debug panel is **only active in debug builds**:

```kotlin
// In MainActivity.kt
if (BuildConfig.DEBUG) {
    DebugPanel(modifier = Modifier.align(Alignment.BottomCenter))
}

// In NetworkModule.kt
if (BuildConfig.DEBUG) {
    builder.addInterceptor(debugLoggingInterceptor)
}
```

Release builds:
- Do not include the DebugLoggingInterceptor
- Do not show the DebugPanel UI
- Have no performance impact from debug tracking

### State Management

- Uses Kotlin Flow for reactive state updates
- StateFlow provides thread-safe access to request list
- Requests are stored in memory (not persisted)
- Automatic cleanup when app is closed

### Privacy and Security

- Request tracking is **debug-only** and never active in production
- Sensitive data in headers (like Authorization tokens) is captured but only visible during development
- Request/response bodies are truncated to 1000 characters to limit memory usage
- All tracked data is cleared when the app is closed

## Usage During Development

### Viewing API Requests

1. Run the app in debug mode
2. Perform actions that trigger API calls (login, view PRs, etc.)
3. Observe the debug bar appearing at the bottom
4. Tap the bar to expand and see all requests
5. Tap any request to view full details

### Debugging API Issues

The debug panel is particularly useful for:
- **Verifying request parameters**: Check that URLs and headers are correct
- **Inspecting response data**: See exactly what the API returned
- **Timing analysis**: Identify slow requests
- **Error diagnosis**: View detailed error messages
- **Rate limiting**: Monitor API usage patterns

### Example Scenarios

**Scenario 1: Checking Authentication**
1. Expand the debug panel
2. Find a request to the GitHub API
3. View request headers
4. Verify the Authorization header is present and correct

**Scenario 2: Debugging a Failed Request**
1. Notice a red error bar at the bottom
2. Tap to expand and select the failed request
3. View the response body to see the error details
4. Check the status code (404, 401, etc.)
5. Copy the error message for bug reporting

**Scenario 3: Performance Monitoring**
1. Make several API requests
2. Expand the debug panel
3. Compare request durations
4. Identify unusually slow requests

## Code Examples

### Accessing Debug Information Programmatically

```kotlin
@Inject
lateinit var requestTracker: HttpRequestTracker

// Observe requests in a ViewModel
val requests: StateFlow<List<HttpRequestInfo>> = requestTracker.requests
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

// Get latest request
val latestRequest = requestTracker.requests.value.firstOrNull()

// Clear all requests
requestTracker.clearRequests()
```

### HttpRequestInfo Properties

```kotlin
data class HttpRequestInfo(
    val id: String,                               // Unique request ID
    val timestamp: Long,                          // Request time
    val method: String,                           // GET, POST, etc.
    val url: String,                              // Full URL
    val requestHeaders: Map<String, String>,      // Request headers
    val requestBody: String?,                     // Request body (if any)
    val responseCode: Int?,                       // HTTP status code
    val responseMessage: String?,                 // Status message
    val responseHeaders: Map<String, String>?,    // Response headers
    val responseBody: String?,                    // Response body (truncated)
    val durationMs: Long?,                        // Request duration
    val error: String?                            // Error message (if failed)
) {
    val isSuccess: Boolean                        // True for 2xx codes
    val statusSummary: String                     // "Success", "Failed", "Error", "Pending"
    val displayUrl: String                        // URL without API prefix
}
```

## Testing

Unit tests are provided for the core tracking functionality:

```bash
./gradlew testDebugUnitTest
```

Tests verify:
- Requests are added to the tracker
- Maximum request limit is enforced
- Clear functionality works
- Request status is correctly determined
- URL display formatting works

## Future Enhancements

Potential improvements for the debug panel:
- Search/filter requests by URL or status
- Export requests to JSON for sharing
- Request replay functionality
- Network statistics dashboard
- Custom tags for grouping requests

## Related Files

- `app/src/main/java/com/issuetrax/app/domain/debug/`
- `app/src/main/java/com/issuetrax/app/data/debug/`
- `app/src/main/java/com/issuetrax/app/data/api/DebugLoggingInterceptor.kt`
- `app/src/main/java/com/issuetrax/app/presentation/ui/debug/`
- `app/src/main/java/com/issuetrax/app/di/DebugModule.kt`
- `app/src/test/java/com/issuetrax/app/data/debug/`

## Troubleshooting

### Debug panel not appearing
- Verify you're running a debug build, not release
- Check that at least one HTTP request has been made
- Ensure BuildConfig.DEBUG is true

### Requests not being tracked
- Verify DebugLoggingInterceptor is added to OkHttpClient
- Check that the interceptor is only added in debug builds
- Look for any exceptions in logcat

### Memory concerns
- The tracker automatically limits to 50 requests
- Request/response bodies are truncated to 1000 characters
- All data is cleared when the app is closed
- Memory usage is minimal for typical development scenarios
