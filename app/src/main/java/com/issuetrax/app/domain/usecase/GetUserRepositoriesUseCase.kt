package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.entity.Repository
import com.issuetrax.app.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserRepositoriesUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository
) {
    suspend operator fun invoke(): Flow<Result<List<Repository>>> {
        return gitHubRepository.getUserRepositories()
    }
}
