package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.entity.CommitStatus
import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case for fetching commit status checks.
 */
class GetCommitStatusUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Gets the combined status of all status checks for a commit.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param ref Git reference (commit SHA, branch, tag)
     * @return Result with CommitStatus or error
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        ref: String
    ): Result<CommitStatus> {
        return repository.getCommitStatus(owner, repo, ref)
    }
}
