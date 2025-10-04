package com.issuetrax.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.issuetrax.app.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthRepository {
    
    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val ACCESS_TOKEN_KEY = "access_token"
    }
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    private val _isAuthenticatedFlow = MutableStateFlow(encryptedPrefs.getString(ACCESS_TOKEN_KEY, null) != null)
    
    override suspend fun authenticate(token: String): Result<String> {
        return try {
            // Validate token is not empty
            if (token.isBlank()) {
                return Result.failure(Exception("Token cannot be empty"))
            }
            
            // Save the token directly (it's a Personal Access Token from GitHub)
            saveAccessToken(token)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun saveAccessToken(token: String) {
        encryptedPrefs.edit().putString(ACCESS_TOKEN_KEY, token).apply()
        _isAuthenticatedFlow.value = true
    }
    
    override suspend fun getAccessToken(): String? {
        return encryptedPrefs.getString(ACCESS_TOKEN_KEY, null)
    }
    
    override suspend fun clearAccessToken() {
        encryptedPrefs.edit().remove(ACCESS_TOKEN_KEY).apply()
        _isAuthenticatedFlow.value = false
    }
    
    override fun isAuthenticated(): Flow<Boolean> {
        return _isAuthenticatedFlow.asStateFlow()
    }
    
    override suspend fun refreshToken(): Result<String> {
        // GitHub Personal Access Tokens don't expire (unless configured to do so)
        // Just return the current token
        val currentToken = getAccessToken()
        return if (currentToken != null) {
            Result.success(currentToken)
        } else {
            Result.failure(Exception("No token to refresh"))
        }
    }
}