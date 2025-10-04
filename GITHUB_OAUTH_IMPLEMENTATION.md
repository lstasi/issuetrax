# GitHub OAuth Integration Implementation

This document describes the GitHub OAuth integration that has been implemented for the Issuetrax application.

## Overview

The GitHub OAuth integration enables users to authenticate with their GitHub account and securely access GitHub APIs. The implementation follows Android security best practices and uses the OAuth 2.0 authorization code flow.

## Components Implemented

### 1. OAuth Service (`GitHubOAuthService.kt`)

- **Purpose**: Handles OAuth token exchange with GitHub
- **Endpoint**: `POST /login/oauth/access_token`
- **Features**:
  - Exchanges authorization code for access token
  - Returns access token, token type, and scope
  - Uses Kotlin serialization for JSON parsing

### 2. Authentication Interceptor (`AuthInterceptor.kt`)

- **Purpose**: Automatically adds OAuth tokens to API requests
- **Features**:
  - Injects `Authorization: Bearer <token>` header to all GitHub API requests
  - Skips injection for OAuth token exchange endpoint
  - Retrieves token from AuthRepository

### 3. Rate Limit Interceptor (`RateLimitInterceptor.kt`)

- **Purpose**: Monitors and logs GitHub API rate limiting
- **Features**:
  - Logs current rate limit status (remaining/total)
  - Warns when rate limit is exceeded
  - Calculates time until rate limit reset
  - Helps prevent and debug rate limit issues

### 4. Updated Auth Repository (`AuthRepositoryImpl.kt`)

- **Security**: Uses `EncryptedSharedPreferences` for secure token storage
- **Features**:
  - OAuth token exchange implementation
  - Secure token persistence using Android Keystore
  - Token retrieval and management
  - Authentication state tracking with Flow

**Key Security Features**:
- Uses `MasterKey` with AES256_GCM encryption
- Token encrypted at rest using Android Security library
- Prevents token exposure in logs or memory dumps

### 5. Network Module Updates (`NetworkModule.kt`)

- **New Qualifiers**: `@ApiClient` and `@OAuthClient` for different HTTP clients
- **API Client**: Includes AuthInterceptor and RateLimitInterceptor
- **OAuth Client**: Separate client for token exchange (no auth headers)
- **Providers**:
  - `GitHubApiService` - Main GitHub API
  - `GitHubOAuthService` - OAuth token exchange

### 6. MainActivity Updates (`MainActivity.kt`)

- **Deep Link Handling**: Processes `issuetrax://oauth` callback
- **OAuth Code Extraction**: Extracts authorization code from callback URI
- **Callback Registration**: Connects OAuth flow with AuthScreen

### 7. AuthScreen Updates (`AuthScreen.kt`)

- **Custom Tabs Integration**: Opens GitHub OAuth in Chrome Custom Tabs
- **OAuth URL Construction**: Builds authorization URL with client ID, redirect URI, and scopes
- **Callback Handling**: Registers callback to receive authorization code

### 8. Navigation Updates (`NavGraph.kt`)

- **OAuth Callback Prop**: Passes OAuth callback registration through navigation
- **Screen Integration**: Connects MainActivity callbacks to AuthScreen

## OAuth Flow

```
1. User clicks "Sign in with GitHub" button
   ↓
2. App opens Custom Tab with GitHub OAuth URL
   ↓
3. User authenticates and authorizes on GitHub
   ↓
4. GitHub redirects to issuetrax://oauth?code=<auth_code>
   ↓
5. MainActivity receives callback and extracts code
   ↓
6. AuthViewModel calls authenticate(code)
   ↓
7. AuthRepository exchanges code for access token
   ↓
8. Token is encrypted and stored in EncryptedSharedPreferences
   ↓
9. User is navigated to CurrentWorkScreen
```

## Configuration Required

### 1. GitHub OAuth App Registration

Register an OAuth app at: https://github.com/settings/developers

- **Application name**: Issuetrax
- **Homepage URL**: Your app homepage
- **Authorization callback URL**: `issuetrax://oauth`

### 2. Update Build Configuration

In `app/build.gradle.kts`, update:

```kotlin
buildConfigField("String", "GITHUB_CLIENT_ID", "\"your_actual_client_id\"")
buildConfigField("String", "GITHUB_REDIRECT_URI", "\"issuetrax://oauth\"")
```

### 3. Add Client Secret

In `AuthRepositoryImpl.kt`, update:

```kotlin
private const val GITHUB_CLIENT_SECRET = "your_actual_client_secret"
```

**Important**: For production, store the client secret securely (e.g., in a backend service or use GitHub's Device Flow for native apps).

## Security Considerations

1. **Token Storage**: Tokens are encrypted using Android Keystore
2. **Network Security**: Certificate pinning is configured for api.github.com
3. **HTTPS Only**: All API calls use HTTPS
4. **No Cleartext Traffic**: Network security config prevents cleartext traffic
5. **Token Exposure**: Tokens never logged or exposed in UI

## Dependencies Used

- `androidx.security:security-crypto` - For EncryptedSharedPreferences
- `androidx.browser:browser` - For Custom Tabs
- `com.squareup.retrofit2:retrofit` - For API calls
- `com.squareup.okhttp3:okhttp` - For HTTP client
- `kotlinx-serialization` - For JSON parsing

## Testing Recommendations

1. **Unit Tests**: Test AuthRepositoryImpl token exchange logic
2. **Integration Tests**: Test OAuth flow end-to-end
3. **Security Tests**: Verify token encryption and secure storage
4. **Rate Limit Tests**: Test behavior under rate limiting

## Rate Limiting

GitHub API has rate limits:
- **Authenticated**: 5,000 requests per hour
- **Unauthenticated**: 60 requests per hour

The `RateLimitInterceptor` monitors usage and logs warnings when limits are approached.

## Future Enhancements

1. Implement token refresh mechanism (GitHub tokens don't expire but may be revoked)
2. Add offline token validation
3. Implement OAuth Device Flow for better security
4. Add multiple account support
5. Implement biometric authentication for token access

## References

- [GitHub OAuth Documentation](https://docs.github.com/en/developers/apps/building-oauth-apps)
- [Android Security Library](https://developer.android.com/topic/security/data)
- [Custom Tabs Documentation](https://developer.chrome.com/docs/android/custom-tabs/)
