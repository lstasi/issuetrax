package com.issuetrax.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.issuetrax.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AuthRepository {
    
    companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }
    
    override suspend fun authenticate(authCode: String): Result<String> {
        // TODO: Implement OAuth token exchange with GitHub
        // For now, we'll assume authCode is already the access token
        return Result.success(authCode)
    }
    
    override suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }
    
    override suspend fun getAccessToken(): String? {
        return dataStore.data.first()[ACCESS_TOKEN_KEY]
    }
    
    override suspend fun clearAccessToken() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
        }
    }
    
    override fun isAuthenticated(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] != null
        }
    }
    
    override suspend fun refreshToken(): Result<String> {
        // TODO: Implement token refresh logic
        val currentToken = getAccessToken()
        return if (currentToken != null) {
            Result.success(currentToken)
        } else {
            Result.failure(Exception("No token to refresh"))
        }
    }
}