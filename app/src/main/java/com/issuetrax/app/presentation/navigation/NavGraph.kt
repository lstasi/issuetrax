package com.issuetrax.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.issuetrax.app.presentation.ui.auth.AuthScreen
import com.issuetrax.app.presentation.ui.current_work.CurrentWorkScreen
import com.issuetrax.app.presentation.ui.pr_review.PRReviewScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Routes.Auth.route
    ) {
        composable(Routes.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Routes.CurrentWork.route) {
                        popUpTo(Routes.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.CurrentWork.route) {
            CurrentWorkScreen(
                onNavigateToPR = { owner, repo, prNumber ->
                    navController.navigate(Routes.PRReview.createRoute(owner, repo, prNumber))
                }
            )
        }
        
        composable(Routes.PRReview.route) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: ""
            val repo = backStackEntry.arguments?.getString("repo") ?: ""
            val prNumber = backStackEntry.arguments?.getString("prNumber")?.toIntOrNull() ?: 0
            
            PRReviewScreen(
                owner = owner,
                repo = repo,
                prNumber = prNumber,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}