# GitHub Personal Access Token Authentication

This document describes the GitHub authentication implementation for the Issuetrax application.

## Overview

The GitHub authentication enables users to securely access GitHub APIs using Personal Access Tokens (PAT). This approach avoids the need for a backend server to manage OAuth secrets, making the app fully standalone. The implementation follows Android security best practices for token storage.

## Why Personal Access Tokens?

**Backend-Free Architecture**: Traditional OAuth flows require storing a client secret, which must be kept secure on a backend server. Using Personal Access Tokens eliminates this requirement, allowing the app to remain fully standalone without any backend infrastructure.

**User Control**: Users can create, revoke, and manage tokens directly from their GitHub account settings, providing full control over app access.

**Security**: Tokens are stored encrypted on the device using Android Keystore, ensuring they remain secure even if the device is compromised.

## Components Implemented

### 1. Authentication Interceptor (`AuthInterceptor.kt`)

- **Purpose**: Automatically adds OAuth tokens to API requests
- **Features**:
  - Injects `Authorization: Bearer <token>` header to all GitHub API requests
  - Skips injection for OAuth token exchange endpoint
  - Retrieves token from AuthRepository

### 2. Rate Limit Interceptor (`RateLimitInterceptor.kt`)

- **Purpose**: Monitors and logs GitHub API rate limiting
- **Features**:
  - Logs current rate limit status (remaining/total)
  - Warns when rate limit is exceeded
  - Calculates time until rate limit reset
  - Helps prevent and debug rate limit issues

### 3. Auth Repository (`AuthRepositoryImpl.kt`)

- **Security**: Uses `EncryptedSharedPreferences` for secure token storage
- **Features**:
  - Direct token validation and storage
  - Secure token persistence using Android Keystore
  - Token retrieval and management
  - Authentication state tracking with Flow

**Key Security Features**:
- Uses `MasterKey` with AES256_GCM encryption
- Token encrypted at rest using Android Security library
- Prevents token exposure in logs or memory dumps

### 4. Network Module (`NetworkModule.kt`)

- **Configuration**: Single OkHttp client with proper interceptor chain
- **Interceptors**: Auth → RateLimit → Logging
- **Providers**:
  - `GitHubApiService` - Main GitHub API interface

### 5. AuthScreen (`AuthScreen.kt`)

- **Token Input**: TextField for users to paste their Personal Access Token
- **Password Protection**: Token field uses password visual transformation
- **Helper Link**: Button to open GitHub token creation page with pre-configured scopes
- **Validation**: Ensures token is not empty before submission

### 6. Simple Navigation

- **Clean Flow**: Removed OAuth callback complexity
- **Direct Authentication**: User enters token and is immediately authenticated

## Authentication Flow

```
1. User opens the app and sees the login screen
   ↓
2. User clicks "Create Token on GitHub" button (optional)
   ↓
3. Browser opens to GitHub token creation page with pre-configured scopes
   ↓
4. User creates token on GitHub and copies it
   ↓
5. User pastes token into the app's token field
   ↓
6. User clicks "Sign in" button
   ↓
7. Token is validated (non-empty check)
   ↓
8. Token is encrypted and stored in EncryptedSharedPreferences
   ↓
9. User is navigated to CurrentWorkScreen
```

## User Setup Instructions

### Creating a Personal Access Token

Users need to create a Personal Access Token from their GitHub account:

1. Go to GitHub Settings: https://github.com/settings/tokens
2. Click "Generate new token" → "Generate new token (classic)"
3. Give it a descriptive name (e.g., "Issuetrax Android App")
4. Select the following scopes:
   - `repo` - Full control of private repositories
   - `user` - Read user profile data
5. Click "Generate token"
6. **Important**: Copy the token immediately (it won't be shown again)
7. Paste the token into the Issuetrax app

**Note**: The app provides a convenient button that opens the token creation page with pre-configured scopes.

## Security Considerations

1. **Token Storage**: Tokens are encrypted using Android Keystore with AES256_GCM
2. **Password Field**: Token input uses password visual transformation to prevent shoulder surfing
3. **Network Security**: Certificate pinning is configured for api.github.com
4. **HTTPS Only**: All API calls use HTTPS
5. **No Cleartext Traffic**: Network security config prevents cleartext traffic
6. **Token Exposure**: Tokens never logged or exposed in UI
7. **No Backend Required**: Eliminates the risk of client secret exposure through app decompilation
8. **User Control**: Users can revoke tokens at any time from GitHub settings

## Dependencies Used

- `androidx.security:security-crypto` - For EncryptedSharedPreferences
- `com.squareup.retrofit2:retrofit` - For API calls
- `com.squareup.okhttp3:okhttp` - For HTTP client
- `kotlinx-serialization` - For JSON parsing
- `androidx.compose.material3` - For UI components (TextField, Button, etc.)

## Testing Recommendations

1. **Unit Tests**: Test AuthRepositoryImpl token storage and retrieval logic
2. **UI Tests**: Test authentication screen with valid and invalid tokens
3. **Security Tests**: Verify token encryption and secure storage
4. **Rate Limit Tests**: Test behavior under rate limiting
5. **Integration Tests**: Test full authentication flow with real GitHub API

## Rate Limiting

GitHub API has rate limits:
- **Authenticated**: 5,000 requests per hour
- **Unauthenticated**: 60 requests per hour

The `RateLimitInterceptor` monitors usage and logs warnings when limits are approached.

## Future Enhancements

1. Add token validation by making a test API call to verify the token works
2. Implement biometric authentication to protect stored token access
3. Add token expiration reminder (for fine-grained tokens with expiration)
4. Add multiple account support with account switching
5. Display token permissions/scopes in the UI
6. Add token health check on app startup

## Advantages Over OAuth Flow

1. **No Backend Required**: Eliminates infrastructure costs and maintenance
2. **Simpler Implementation**: Less code, fewer moving parts
3. **Better Security**: No risk of client secret exposure
4. **User Control**: Users can revoke and recreate tokens as needed
5. **Offline Setup**: Users can create tokens before app installation
6. **No Deep Links**: Simpler Android manifest configuration

## References

- [GitHub Personal Access Tokens](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
- [Android Security Library](https://developer.android.com/topic/security/data)
- [GitHub API Authentication](https://docs.github.com/en/rest/overview/authenticating-to-the-rest-api)
