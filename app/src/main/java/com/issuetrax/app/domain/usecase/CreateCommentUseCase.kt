package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case for creating a comment on a pull request or issue.
 */
class CreateCommentUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Creates a comment on the specified issue/PR.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param issueNumber Issue or PR number
     * @param body Comment body text
     * @return Result with success or error
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        issueNumber: Int,
        body: String
    ): Result<Unit> {
        return repository.createIssueComment(owner, repo, issueNumber, body)
    }
}
