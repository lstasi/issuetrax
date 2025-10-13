# Migration from OAuth to Personal Access Token Authentication

## Summary

This document describes the changes made to switch from OAuth-based authentication to Personal Access Token (PAT) authentication in response to the requirement to avoid backend server dependencies.

## Why the Change?

**Problem with OAuth**: The standard OAuth 2.0 authorization code flow requires a client secret, which must be stored securely on a backend server. Embedding it in a mobile app is a security risk as it can be extracted through reverse engineering.

**Solution**: Use GitHub Personal Access Tokens that users create and manage themselves. This eliminates the need for any backend infrastructure while maintaining security.

## Changes Made

### Removed Components

1. **GitHubOAuthService.kt** - No longer needed for token exchange
2. **OAuth Retrofit Client** - Separate client for OAuth endpoints removed
3. **OAuth Callback Handling** - Deep link handlers in MainActivity removed
4. **Build Configuration** - Removed GITHUB_CLIENT_ID and GITHUB_REDIRECT_URI
5. **Manifest Intent Filter** - Removed `issuetrax://oauth` deep link handler

### Modified Components

1. **AuthRepositoryImpl.kt**
   - Removed GitHubOAuthService dependency
   - Simplified `authenticate()` to directly validate and store tokens
   - Removed OAuth token exchange logic
   - Kept EncryptedSharedPreferences for secure storage

2. **AuthScreen.kt**
   - Added OutlinedTextField for token input
   - Added password visual transformation for security
   - Added "Create Token on GitHub" helper button
   - Removed Custom Tabs OAuth launch logic
   - Added inline instructions for users

3. **MainActivity.kt**
   - Removed OAuth callback handling
   - Removed onNewIntent override
   - Simplified to basic activity setup

4. **NavGraph.kt**
   - Removed OAuth callback registration parameter
   - Simplified to standard navigation flow

5. **NetworkModule.kt**
   - Removed @OAuthClient qualifier
   - Removed separate OAuth Retrofit instance
   - Simplified to single OkHttpClient configuration

6. **AuthInterceptor.kt**
   - Removed OAuth endpoint check
   - Simplified to standard auth header injection

### Updated Documentation

- **GITHUB_OAUTH_IMPLEMENTATION.md** → **GITHUB_AUTHENTICATION.md**
  - Completely rewrote to describe PAT-based authentication
  - Added user instructions for creating tokens
  - Added security considerations specific to PAT approach
  - Added advantages of PAT over OAuth section

## Benefits of This Approach

1. ✅ **No Backend Required** - Zero infrastructure costs
2. ✅ **Better Security** - No client secret exposure risk
3. ✅ **Simpler Code** - 100 lines of code removed
4. ✅ **User Control** - Users can revoke/recreate tokens anytime
5. ✅ **Faster Development** - No need to maintain OAuth infrastructure
6. ✅ **Easier Setup** - Users just need to create a token

## User Experience Flow

### Before (OAuth)
1. Click "Sign in with GitHub"
2. Browser opens to GitHub authorization page
3. User authorizes app
4. Redirect back to app with code
5. App exchanges code for token
6. Token stored and user authenticated

### After (PAT)
1. Click "Create Token on GitHub" (optional)
2. Browser opens to GitHub token creation page
3. User creates token with required scopes
4. User copies token
5. User pastes token into app
6. Token validated and stored, user authenticated

## Migration Guide for Users

When users update to this version:

1. They will be logged out (old OAuth tokens are different format)
2. On the login screen, they'll see the new token input field
3. They need to:
   - Click "Create Token on GitHub" or go to github.com/settings/tokens
   - Create a new Personal Access Token with `repo` and `user` scopes
   - Copy the token
   - Paste it into the app
   - Click "Sign in"

## Security Considerations

Both approaches are secure, but PAT has advantages:

| Aspect | OAuth | Personal Access Token |
|--------|-------|----------------------|
| Client Secret Storage | Backend required | Not needed |
| Token Storage | EncryptedSharedPreferences | EncryptedSharedPreferences |
| Token Exposure Risk | Low (if backend secure) | Low (encrypted on device) |
| Revocation | User or app can revoke | User can revoke |
| Setup Complexity | High | Low |
| Code Decompilation Risk | High (secret exposed) | None (no secret) |

## Code Statistics

- **Files Modified**: 9
- **Files Deleted**: 2 (GitHubOAuthService, old documentation)
- **Files Added**: 1 (new documentation)
- **Net Lines Changed**: -100 lines (simpler implementation)

## Testing Recommendations

1. Test token input with valid PAT
2. Test token input with invalid/empty input
3. Test "Create Token on GitHub" button
4. Test token persistence across app restarts
5. Test API calls with stored token
6. Test error handling for invalid tokens

## Rollback Plan

If needed to rollback:
1. Restore commit before 1b9fcf1
2. Users would need to re-authenticate with OAuth flow
3. No data loss (stored data is forward compatible)

## References

- Commit: 1b9fcf1 - Main implementation
- Commit: 59f1d3a - Configuration cleanup
- Documentation: GITHUB_AUTHENTICATION.md
