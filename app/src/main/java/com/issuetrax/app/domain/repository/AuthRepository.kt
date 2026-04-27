package com.issuetrax.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun authenticate(authCode: String): Result<String>

    suspend fun saveAccessToken(token: String)

    suspend fun getAccessToken(): String?

    fun getAccessTokenBlocking(): String?

    suspend fun clearAccessToken()

    fun isAuthenticated(): Flow<Boolean>
}