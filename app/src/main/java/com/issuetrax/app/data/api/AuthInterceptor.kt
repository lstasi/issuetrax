package com.issuetrax.app.data.api

import com.issuetrax.app.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Skip adding auth header if it already exists or for OAuth token exchange
        if (request.header("Authorization") != null || 
            request.url.encodedPath.contains("/login/oauth/access_token")) {
            return chain.proceed(request)
        }
        
        val token = runBlocking {
            authRepository.getAccessToken()
        }
        
        val newRequest = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        
        return chain.proceed(newRequest)
    }
}
