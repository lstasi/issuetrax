package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.AuthRepository
import javax.inject.Inject

class AuthenticateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(authCode: String): Result<Unit> {
        return try {
            val tokenResult = authRepository.authenticate(authCode)
            if (tokenResult.isSuccess) {
                val token = tokenResult.getOrThrow()
                authRepository.saveAccessToken(token)
                Result.success(Unit)
            } else {
                Result.failure(tokenResult.exceptionOrNull() ?: Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}