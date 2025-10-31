package com.issuetrax.app.domain.usecase

import com.issuetrax.app.domain.entity.Issue
import com.issuetrax.app.domain.repository.GitHubRepository
import javax.inject.Inject

/**
 * Use case for creating a GitHub issue.
 * 
 * Creates a new issue in the specified repository with optional assignees.
 */
class CreateIssueUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    /**
     * Creates a new issue.
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param title Issue title
     * @param body Issue body/description
     * @param assignees List of GitHub usernames to assign
     * @return Result containing the created Issue or failure
     */
    suspend operator fun invoke(
        owner: String,
        repo: String,
        title: String,
        body: String?,
        assignees: List<String> = emptyList()
    ): Result<Issue> {
        return repository.createIssue(owner, repo, title, body, assignees)
    }
}
