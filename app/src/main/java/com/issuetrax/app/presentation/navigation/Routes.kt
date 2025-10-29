package com.issuetrax.app.presentation.navigation

sealed class Routes(val route: String) {
    object Auth : Routes("auth")
    object RepositorySelection : Routes("repository_selection")
    object CurrentWork : Routes("current_work/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String): String {
            return "current_work/$owner/$repo"
        }
    }
    object PRReview : Routes("pr_review/{owner}/{repo}/{prNumber}") {
        fun createRoute(owner: String, repo: String, prNumber: Int): String {
            return "pr_review/$owner/$repo/$prNumber"
        }
    }
    object CreateIssue : Routes("create_issue/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String): String {
            return "create_issue/$owner/$repo"
        }
    }
}