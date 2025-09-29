package com.issuetrax.app.presentation.navigation

sealed class Routes(val route: String) {
    object Auth : Routes("auth")
    object CurrentWork : Routes("current_work")
    object PRReview : Routes("pr_review/{owner}/{repo}/{prNumber}") {
        fun createRoute(owner: String, repo: String, prNumber: Int): String {
            return "pr_review/$owner/$repo/$prNumber"
        }
    }
}