package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPullRequestsUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository
) {
    suspend operator fun invoke(
        owner: String,
        repo: String,
        state: String = "open"
    ): Flow<Result<List<PullRequest>>> {
        return gitHubRepository.getPullRequests(owner, repo, state)
    }
}